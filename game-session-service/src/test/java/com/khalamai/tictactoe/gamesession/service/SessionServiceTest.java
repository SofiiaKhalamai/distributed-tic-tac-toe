package com.khalamai.tictactoe.gamesession.service;

import com.khalamai.tictactoe.gamesession.client.EngineGameResponse;
import com.khalamai.tictactoe.gamesession.client.EngineMoveRequest;
import com.khalamai.tictactoe.gamesession.client.GameEngineClient;
import com.khalamai.tictactoe.gamesession.domain.entity.Session;
import com.khalamai.tictactoe.common.domain.enums.GameStatus;
import com.khalamai.tictactoe.gamesession.domain.enums.SessionStatus;
import com.khalamai.tictactoe.gamesession.domain.exception.SessionNotFoundException;
import com.khalamai.tictactoe.gamesession.repository.SessionRepository;
import com.khalamai.tictactoe.gamesession.service.SessionService;
import com.khalamai.tictactoe.gamesession.service.impl.SessionServiceImpl;
import com.khalamai.tictactoe.gamesession.service.mapper.SessionResponseMapperImpl;
import com.khalamai.tictactoe.gamesession.service.impl.RandomMoveGeneratorService;
import com.khalamai.tictactoe.gamesession.support.EngineBoardFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SessionServiceTest {

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private GameEngineClient gameEngineClient;

    private SessionService sessionService;

    @BeforeEach
    void setUp() {
        sessionService = new SessionServiceImpl(
                sessionRepository, gameEngineClient, new RandomMoveGeneratorService(), new SessionResponseMapperImpl());
    }

    @Test
    void shouldCreateSessionWithoutCallingEngine() {
        when(sessionRepository.save(any(Session.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = sessionService.createSession();

        assertThat(response.status()).isEqualTo(SessionStatus.CREATED);
        assertThat(response.gameStatus()).isEqualTo(GameStatus.IN_PROGRESS);
        verifyNoInteractions(gameEngineClient);
    }

    @Test
    void shouldSimulateUntilGameFinishes() {
        var board = EngineBoardFixtures.emptyBoard();
        var session = new Session("s1");
        when(sessionRepository.findById("s1")).thenReturn(Optional.of(session));
        when(sessionRepository.save(any(Session.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(gameEngineClient.makeMove(anyString(), any(EngineMoveRequest.class))).thenAnswer(inv -> {
            EngineMoveRequest req = inv.getArgument(1);
            board.set(req.row() * 3 + req.col(), req.symbol());
            var full = board.stream().noneMatch(Objects::isNull);
            var status = full ? GameStatus.DRAW : GameStatus.IN_PROGRESS;
            return new EngineGameResponse("s1", EngineBoardFixtures.toSparse(board), status, req.symbol().opponent());
        });

        var result = sessionService.simulate("s1");

        assertThat(result.status()).isEqualTo(SessionStatus.COMPLETED);
        assertThat(result.moveHistory()).hasSize(9);
        assertThat(result.gameStatus()).isEqualTo(GameStatus.DRAW);

        var savedSession = ArgumentCaptor.forClass(Session.class);
        org.mockito.Mockito.verify(sessionRepository).save(savedSession.capture());
        assertThat(savedSession.getValue().getMoveHistory()).hasSize(9);
    }

    @Test
    void shouldThrowWhenSessionNotFound() {
        when(sessionRepository.findById(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sessionService.getSession("missing"))
                .isInstanceOf(SessionNotFoundException.class);
    }
}
