package com.khalamai.tictactoe.gameengine;

import com.khalamai.tictactoe.common.domain.enums.Symbol;
import com.khalamai.tictactoe.gameengine.domain.exception.InvalidMoveException;
import com.khalamai.tictactoe.gameengine.dto.MoveRequest;
import com.khalamai.tictactoe.gameengine.service.GameService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class GameServiceConcurrencyIntegrationTest {

    @Autowired
    private GameService gameService;

    @Test
    void shouldNotLoseOrDuplicateAMoveWhenTwoThreadsRaceOnTheSameCell() throws Exception {
        var gameId = "race-" + UUID.randomUUID();
        var barrier = new CyclicBarrier(2);
        var executor = Executors.newFixedThreadPool(2);
        try {
            Callable<Boolean> attemptToPlaceZeroZero = () -> {
                barrier.await();
                try {
                    gameService.applyMove(gameId, new MoveRequest(Symbol.X, 0, 0));
                    return true;
                } catch (InvalidMoveException ex) {
                    // lost the race - after retrying against the fresh version, this thread correctly
                    // saw the cell as already occupied rather than corrupting the board
                    return false;
                }
            };

            var first = executor.submit(attemptToPlaceZeroZero);
            var second = executor.submit(attemptToPlaceZeroZero);

            var results = Stream.of(first.get(5, TimeUnit.SECONDS), second.get(5, TimeUnit.SECONDS)).toList();

            assertThat(results.stream().filter(Boolean::booleanValue)).hasSize(1);

            var finalGame = gameService.getGame(gameId);
            assertThat(finalGame.board()).hasSize(1);
            assertThat(List.of(finalGame.board().get(0).row(), finalGame.board().get(0).col())).containsExactly(0, 0);
        } finally {
            executor.shutdown();
        }
    }
}
