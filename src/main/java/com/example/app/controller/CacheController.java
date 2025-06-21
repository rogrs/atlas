package com.example.app.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Controller para operações de cache Redis e monitoramento
 */
@RestController
@RequestMapping("/cache")
@Tag(name = "Cache", description = "Operações de cache Redis e monitoramento")
public class CacheController {

    private static final Logger logger = LoggerFactory.getLogger(CacheController.class);

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Operation(summary = "Listar caches disponíveis", description = "Retorna lista de todos os caches configurados")
    @ApiResponse(responseCode = "200", description = "Lista de caches")
    @GetMapping("/names")
    public ResponseEntity<Set<String>> getCacheNames() {
        logger.info("Listando nomes dos caches");
        
        Set<String> cacheNames = cacheManager.getCacheNames().stream()
                .collect(java.util.stream.Collectors.toSet());
        
        return ResponseEntity.ok(cacheNames);
    }

    @Operation(summary = "Limpar cache específico", description = "Remove todas as entradas de um cache específico")
    @ApiResponse(responseCode = "200", description = "Cache limpo com sucesso")
    @DeleteMapping("/{cacheName}")
    public ResponseEntity<String> clearCache(
            @Parameter(description = "Nome do cache a ser limpo", required = true)
            @PathVariable String cacheName) {
        
        logger.info("Limpando cache: {}", cacheName);
        
        var cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.clear();
            return ResponseEntity.ok("Cache '" + cacheName + "' limpo com sucesso");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Limpar todos os caches", description = "Remove todas as entradas de todos os caches")
    @ApiResponse(responseCode = "200", description = "Todos os caches limpos com sucesso")
    @DeleteMapping("/all")
    public ResponseEntity<String> clearAllCaches() {
        logger.info("Limpando todos os caches");
        
        cacheManager.getCacheNames().forEach(cacheName -> {
            var cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                cache.clear();
            }
        });
        
        return ResponseEntity.ok("Todos os caches limpos com sucesso");
    }

    @Operation(summary = "Obter estatísticas do Redis", description = "Retorna informações e estatísticas do Redis")
    @ApiResponse(responseCode = "200", description = "Estatísticas do Redis")
    @GetMapping("/redis/stats")
    public ResponseEntity<Map<String, Object>> getRedisStats() {
        logger.info("Obtendo estatísticas do Redis");
        
        Map<String, Object> stats = new HashMap<>();
        
        try {
            // Informações básicas do Redis
            var connection = redisTemplate.getConnectionFactory().getConnection();
            var info = connection.info();
            
            stats.put("connected", true);
            stats.put("info", info.toString());
            
            // Número de chaves
            Set<String> keys = redisTemplate.keys("*");
            stats.put("totalKeys", keys != null ? keys.size() : 0);
            
            connection.close();
            
        } catch (Exception e) {
            logger.error("Erro ao obter estatísticas do Redis: {}", e.getMessage());
            stats.put("connected", false);
            stats.put("error", e.getMessage());
        }
        
        return ResponseEntity.ok(stats);
    }

    @Operation(summary = "Listar chaves do Redis", description = "Retorna todas as chaves armazenadas no Redis")
    @ApiResponse(responseCode = "200", description = "Lista de chaves do Redis")
    @GetMapping("/redis/keys")
    public ResponseEntity<Set<String>> getRedisKeys(
            @Parameter(description = "Padrão para filtrar chaves (opcional)")
            @RequestParam(defaultValue = "*") String pattern) {
        
        logger.info("Listando chaves do Redis com padrão: {}", pattern);
        
        try {
            Set<String> keys = redisTemplate.keys(pattern);
            return ResponseEntity.ok(keys != null ? keys : Set.of());
        } catch (Exception e) {
            logger.error("Erro ao listar chaves do Redis: {}", e.getMessage());
            return ResponseEntity.ok(Set.of());
        }
    }

    @Operation(summary = "Obter valor do Redis", description = "Retorna o valor de uma chave específica do Redis")
    @ApiResponse(responseCode = "200", description = "Valor da chave")
    @GetMapping("/redis/key/{key}")
    public ResponseEntity<Object> getRedisValue(
            @Parameter(description = "Chave a ser consultada", required = true)
            @PathVariable String key) {
        
        logger.info("Obtendo valor da chave: {}", key);
        
        try {
            Object value = redisTemplate.opsForValue().get(key);
            if (value != null) {
                return ResponseEntity.ok(value);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Erro ao obter valor da chave {}: {}", key, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Deletar chave do Redis", description = "Remove uma chave específica do Redis")
    @ApiResponse(responseCode = "200", description = "Chave removida com sucesso")
    @DeleteMapping("/redis/key/{key}")
    public ResponseEntity<String> deleteRedisKey(
            @Parameter(description = "Chave a ser removida", required = true)
            @PathVariable String key) {
        
        logger.info("Removendo chave do Redis: {}", key);
        
        try {
            Boolean deleted = redisTemplate.delete(key);
            if (Boolean.TRUE.equals(deleted)) {
                return ResponseEntity.ok("Chave '" + key + "' removida com sucesso");
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Erro ao remover chave {}: {}", key, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Definir valor no Redis", description = "Define um valor para uma chave específica no Redis")
    @ApiResponse(responseCode = "200", description = "Valor definido com sucesso")
    @PostMapping("/redis/key/{key}")
    public ResponseEntity<String> setRedisValue(
            @Parameter(description = "Chave a ser definida", required = true)
            @PathVariable String key,
            @Parameter(description = "Valor a ser armazenado", required = true)
            @RequestBody Object value) {
        
        logger.info("Definindo valor para chave: {}", key);
        
        try {
            redisTemplate.opsForValue().set(key, value);
            return ResponseEntity.ok("Valor definido para chave '" + key + "' com sucesso");
        } catch (Exception e) {
            logger.error("Erro ao definir valor para chave {}: {}", key, e.getMessage());
            return ResponseEntity.badRequest().body("Erro ao definir valor: " + e.getMessage());
        }
    }

    @Operation(summary = "Verificar conectividade Redis", description = "Testa a conectividade com o Redis")
    @ApiResponse(responseCode = "200", description = "Status da conectividade")
    @GetMapping("/redis/ping")
    public ResponseEntity<String> pingRedis() {
        logger.info("Testando conectividade com Redis");
        
        try {
            var connection = redisTemplate.getConnectionFactory().getConnection();
            String pong = connection.ping();
            connection.close();
            
            return ResponseEntity.ok("Redis conectado: " + pong);
        } catch (Exception e) {
            logger.error("Erro ao conectar com Redis: {}", e.getMessage());
            return ResponseEntity.status(503).body("Redis não conectado: " + e.getMessage());
        }
    }
}

