package com.example.app.repository;

import com.example.app.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Repositório para operações com a entidade Product no MongoDB
 */
@Repository
public interface ProductRepository extends MongoRepository<Product, String> {

    /**
     * Buscar produtos por categoria
     */
    List<Product> findByCategory(String category);

    /**
     * Buscar produtos disponíveis
     */
    List<Product> findByAvailableTrue();

    /**
     * Buscar produtos por categoria e disponibilidade
     */
    List<Product> findByCategoryAndAvailableTrue(String category);

    /**
     * Buscar produtos por nome (case insensitive)
     */
    @Query("{'name': {$regex: ?0, $options: 'i'}}")
    List<Product> findByNameContainingIgnoreCase(String name);

    /**
     * Buscar produtos por faixa de preço
     */
    List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    /**
     * Buscar produtos por tag
     */
    @Query("{'tags': {$in: [?0]}}")
    List<Product> findByTagsContaining(String tag);

    /**
     * Buscar produtos disponíveis com paginação
     */
    Page<Product> findByAvailableTrue(Pageable pageable);

    /**
     * Buscar produtos por categoria com paginação
     */
    Page<Product> findByCategoryAndAvailableTrue(String category, Pageable pageable);

    /**
     * Buscar produtos por nome com paginação
     */
    @Query("{'name': {$regex: ?0, $options: 'i'}, 'available': true}")
    Page<Product> findByNameContainingIgnoreCaseAndAvailableTrue(String name, Pageable pageable);

    /**
     * Contar produtos por categoria
     */
    long countByCategory(String category);

    /**
     * Contar produtos disponíveis
     */
    long countByAvailableTrue();

    /**
     * Buscar produtos com estoque baixo
     */
    @Query("{'stock': {$lte: ?0}, 'available': true}")
    List<Product> findByStockLessThanEqualAndAvailableTrue(Integer maxStock);

    /**
     * Buscar categorias distintas
     */
    @Query(value = "{}", fields = "{'category': 1}")
    List<Product> findDistinctCategories();
}

