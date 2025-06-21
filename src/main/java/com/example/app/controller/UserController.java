package com.example.app.controller;

import com.example.app.entity.User;
import com.example.app.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Controller REST para operações com usuários
 */
@RestController
@RequestMapping("/users")
@Tag(name = "Users", description = "API para gerenciamento de usuários")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Operation(summary = "Criar novo usuário", description = "Cria um novo usuário no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "409", description = "Email já existe")
    })
    @PostMapping
    public ResponseEntity<User> createUser(
            @Parameter(description = "Dados do usuário a ser criado", required = true)
            @Valid @RequestBody User user) {
        
        logger.info("Criando usuário: {}", user.getEmail());
        
        try {
            User createdUser = userService.createUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (IllegalArgumentException e) {
            logger.warn("Erro ao criar usuário: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @Operation(summary = "Buscar usuário por ID", description = "Retorna um usuário específico pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(
            @Parameter(description = "ID do usuário", required = true)
            @PathVariable String id) {
        
        logger.info("Buscando usuário por ID: {}", id);
        
        Optional<User> user = userService.findById(id);
        return user.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Buscar usuário por email", description = "Retorna um usuário específico pelo email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(
            @Parameter(description = "Email do usuário", required = true)
            @PathVariable String email) {
        
        logger.info("Buscando usuário por email: {}", email);
        
        Optional<User> user = userService.findByEmail(email);
        return user.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Listar usuários ativos", description = "Retorna lista de todos os usuários ativos")
    @ApiResponse(responseCode = "200", description = "Lista de usuários",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class)))
    @GetMapping
    public ResponseEntity<List<User>> getActiveUsers() {
        logger.info("Listando usuários ativos");
        
        List<User> users = userService.findActiveUsers();
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Listar usuários com paginação", description = "Retorna lista paginada de usuários ativos")
    @ApiResponse(responseCode = "200", description = "Página de usuários")
    @GetMapping("/page")
    public ResponseEntity<Page<User>> getActiveUsersWithPagination(
            @Parameter(description = "Número da página (0-based)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo para ordenação")
            @RequestParam(defaultValue = "name") String sortBy,
            @Parameter(description = "Direção da ordenação (asc/desc)")
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        logger.info("Listando usuários com paginação: page={}, size={}", page, size);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                   Sort.by(sortBy).descending() : 
                   Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<User> users = userService.findActiveUsers(pageable);
        
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Buscar usuários por nome", description = "Busca usuários que contenham o nome especificado")
    @ApiResponse(responseCode = "200", description = "Lista de usuários encontrados")
    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUsersByName(
            @Parameter(description = "Nome a ser buscado", required = true)
            @RequestParam String name) {
        
        logger.info("Buscando usuários por nome: {}", name);
        
        List<User> users = userService.findByName(name);
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Atualizar usuário", description = "Atualiza os dados de um usuário existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(
            @Parameter(description = "ID do usuário", required = true)
            @PathVariable String id,
            @Parameter(description = "Dados atualizados do usuário", required = true)
            @Valid @RequestBody User user) {
        
        logger.info("Atualizando usuário: {}", id);
        
        try {
            user.setId(id);
            User updatedUser = userService.updateUser(user);
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            logger.warn("Erro ao atualizar usuário: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Desativar usuário", description = "Desativa um usuário (soft delete)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário desativado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<User> deactivateUser(
            @Parameter(description = "ID do usuário", required = true)
            @PathVariable String id) {
        
        logger.info("Desativando usuário: {}", id);
        
        try {
            User deactivatedUser = userService.deactivateUser(id);
            return ResponseEntity.ok(deactivatedUser);
        } catch (IllegalArgumentException e) {
            logger.warn("Erro ao desativar usuário: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Deletar usuário", description = "Remove permanentemente um usuário do sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuário deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "ID do usuário", required = true)
            @PathVariable String id) {
        
        logger.info("Deletando usuário: {}", id);
        
        try {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            logger.warn("Erro ao deletar usuário: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Contar usuários ativos", description = "Retorna o número total de usuários ativos")
    @ApiResponse(responseCode = "200", description = "Número de usuários ativos")
    @GetMapping("/count")
    public ResponseEntity<Long> countActiveUsers() {
        logger.info("Contando usuários ativos");
        
        long count = userService.countActiveUsers();
        return ResponseEntity.ok(count);
    }

    @Operation(summary = "Verificar se email existe", description = "Verifica se um email já está em uso")
    @ApiResponse(responseCode = "200", description = "Resultado da verificação")
    @GetMapping("/email-exists/{email}")
    public ResponseEntity<Boolean> emailExists(
            @Parameter(description = "Email a ser verificado", required = true)
            @PathVariable String email) {
        
        logger.info("Verificando se email existe: {}", email);
        
        boolean exists = userService.emailExists(email);
        return ResponseEntity.ok(exists);
    }

    @Operation(summary = "Limpar cache de usuários", description = "Remove todos os dados de usuários do cache Redis")
    @ApiResponse(responseCode = "200", description = "Cache limpo com sucesso")
    @PostMapping("/clear-cache")
    public ResponseEntity<String> clearCache() {
        logger.info("Limpando cache de usuários");
        
        userService.clearCache();
        return ResponseEntity.ok("Cache de usuários limpo com sucesso");
    }
}

