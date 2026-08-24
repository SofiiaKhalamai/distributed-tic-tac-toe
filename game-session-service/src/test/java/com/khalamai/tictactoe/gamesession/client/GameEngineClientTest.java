package com.khalamai.tictactoe.gamesession.client;

import com.khalamai.tictactoe.common.domain.enums.GameStatus;
import com.khalamai.tictactoe.common.domain.enums.Symbol;
import com.khalamai.tictactoe.gamesession.client.EngineCell;
import com.khalamai.tictactoe.gamesession.client.EngineMoveRequest;
import com.khalamai.tictactoe.gamesession.client.GameEngineClient;
import com.khalamai.tictactoe.gamesession.config.RestClientConfig;
import com.khalamai.tictactoe.gamesession.domain.exception.GameEngineCommunicationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.MockRestServiceServer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.http.HttpMethod.POST;

@RestClientTest({GameEngineClient.class, RestClientConfig.class})
@TestPropertySource(properties = "game-engine.base-url=http://game-engine")
class GameEngineClientTest {

    @Autowired
    private GameEngineClient gameEngineClient;

    @Autowired
    private MockRestServiceServer server;

    @Test
    void shouldSubmitMoveAndParseTheEngineResponse() {
        server.expect(requestTo("http://game-engine/games/s1/move"))
                .andExpect(method(POST))
                .andRespond(withSuccess("""
                        {
                          "gameId": "s1",
                          "board": [{ "row": 0, "col": 0, "symbol": "X" }],
                          "status": "IN_PROGRESS",
                          "nextTurn": "O"
                        }
                        """, MediaType.APPLICATION_JSON));

        var response = gameEngineClient.makeMove("s1", new EngineMoveRequest(Symbol.X, 0, 0));

        assertThat(response.status()).isEqualTo(GameStatus.IN_PROGRESS);
        assertThat(response.nextTurn()).isEqualTo(Symbol.O);
        assertThat(response.board()).containsExactly(new EngineCell(0, 0, Symbol.X));
    }

    @Test
    void shouldWrapEngineFailuresInGameEngineCommunicationException() {
        server.expect(requestTo("http://game-engine/games/s1/move"))
                .andRespond(withServerError());

        assertThatThrownBy(() -> gameEngineClient.makeMove("s1", new EngineMoveRequest(Symbol.X, 0, 0)))
                .isInstanceOf(GameEngineCommunicationException.class)
                .hasMessageContaining("Failed to communicate with game-engine-service");
    }
}
