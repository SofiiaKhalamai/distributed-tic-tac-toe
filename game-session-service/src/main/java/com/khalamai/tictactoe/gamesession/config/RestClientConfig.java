package com.khalamai.tictactoe.gamesession.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClientCustomizer gameEngineTimeoutCustomizer(
            @Value("${game-engine.connect-timeout:3s}") Duration connectTimeout,
            @Value("${game-engine.read-timeout:5s}") Duration readTimeout) {
        var requestFactorySettings = ClientHttpRequestFactorySettings.DEFAULTS
                .withConnectTimeout(connectTimeout)
                .withReadTimeout(readTimeout);

        return builder -> builder.requestFactory(ClientHttpRequestFactories.get(requestFactorySettings));
    }

    @Bean
    public RestClient gameEngineRestClient(RestClient.Builder builder,
                                           @Value("${game-engine.base-url}") String baseUrl) {
        return builder
                .baseUrl(baseUrl)
                .build();
    }
}
