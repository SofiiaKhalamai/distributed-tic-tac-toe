package com.khalamai.tictactoe.gameengine;

import com.khalamai.tictactoe.common.domain.enums.GameStatus;
import com.khalamai.tictactoe.common.domain.enums.Symbol;
import com.khalamai.tictactoe.gameengine.domain.exception.GameNotFoundException;
import com.khalamai.tictactoe.gameengine.dto.MoveRequest;
import com.khalamai.tictactoe.gameengine.service.GameService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class GameServiceIntegrationTest {

    @Autowired
    private GameService gameService;

    @Test
    void shouldPlayFullGameAgainstRealH2Database() {
        gameService.applyMove("integration-1", new MoveRequest(Symbol.X, 0, 0));
        gameService.applyMove("integration-1", new MoveRequest(Symbol.O, 1, 0));
        gameService.applyMove("integration-1", new MoveRequest(Symbol.X, 0, 1));
        gameService.applyMove("integration-1", new MoveRequest(Symbol.O, 1, 1));
        var response = gameService.applyMove("integration-1", new MoveRequest(Symbol.X, 0, 2));

        assertThat(response.status()).isEqualTo(GameStatus.X_WON);
        assertThat(gameService.getGame("integration-1").status()).isEqualTo(GameStatus.X_WON);
    }

    @Test
    void shouldThrowWhenGameNotFound() {
        assertThatThrownBy(() -> gameService.getGame("does-not-exist"))
                .isInstanceOf(GameNotFoundException.class);
    }
}
