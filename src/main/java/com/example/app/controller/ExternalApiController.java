package com.example.app.controller;

import com.example.app.client.ExternalApiClient;
import com.example.app.dto.ExternalPostDto;
import com.example.app.dto.ExternalUserDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller para demonstrar integração com APIs externas
 */
@RestController
@RequestMapping("/external")
@Tag(name = "External API", description = "Integração com APIs externas usando WebClient")
public class ExternalApiController {

    private static final Logger logger = LoggerFactory.getLogger(ExternalApiController.class);

    @Autowired
    private ExternalApiClient externalApiClient;

    @Operation(summary = "Testar conectividade", description = "Testa a conectividade com a API externa")
    @ApiResponse(responseCode = "200", description = "Status da conectividade")
    @GetMapping("/test-connection")
    public ResponseEntity<String> testConnection() {
        logger.info("Testando conectividade com API externa");
        
        boolean connected = externalApiClient.testConnection();
        String message = connected ? "Conectividade OK" : "Falha na conectividade";
        HttpStatus status = connected ? HttpStatus.OK : HttpStatus.SERVICE_UNAVAILABLE;
        
        return ResponseEntity.status(status).body(message);
    }

    @Operation(summary = "Listar todos os posts", description = "Busca todos os posts da API externa JSONPlaceholder")
    @ApiResponse(responseCode = "200", description = "Lista de posts externos",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExternalPostDto.class)))
    @GetMapping("/posts")
    public ResponseEntity<List<ExternalPostDto>> getAllPosts() {
        logger.info("Buscando todos os posts da API externa");
        
        try {
            List<ExternalPostDto> posts = externalApiClient.getAllPosts();
            return ResponseEntity.ok(posts);
        } catch (RuntimeException e) {
            logger.error("Erro ao buscar posts: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
    }

    @Operation(summary = "Buscar post por ID", description = "Busca um post específico pelo ID na API externa")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Post encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExternalPostDto.class))),
            @ApiResponse(responseCode = "404", description = "Post não encontrado"),
            @ApiResponse(responseCode = "503", description = "Serviço indisponível")
    })
    @GetMapping("/posts/{id}")
    public ResponseEntity<ExternalPostDto> getPostById(
            @Parameter(description = "ID do post", required = true)
            @PathVariable Long id) {
        
        logger.info("Buscando post por ID: {}", id);
        
        try {
            ExternalPostDto post = externalApiClient.getPostById(id);
            if (post != null) {
                return ResponseEntity.ok(post);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (RuntimeException e) {
            logger.error("Erro ao buscar post {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
    }

    @Operation(summary = "Buscar posts por usuário", description = "Busca todos os posts de um usuário específico")
    @ApiResponse(responseCode = "200", description = "Lista de posts do usuário")
    @GetMapping("/posts/user/{userId}")
    public ResponseEntity<List<ExternalPostDto>> getPostsByUserId(
            @Parameter(description = "ID do usuário", required = true)
            @PathVariable Long userId) {
        
        logger.info("Buscando posts do usuário: {}", userId);
        
        try {
            List<ExternalPostDto> posts = externalApiClient.getPostsByUserId(userId);
            return ResponseEntity.ok(posts);
        } catch (RuntimeException e) {
            logger.error("Erro ao buscar posts do usuário {}: {}", userId, e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
    }

    @Operation(summary = "Criar novo post", description = "Cria um novo post na API externa")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Post criado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExternalPostDto.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "503", description = "Serviço indisponível")
    })
    @PostMapping("/posts")
    public ResponseEntity<ExternalPostDto> createPost(
            @Parameter(description = "Dados do post a ser criado", required = true)
            @RequestBody ExternalPostDto post) {
        
        logger.info("Criando novo post: {}", post.getTitle());
        
        try {
            ExternalPostDto createdPost = externalApiClient.createPost(post);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdPost);
        } catch (RuntimeException e) {
            logger.error("Erro ao criar post: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
    }

    @Operation(summary = "Listar todos os usuários", description = "Busca todos os usuários da API externa JSONPlaceholder")
    @ApiResponse(responseCode = "200", description = "Lista de usuários externos",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExternalUserDto.class)))
    @GetMapping("/users")
    public ResponseEntity<List<ExternalUserDto>> getAllUsers() {
        logger.info("Buscando todos os usuários da API externa");
        
        try {
            List<ExternalUserDto> users = externalApiClient.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (RuntimeException e) {
            logger.error("Erro ao buscar usuários: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
    }

    @Operation(summary = "Buscar usuário por ID", description = "Busca um usuário específico pelo ID na API externa")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExternalUserDto.class))),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "503", description = "Serviço indisponível")
    })
    @GetMapping("/users/{id}")
    public ResponseEntity<ExternalUserDto> getUserById(
            @Parameter(description = "ID do usuário", required = true)
            @PathVariable Long id) {
        
        logger.info("Buscando usuário por ID: {}", id);
        
        try {
            ExternalUserDto user = externalApiClient.getUserById(id);
            if (user != null) {
                return ResponseEntity.ok(user);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (RuntimeException e) {
            logger.error("Erro ao buscar usuário {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
    }
}

