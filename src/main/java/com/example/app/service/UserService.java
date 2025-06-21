package com.example.app.service;

import com.example.app.entity.User;
import com.example.app.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Serviço para operações com usuários
 * 
 * Implementa cache Redis para melhorar performance
 */
@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    /**
     * Criar novo usuário
     */
    @CachePut(value = "users", key = "#result.id", condition = "#result != null")
    public User createUser(User user) {
        logger.info("Criando novo usuário: {}", user.getEmail());
        
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email já está em uso: " + user.getEmail());
        }
        
        return userRepository.save(user);
    }

    /**
     * Buscar usuário por ID (com cache)
     */
    @Cacheable(value = "users", key = "#id")
    public Optional<User> findById(String id) {
        logger.info("Buscando usuário por ID: {}", id);
        return userRepository.findById(id);
    }

    /**
     * Buscar usuário por email (com cache)
     */
    @Cacheable(value = "users", key = "'email:' + #email")
    public Optional<User> findByEmail(String email) {
        logger.info("Buscando usuário por email: {}", email);
        return userRepository.findByEmail(email);
    }

    /**
     * Atualizar usuário
     */
    @CachePut(value = "users", key = "#user.id")
    public User updateUser(User user) {
        logger.info("Atualizando usuário: {}", user.getId());
        
        if (!userRepository.existsById(user.getId())) {
            throw new IllegalArgumentException("Usuário não encontrado: " + user.getId());
        }
        
        return userRepository.save(user);
    }

    /**
     * Deletar usuário (remove do cache)
     */
    @CacheEvict(value = "users", key = "#id")
    public void deleteUser(String id) {
        logger.info("Deletando usuário: {}", id);
        
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("Usuário não encontrado: " + id);
        }
        
        userRepository.deleteById(id);
    }

    /**
     * Desativar usuário (soft delete)
     */
    @CachePut(value = "users", key = "#id")
    public User deactivateUser(String id) {
        logger.info("Desativando usuário: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado: " + id));
        
        user.setActive(false);
        return userRepository.save(user);
    }

    /**
     * Listar todos os usuários ativos
     */
    @Cacheable(value = "activeUsers")
    public List<User> findActiveUsers() {
        logger.info("Buscando usuários ativos");
        return userRepository.findByActiveTrue();
    }

    /**
     * Buscar usuários com paginação
     */
    public Page<User> findActiveUsers(Pageable pageable) {
        logger.info("Buscando usuários ativos com paginação: {}", pageable);
        return userRepository.findByActiveTrue(pageable);
    }

    /**
     * Buscar usuários por nome
     */
    public List<User> findByName(String name) {
        logger.info("Buscando usuários por nome: {}", name);
        return userRepository.findByNameContainingIgnoreCase(name);
    }

    /**
     * Buscar usuários por nome com paginação
     */
    public Page<User> findByName(String name, Pageable pageable) {
        logger.info("Buscando usuários por nome com paginação: {} - {}", name, pageable);
        return userRepository.findByNameContainingIgnoreCaseAndActiveTrue(name, pageable);
    }

    /**
     * Contar usuários ativos
     */
    @Cacheable(value = "userCount")
    public long countActiveUsers() {
        logger.info("Contando usuários ativos");
        return userRepository.countByActiveTrue();
    }

    /**
     * Verificar se email existe
     */
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Limpar cache de usuários
     */
    @CacheEvict(value = {"users", "activeUsers", "userCount"}, allEntries = true)
    public void clearCache() {
        logger.info("Limpando cache de usuários");
    }
}

