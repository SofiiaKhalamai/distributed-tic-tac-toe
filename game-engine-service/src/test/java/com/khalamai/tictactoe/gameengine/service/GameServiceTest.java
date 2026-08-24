package com.khalamai.tictactoe.gameengine.service;

import com.khalamai.tictactoe.gameengine.domain.entity.Game;
import com.khalamai.tictactoe.gameengine.domain.exception.GameAlreadyFinishedException;
import com.khalamai.tictactoe.gameengine.domain.exception.GameNotFoundException;
import com.khalamai.tictactoe.gameengine.domain.exception.InvalidMoveException;
import com.khalamai.tictactoe.common.domain.enums.GameStatus;
import com.khalamai.tictactoe.common.domain.enums.Symbol;
import com.khalamai.tictactoe.gameengine.dto.GameResponse;
import com.khalamai.tictactoe.gameengine.dto.MoveRequest;
import com.khalamai.tictactoe.gameengine.repository.GameRepository;
import com.khalamai.tictactoe.gameengine.service.GameService;
import com.khalamai.tictactoe.gameengine.service.impl.BoardServiceImpl;
import com.khalamai.tictactoe.gameengine.service.impl.GameServiceImpl;
import com.khalamai.tictactoe.gameengine.service.impl.WinDetectorServiceImpl;
import com.khalamai.tictactoe.gameengine.service.mapper.GameResponseMapperImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GameServiceTest {

    @Mock
    private GameRepository gameRepository;

    @Mock
    private TransactionTemplate transactionTemplate;

    private GameService gameService;

    @BeforeEach
    void setUp() {
        var boardService = new BoardServiceImpl();
        gameService = new GameServiceImpl(
                gameRepository, boardService, new WinDetectorServiceImpl(boardService), new GameResponseMapperImpl(), transactionTemplate);
    }

    @Test
    void shouldAutoCreateGameOnFirstMove() {
        runTransactionsDirectly();
        when(gameRepository.findById("g1")).thenReturn(Optional.empty());
        when(gameRepository.save(any(Game.class))).thenAnswer(invocation -> invocation.getArgument(0));

        GameResponse response = gameService.applyMove("g1", new MoveRequest(Symbol.X, 0, 0));

        assertThat(response.status()).isEqualTo(GameStatus.IN_PROGRESS);
        assertThat(response.nextTurn()).isEqualTo(Symbol.O);
    }

    @Test
    void shouldRejectMoveOnOccupiedCell() {
        runTransactionsDirectly();
        stubExistingGame(new Game("g2"));
        gameService.applyMove("g2", new MoveRequest(Symbol.X, 0, 0));

        assertThatThrownBy(() -> gameService.applyMove("g2", new MoveRequest(Symbol.O, 0, 0)))
                .isInstanceOf(InvalidMoveException.class);
    }

    @Test
    void shouldRejectMoveOutOfTurn() {
        runTransactionsDirectly();
        stubExistingGame(new Game("g3"));

        assertThatThrownBy(() -> gameService.applyMove("g3", new MoveRequest(Symbol.O, 0, 0)))
                .isInstanceOf(InvalidMoveException.class);
    }

    @Test
    void shouldDetectWinThroughFullMoveFlow() {
        runTransactionsDirectly();
        stubExistingGame(new Game("g4"));

        gameService.applyMove("g4", new MoveRequest(Symbol.X, 0, 0));
        gameService.applyMove("g4", new MoveRequest(Symbol.O, 1, 0));
        gameService.applyMove("g4", new MoveRequest(Symbol.X, 0, 1));
        gameService.applyMove("g4", new MoveRequest(Symbol.O, 1, 1));
        GameResponse response = gameService.applyMove("g4", new MoveRequest(Symbol.X, 0, 2));

        assertThat(response.status()).isEqualTo(GameStatus.X_WON);
        assertThat(response.nextTurn()).isNull();
    }

    @Test
    void shouldRejectMoveAfterGameFinished() {
        runTransactionsDirectly();
        stubExistingGame(new Game("g5"));
        gameService.applyMove("g5", new MoveRequest(Symbol.X, 0, 0));
        gameService.applyMove("g5", new MoveRequest(Symbol.O, 1, 0));
        gameService.applyMove("g5", new MoveRequest(Symbol.X, 0, 1));
        gameService.applyMove("g5", new MoveRequest(Symbol.O, 1, 1));
        gameService.applyMove("g5", new MoveRequest(Symbol.X, 0, 2));

        assertThatThrownBy(() -> gameService.applyMove("g5", new MoveRequest(Symbol.O, 2, 2)))
                .isInstanceOf(GameAlreadyFinishedException.class);
    }

    @Test
    void shouldRetryAfterLosingOptimisticLockRace() {
        stubExistingGame(new Game("g6"));
        // first attempt loses the race with another concurrent update; the retry should
        // re-run the callback and succeed.
        var attempts = new AtomicInteger();
        when(transactionTemplate.execute(any())).thenAnswer(inv -> {
            if (attempts.getAndIncrement() == 0) {
                throw new ObjectOptimisticLockingFailureException(Game.class, "g6");
            }
            TransactionCallback<GameResponse> callback = inv.getArgument(0);
            return callback.doInTransaction(null);
        });

        GameResponse response = gameService.applyMove("g6", new MoveRequest(Symbol.X, 0, 0));

        assertThat(response.status()).isEqualTo(GameStatus.IN_PROGRESS);
        assertThat(response.nextTurn()).isEqualTo(Symbol.O);
        assertThat(attempts.get()).isEqualTo(2);
    }

    @Test
    void shouldThrowWhenGameNotFound() {
        when(gameRepository.findById(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> gameService.getGame("missing"))
                .isInstanceOf(GameNotFoundException.class);
    }


    // no real Spring transaction manager in this unit test - just run the callback directly,
    // simulating a transaction that always succeeds on the first attempt.
    private void runTransactionsDirectly() {
        when(transactionTemplate.execute(any())).thenAnswer(inv -> {
            TransactionCallback<GameResponse> callback = inv.getArgument(0);
            return callback.doInTransaction(null);
        });
    }

    private void stubExistingGame(Game game) {
        when(gameRepository.findById(game.getGameId())).thenReturn(Optional.of(game));
        lenient().when(gameRepository.save(any(Game.class))).thenAnswer(inv -> inv.getArgument(0));
    }
}
