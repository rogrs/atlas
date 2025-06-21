package com.example.app.client;

import com.example.app.config.AppConfig.AppProperties;
import com.example.app.dto.ExternalPostDto;
import com.example.app.dto.ExternalUserDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;

/**
 * Serviço cliente REST para consumir APIs externas
 * 
 * Demonstra uso do WebClient com:
 * - Retry automático
 * - Timeout
 * - Cache
 * - Tratamento de erros
 */
@Service
public class ExternalApiClient {

    private static final Logger logger = LoggerFactory.getLogger(ExternalApiClient.class);

    @Autowired
    @Qualifier("jsonPlaceholderWebClient")
    private WebClient jsonPlaceholderWebClient;

    @Autowired
    private WebClient webClient;

    @Autowired
    private AppProperties appProperties;

    /**
     * Buscar todos os posts da API externa (com cache)
     */
    @Cacheable(value = "externalPosts")
    public List<ExternalPostDto> getAllPosts() {
        logger.info("Buscando todos os posts da API externa");
        
        try {
            return jsonPlaceholderWebClient
                    .get()
                    .uri("/posts")
                    .retrieve()
                    .bodyToFlux(ExternalPostDto.class)
                    .timeout(Duration.ofMillis(appProperties.getExternalApi().getTimeout()))
                    .retryWhen(Retry.fixedDelay(appProperties.getExternalApi().getRetryAttempts(), Duration.ofSeconds(1)))
                    .collectList()
                    .doOnSuccess(posts -> logger.info("Encontrados {} posts", posts.size()))
                    .doOnError(error -> logger.error("Erro ao buscar posts: {}", error.getMessage()))
                    .block();
        } catch (Exception e) {
            logger.error("Erro ao buscar posts da API externa", e);
            throw new RuntimeException("Falha ao buscar posts da API externa", e);
        }
    }

