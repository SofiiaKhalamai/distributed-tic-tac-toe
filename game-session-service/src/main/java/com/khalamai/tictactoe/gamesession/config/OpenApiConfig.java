package com.khalamai.tictactoe.gamesession.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI gameSessionOpenApi() {
        return new OpenAPI().info(new Info()
                .title("Game Session Service API")
                .description("Manages game sessions and automates moves by coordinating with the Game Engine Service")
                .version("v1"));
    }
}
