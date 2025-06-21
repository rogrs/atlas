package com.example.app.repository;

import com.example.app.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositório para operações com a entidade User no MongoDB
 */
@Repository
public interface UserRepository extends MongoRepository<User, String> {

    /**
     * Buscar usuário por email
     */
    Optional<User> findByEmail(String email);

    /**
     * Verificar se existe usuário com o email
     */
    boolean existsByEmail(String email);

    /**
     * Buscar usuários ativos
     */
    List<User> findByActiveTrue();

    /**
     * Buscar usuários por nome (case insensitive)
     */
    @Query("{'name': {$regex: ?0, $options: 'i'}}")
    List<User> findByNameContainingIgnoreCase(String name);

    /**
     * Buscar usuários ativos com paginação
     */
    Page<User> findByActiveTrue(Pageable pageable);

    /**
     * Buscar usuários por nome com paginação
     */
    @Query("{'name': {$regex: ?0, $options: 'i'}, 'active': true}")
    Page<User> findByNameContainingIgnoreCaseAndActiveTrue(String name, Pageable pageable);

    /**
     * Contar usuários ativos
     */
    long countByActiveTrue();

    /**
     * Buscar usuários por lista de IDs
     */
    List<User> findByIdIn(List<String> ids);
}