    /**
     * Buscar post por ID
     */
    @Cacheable(value = "externalPost", key = "#postId")
    public ExternalPostDto getPostById(Long postId) {
        logger.info("Buscando post por ID: {}", postId);
        
        try {
            return jsonPlaceholderWebClient
                    .get()
                    .uri("/posts/{id}", postId)
                    .retrieve()
                    .onStatus(HttpStatus.NOT_FOUND::equals, 
                            response -> Mono.error(new RuntimeException("Post não encontrado: " + postId)))
                    .bodyToMono(ExternalPostDto.class)
                    .timeout(Duration.ofMillis(appProperties.getExternalApi().getTimeout()))
                    .retryWhen(Retry.fixedDelay(appProperties.getExternalApi().getRetryAttempts(), Duration.ofSeconds(1)))
                    .doOnSuccess(post -> logger.info("Post encontrado: {}", post.getTitle()))
                    .doOnError(error -> logger.error("Erro ao buscar post {}: {}", postId, error.getMessage()))
                    .block();
        } catch (WebClientResponseException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                logger.warn("Post não encontrado: {}", postId);
                return null;
            }
            logger.error("Erro HTTP ao buscar post {}: {}", postId, e.getMessage());
            throw new RuntimeException("Erro ao buscar post da API externa", e);
        } catch (Exception e) {
            logger.error("Erro ao buscar post {} da API externa", postId, e);
            throw new RuntimeException("Falha ao buscar post da API externa", e);
        }
    }

    /**
     * Buscar posts por usuário
     */
    @Cacheable(value = "externalPostsByUser", key = "#userId")
    public List<ExternalPostDto> getPostsByUserId(Long userId) {
        logger.info("Buscando posts do usuário: {}", userId);
        
        try {
            return jsonPlaceholderWebClient
                    .get()
                    .uri("/posts?userId={userId}", userId)
                    .retrieve()
                    .bodyToFlux(ExternalPostDto.class)
                    .timeout(Duration.ofMillis(appProperties.getExternalApi().getTimeout()))
                    .retryWhen(Retry.fixedDelay(appProperties.getExternalApi().getRetryAttempts(), Duration.ofSeconds(1)))
                    .collectList()
                    .doOnSuccess(posts -> logger.info("Encontrados {} posts para usuário {}", posts.size(), userId))
                    .doOnError(error -> logger.error("Erro ao buscar posts do usuário {}: {}", userId, error.getMessage()))
                    .block();
        } catch (Exception e) {
            logger.error("Erro ao buscar posts do usuário {} da API externa", userId, e);
            throw new RuntimeException("Falha ao buscar posts do usuário da API externa", e);
        }
    }

    /**
     * Buscar todos os usuários da API externa (com cache)
     */
    @Cacheable(value = "externalUsers")
    public List<ExternalUserDto> getAllUsers() {
        logger.info("Buscando todos os usuários da API externa");
        
        try {
            return jsonPlaceholderWebClient
                    .get()
                    .uri("/users")
                    .retrieve()
                    .bodyToFlux(ExternalUserDto.class)
                    .timeout(Duration.ofMillis(appProperties.getExternalApi().getTimeout()))
                    .retryWhen(Retry.fixedDelay(appProperties.getExternalApi().getRetryAttempts(), Duration.ofSeconds(1)))
                    .collectList()
                    .doOnSuccess(users -> logger.info("Encontrados {} usuários", users.size()))
                    .doOnError(error -> logger.error("Erro ao buscar usuários: {}", error.getMessage()))
                    .block();
        } catch (Exception e) {
            logger.error("Erro ao buscar usuários da API externa", e);
            throw new RuntimeException("Falha ao buscar usuários da API externa", e);
        }
    }

    /**
     * Buscar usuário por ID
     */
    @Cacheable(value = "externalUser", key = "#userId")
    public ExternalUserDto getUserById(Long userId) {
        logger.info("Buscando usuário por ID: {}", userId);
        
        try {
            return jsonPlaceholderWebClient
                    .get()
                    .uri("/users/{id}", userId)
                    .retrieve()
                    .onStatus(HttpStatus.NOT_FOUND::equals, 
                            response -> Mono.error(new RuntimeException("Usuário não encontrado: " + userId)))
                    .bodyToMono(ExternalUserDto.class)
                    .timeout(Duration.ofMillis(appProperties.getExternalApi().getTimeout()))
                    .retryWhen(Retry.fixedDelay(appProperties.getExternalApi().getRetryAttempts(), Duration.ofSeconds(1)))
                    .doOnSuccess(user -> logger.info("Usuário encontrado: {}", user.getName()))
                    .doOnError(error -> logger.error("Erro ao buscar usuário {}: {}", userId, error.getMessage()))
                    .block();
        } catch (WebClientResponseException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                logger.warn("Usuário não encontrado: {}", userId);
                return null;
            }
            logger.error("Erro HTTP ao buscar usuário {}: {}", userId, e.getMessage());
            throw new RuntimeException("Erro ao buscar usuário da API externa", e);
        } catch (Exception e) {
            logger.error("Erro ao buscar usuário {} da API externa", userId, e);
            throw new RuntimeException("Falha ao buscar usuário da API externa", e);
        }
    }

    /**
     * Criar novo post na API externa
     */
    public ExternalPostDto createPost(ExternalPostDto post) {
        logger.info("Criando novo post na API externa: {}", post.getTitle());
        
        try {
            return jsonPlaceholderWebClient
                    .post()
                    .uri("/posts")
                    .bodyValue(post)
                    .retrieve()
                    .bodyToMono(ExternalPostDto.class)
                    .timeout(Duration.ofMillis(appProperties.getExternalApi().getTimeout()))
                    .retryWhen(Retry.fixedDelay(appProperties.getExternalApi().getRetryAttempts(), Duration.ofSeconds(1)))
                    .doOnSuccess(createdPost -> logger.info("Post criado com ID: {}", createdPost.getId()))
                    .doOnError(error -> logger.error("Erro ao criar post: {}", error.getMessage()))
                    .block();
        } catch (Exception e) {
            logger.error("Erro ao criar post na API externa", e);
            throw new RuntimeException("Falha ao criar post na API externa", e);
        }
    }

    /**
     * Método para testar conectividade com API externa
     */
    public boolean testConnection() {
        try {
            logger.info("Testando conectividade com API externa");
            
            ExternalPostDto firstPost = jsonPlaceholderWebClient
                    .get()
                    .uri("/posts/1")
                    .retrieve()
                    .bodyToMono(ExternalPostDto.class)
                    .timeout(Duration.ofSeconds(5))
                    .block();
            
            boolean connected = firstPost != null;
            logger.info("Teste de conectividade: {}", connected ? "SUCESSO" : "FALHA");
            return connected;
            
        } catch (Exception e) {
            logger.error("Falha no teste de conectividade: {}", e.getMessage());
            return false;
        }
    }
}

