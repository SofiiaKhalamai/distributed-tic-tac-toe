package com.khalamai.tictactoe.gameengine.service.impl;

import com.khalamai.tictactoe.gameengine.domain.entity.Game;
import com.khalamai.tictactoe.gameengine.domain.exception.GameAlreadyFinishedException;
import com.khalamai.tictactoe.gameengine.domain.exception.GameNotFoundException;
import com.khalamai.tictactoe.gameengine.domain.exception.InvalidMoveException;
import com.khalamai.tictactoe.gameengine.dto.GameResponse;
import com.khalamai.tictactoe.gameengine.dto.MoveRequest;
import com.khalamai.tictactoe.gameengine.repository.GameRepository;
import com.khalamai.tictactoe.gameengine.service.BoardService;
import com.khalamai.tictactoe.gameengine.service.GameService;
import com.khalamai.tictactoe.gameengine.service.WinDetectorService;
import com.khalamai.tictactoe.gameengine.service.mapper.GameResponseMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameServiceImpl implements GameService {

    private static final int MAX_RETRIES = 5;

    private final GameRepository gameRepository;
    private final BoardService boardService;
    private final WinDetectorService winDetectorService;
    private final GameResponseMapper gameResponseMapper;
    private final TransactionTemplate transactionTemplate;

    @Override
    @Transactional(readOnly = true)
    public GameResponse getGame(String gameId) {
        return gameResponseMapper.toResponse(gameRepository.findById(gameId).orElseThrow(() -> new GameNotFoundException(gameId)));
    }

    @Override
    public GameResponse applyMove(String gameId, MoveRequest request) {
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                return transactionTemplate.execute(status -> attemptMove(gameId, request));
            } catch (ObjectOptimisticLockingFailureException ex) {
                log.warn("Optimistic lock conflict applying move to game {} (attempt {}/{}), retrying", gameId, attempt, MAX_RETRIES);
            } catch (DataIntegrityViolationException ex) {
                // two concurrent first moves both found no existing game and raced to insert it -
                // retry sees the game the other thread just created and applies this move to it
                log.warn("Concurrent game creation race for game {} (attempt {}/{}), retrying", gameId, attempt, MAX_RETRIES);
            }
        }
        log.warn("Exhausted {} retries applying move to game {} due to concurrent updates", MAX_RETRIES, gameId);
        throw new IllegalStateException("Could not apply move to game %s after %d attempts due to concurrent updates".formatted(gameId, MAX_RETRIES));
    }

    private GameResponse attemptMove(String gameId, MoveRequest request) {
        var game = gameRepository.findById(gameId).orElseGet(() -> gameRepository.save(new Game(gameId)));
        validateMove(game, request);
        performMove(game, request);

        gameRepository.save(game);
        return gameResponseMapper.toResponse(game);
    }

    private void validateMove(Game game, MoveRequest request) {
        if (game.getStatus().isFinished()) {
            throw new GameAlreadyFinishedException(game.getGameId());
        }
        if (boardService.isOccupied(game.getCells(), request.row(), request.col())) {
            throw new InvalidMoveException("Cell already occupied: (%d, %d)".formatted(request.row(), request.col()));
        }
        if (request.symbol() != game.getNextTurn()) {
            throw new InvalidMoveException("It is not %s's turn".formatted(request.symbol()));
        }
    }

    private void performMove(Game game, MoveRequest request) {
        var cells = game.getCells();
        boardService.place(cells, request.symbol(), request.row(), request.col());
        game.setStatus(winDetectorService.evaluate(cells, request.symbol()));
        game.setNextTurn(request.symbol().opponent());
    }
}
