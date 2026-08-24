package com.khalamai.tictactoe.gamesession.client;

import com.khalamai.tictactoe.gamesession.domain.exception.GameEngineCommunicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Optional;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class GameEngineClient {

    private final RestClient restClient;

    public EngineGameResponse makeMove(String gameId, EngineMoveRequest request) {
        return execute(() -> restClient.post()
                .uri("/games/{gameId}/move", gameId)
                .body(request)
                .retrieve()
                .body(EngineGameResponse.class));
    }

    public Optional<EngineGameResponse> getGame(String gameId) {
        try {
            return Optional.ofNullable(restClient.get()
                    .uri("/games/{gameId}", gameId)
                    .retrieve()
                    .body(EngineGameResponse.class));
        } catch (HttpClientErrorException.NotFound ex) {
            // expected: the engine creates games lazily on first move
            return Optional.empty();
        } catch (RestClientException ex) {
            throw new GameEngineCommunicationException("Failed to communicate with game-engine-service", ex);
        }
    }

    private EngineGameResponse execute(Supplier<EngineGameResponse> call) {
        try {
            return call.get();
        } catch (RestClientException ex) {
            throw new GameEngineCommunicationException("Failed to communicate with game-engine-service", ex);
        }
    }
}
