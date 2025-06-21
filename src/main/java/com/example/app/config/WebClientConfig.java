package com.example.app.config;

import com.example.app.config.AppConfig.AppProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

/**
 * Configuração do WebClient para consumo de APIs REST externas
 */
@Configuration
public class WebClientConfig {

    @Autowired
    private AppProperties appProperties;

    /**
     * WebClient configurado para APIs externas
     */
    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl(appProperties.getExternalApi().getBaseUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.USER_AGENT, "SpringBoot-App/1.0")
                .codecs(configurer -> configurer
                        .defaultCodecs()
                        .maxInMemorySize(1024 * 1024)) // 1MB buffer
                .build();
    }

    /**
     * WebClient configurado especificamente para JSONPlaceholder API
     */
    @Bean("jsonPlaceholderWebClient")
    public WebClient jsonPlaceholderWebClient() {
        return WebClient.builder()
                .baseUrl("https://jsonplaceholder.typicode.com")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .codecs(configurer -> configurer
                        .defaultCodecs()
                        .maxInMemorySize(512 * 1024)) // 512KB buffer
                .build();
    }

    /**
     * WebClient configurado para APIs que requerem autenticação
     */
    @Bean("authenticatedWebClient")
    public WebClient authenticatedWebClient() {
        return WebClient.builder()
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.USER_AGENT, "SpringBoot-App/1.0")
                // Em produção, adicionar token de autenticação aqui
                // .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .codecs(configurer -> configurer
                        .defaultCodecs()
                        .maxInMemorySize(2 * 1024 * 1024)) // 2MB buffer
                .build();
    }
}

