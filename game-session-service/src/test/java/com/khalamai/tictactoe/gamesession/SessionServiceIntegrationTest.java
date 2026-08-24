package com.khalamai.tictactoe.gamesession;

import com.khalamai.tictactoe.gamesession.client.EngineGameResponse;
import com.khalamai.tictactoe.gamesession.client.EngineMoveRequest;
import com.khalamai.tictactoe.gamesession.client.GameEngineClient;
import com.khalamai.tictactoe.common.domain.enums.GameStatus;
import com.khalamai.tictactoe.gamesession.domain.enums.SessionStatus;
import com.khalamai.tictactoe.gamesession.service.SessionService;
import com.khalamai.tictactoe.gamesession.support.EngineBoardFixtures;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
class SessionServiceIntegrationTest {

    @Autowired
    private SessionService sessionService;

    @MockBean
    private GameEngineClient gameEngineClient;

    @Test
    void shouldPlayFullSessionAgainstRealH2Database() {
        var board = EngineBoardFixtures.emptyBoard();

        when(gameEngineClient.makeMove(anyString(), any(EngineMoveRequest.class))).thenAnswer(inv -> {
            String gameId = inv.getArgument(0);
            EngineMoveRequest req = inv.getArgument(1);
            board.set(req.row() * 3 + req.col(), req.symbol());
            var full = board.stream().noneMatch(Objects::isNull);
            var status = full ? GameStatus.DRAW : GameStatus.IN_PROGRESS;
            return new EngineGameResponse(gameId, EngineBoardFixtures.toSparse(board), status, req.symbol().opponent());
        });

        var created = sessionService.createSession();
        var result = sessionService.simulate(created.sessionId());

        assertThat(result.status()).isEqualTo(SessionStatus.COMPLETED);
        assertThat(result.moveHistory()).hasSize(9);
        assertThat(result.gameStatus()).isEqualTo(GameStatus.DRAW);

        var fetched = sessionService.getSession(created.sessionId());
        assertThat(fetched.moveHistory()).hasSize(9);
    }
}
