package com.khalamai.tictactoe.gamesession.service.impl;

import com.khalamai.tictactoe.gamesession.client.EngineCell;
import com.khalamai.tictactoe.gamesession.client.EngineGameResponse;
import com.khalamai.tictactoe.gamesession.client.EngineMoveRequest;
import com.khalamai.tictactoe.gamesession.client.GameEngineClient;
import com.khalamai.tictactoe.gamesession.domain.entity.Session;
import com.khalamai.tictactoe.gamesession.domain.entity.SessionMove;
import com.khalamai.tictactoe.gamesession.domain.exception.SessionAlreadySimulatedException;
import com.khalamai.tictactoe.gamesession.domain.exception.SessionNotFoundException;
import com.khalamai.tictactoe.gamesession.domain.enums.SessionStatus;
import com.khalamai.tictactoe.common.domain.enums.GameStatus;
import com.khalamai.tictactoe.common.domain.enums.Symbol;
import com.khalamai.tictactoe.gamesession.dto.SessionResponse;
import com.khalamai.tictactoe.gamesession.repository.SessionRepository;
import com.khalamai.tictactoe.gamesession.service.SessionService;
import com.khalamai.tictactoe.gamesession.service.mapper.SessionResponseMapper;
import com.khalamai.tictactoe.gamesession.service.MoveGeneratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {

    private static final int MAX_MOVES = 9;

    // fixed-size lock striping instead of a per-sessionId map: bounded memory (no cleanup needed as
    // sessions accumulate), at the cost of rare, harmless collisions where two unrelated sessions
    // share a stripe and briefly serialize instead of running fully in parallel.
    private static final int LOCK_STRIPES = 32;

    private final SessionRepository sessionRepository;
    private final GameEngineClient gameEngineClient;
    private final MoveGeneratorService moveGeneratorService;
    private final SessionResponseMapper sessionResponseMapper;

    private final List<ReentrantLock> sessionLocks = Stream.generate(ReentrantLock::new)
            .limit(LOCK_STRIPES)
            .toList();

    @Override
    @Transactional
    public SessionResponse createSession() {
        var session = new Session(UUID.randomUUID().toString());
        sessionRepository.save(session);

        return sessionResponseMapper.toResponse(session);
    }

    @Override
    @Transactional
    public SessionResponse simulate(String sessionId) {
        var lock = lockFor(sessionId);
        lock.lock();
        try {
            return runSimulation(sessionId);
        } finally {
            lock.unlock();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public SessionResponse getSession(String sessionId) {
        return sessionResponseMapper.toResponse(findSessionOrThrow(sessionId));
    }

    private ReentrantLock lockFor(String sessionId) {
        var stripe = Math.floorMod(sessionId.hashCode(), LOCK_STRIPES);
        return sessionLocks.get(stripe);
    }

    private SessionResponse runSimulation(String sessionId) {
        var session = findSessionOrThrow(sessionId);
        if (session.getStatus() == SessionStatus.COMPLETED) {
            throw new SessionAlreadySimulatedException(sessionId);
        }
        session.setStatus(SessionStatus.SIMULATING);

        var current = resyncWithEngine(sessionId, session);
        var movesPlayed = current.board().size();

        while (!current.status().isFinished() && movesPlayed < MAX_MOVES) {
            current = playMove(sessionId, session, current);
            movesPlayed++;
        }

        session.setStatus(SessionStatus.COMPLETED);
        sessionRepository.save(session);
        return sessionResponseMapper.toResponse(session);
    }

    private EngineGameResponse resyncWithEngine(String sessionId, Session session) {
        return gameEngineClient.getGame(sessionId)
                .orElseGet(() -> new EngineGameResponse(sessionId, Collections.emptyList(), session.getGameStatus(), Symbol.X));
    }

    private EngineGameResponse playMove(String sessionId, Session session, EngineGameResponse current) {
        var move = moveGeneratorService.nextMove(toOccupiedCells(current.board()));
        var symbol = current.nextTurn();
        var updated = gameEngineClient.makeMove(sessionId, new EngineMoveRequest(symbol, move.row(), move.col()));
        recordMove(session, symbol, move.row(), move.col(), updated.status());
        return updated;
    }

    private Session findSessionOrThrow(String sessionId) {
        return sessionRepository.findById(sessionId).orElseThrow(() -> new SessionNotFoundException(sessionId));
    }

    private static void recordMove(Session session, Symbol symbol, int row, int col, GameStatus gameStatus) {
        session.getMoveHistory().add(new SessionMove(session, symbol, row, col));
        session.setGameStatus(gameStatus);
    }

    private static List<MoveGeneratorService.Cell> toOccupiedCells(List<EngineCell> cells) {
        return cells.stream()
                .map(cell -> new MoveGeneratorService.Cell(cell.row(), cell.col()))
                .toList();
    }
}
