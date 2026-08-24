package com.khalamai.tictactoe.gamesession.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/sessions/**")
                .allowedOrigins("http://localhost:8083")
                .allowedMethods("GET", "POST");
    }
}
