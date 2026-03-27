package com.campusmarket.backend.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ProductAiWebClientConfig {

    @Bean
    public WebClient productAiWebClient(
            @Value("https://watertrue-campusmarket-ai.hf.space") String aiBaseUrl
    ) {
        return WebClient.builder()
                .baseUrl(aiBaseUrl)
                .build();
    }
}