package com.khalamai.tictactoe.gamesession;

import com.khalamai.tictactoe.common.domain.enums.GameStatus;
import com.khalamai.tictactoe.gameengine.GameEngineServiceApplication;
import com.khalamai.tictactoe.gamesession.domain.enums.SessionStatus;
import com.khalamai.tictactoe.gamesession.service.SessionService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

// boots a real game-engine-service instance (random port, isolated H2 schema) and drives
// session-service against it over actual HTTP - unlike SessionServiceIntegrationTest, which
// stubs GameEngineClient, this exercises the real wire contract between the two services.
@SpringBootTest
class CrossServiceIntegrationTest {

    private static final ConfigurableApplicationContext ENGINE_CONTEXT = startRealGameEngine();

    private static ConfigurableApplicationContext startRealGameEngine() {
        return new SpringApplicationBuilder(GameEngineServiceApplication.class)
                .run(
                        "--server.port=0",
                        "--spring.application.name=game-engine-service-cross-test",
                        "--spring.datasource.url=jdbc:h2:mem:cross-service-engine;DB_CLOSE_DELAY=-1");
    }

    @DynamicPropertySource
    static void pointSessionAtTheRealEngine(DynamicPropertyRegistry registry) {
        registry.add("game-engine.base-url", () -> "http://localhost:" + enginePort());
    }

    private static int enginePort() {
        return ((ServletWebServerApplicationContext) ENGINE_CONTEXT).getWebServer().getPort();
    }

    @AfterAll
    static void stopRealGameEngine() {
        ENGINE_CONTEXT.close();
    }

    @Autowired
    private SessionService sessionService;

    @Test
    void shouldPlayFullSessionAgainstARealRunningEngineService() {
        var created = sessionService.createSession();
        assertThat(created.status()).isEqualTo(SessionStatus.CREATED);

        var result = sessionService.simulate(created.sessionId());

        assertThat(result.status()).isEqualTo(SessionStatus.COMPLETED);
        assertThat(result.gameStatus()).isNotEqualTo(GameStatus.IN_PROGRESS);
        assertThat(result.moveHistory()).isNotEmpty();
        assertThat(result.board()).isNotEmpty();

        var fetched = sessionService.getSession(created.sessionId());
        assertThat(fetched.moveHistory()).isEqualTo(result.moveHistory());
    }
}
