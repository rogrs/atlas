package com.example.app.service;

import com.example.app.entity.Product;
import com.example.app.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Serviço para operações com produtos
 * 
 * Implementa cache Redis para melhorar performance
 */
@Service
public class ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    @Autowired
    private ProductRepository productRepository;

    /**
     * Criar novo produto
     */
    @CachePut(value = "products", key = "#result.id", condition = "#result != null")
    public Product createProduct(Product product) {
        logger.info("Criando novo produto: {}", product.getName());
        return productRepository.save(product);
    }

    /**
     * Buscar produto por ID (com cache)
     */
    @Cacheable(value = "products", key = "#id")
    public Optional<Product> findById(String id) {
        logger.info("Buscando produto por ID: {}", id);
        return productRepository.findById(id);
    }

    /**
     * Atualizar produto
     */
    @CachePut(value = "products", key = "#product.id")
    public Product updateProduct(Product product) {
        logger.info("Atualizando produto: {}", product.getId());
        
        if (!productRepository.existsById(product.getId())) {
            throw new IllegalArgumentException("Produto não encontrado: " + product.getId());
        }
        
        return productRepository.save(product);
    }

    /**
     * Deletar produto (remove do cache)
     */
    @CacheEvict(value = "products", key = "#id")
    public void deleteProduct(String id) {
        logger.info("Deletando produto: {}", id);
        
        if (!productRepository.existsById(id)) {
            throw new IllegalArgumentException("Produto não encontrado: " + id);
        }
        
        productRepository.deleteById(id);
    }

    /**
     * Desativar produto (soft delete)
     */
    @CachePut(value = "products", key = "#id")
    public Product deactivateProduct(String id) {
        logger.info("Desativando produto: {}", id);
        
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado: " + id));
        
        product.setAvailable(false);
        return productRepository.save(product);
    }

    /**
     * Listar todos os produtos disponíveis
     */
    @Cacheable(value = "availableProducts")
    public List<Product> findAvailableProducts() {
        logger.info("Buscando produtos disponíveis");
        return productRepository.findByAvailableTrue();
    }

    /**
     * Buscar produtos com paginação
     */
    public Page<Product> findAvailableProducts(Pageable pageable) {
        logger.info("Buscando produtos disponíveis com paginação: {}", pageable);
        return productRepository.findByAvailableTrue(pageable);
    }

    /**
     * Buscar produtos por categoria
     */
    @Cacheable(value = "productsByCategory", key = "#category")
    public List<Product> findByCategory(String category) {
        logger.info("Buscando produtos por categoria: {}", category);
        return productRepository.findByCategoryAndAvailableTrue(category);
    }

    /**
     * Buscar produtos por categoria com paginação
     */
    public Page<Product> findByCategory(String category, Pageable pageable) {
        logger.info("Buscando produtos por categoria com paginação: {} - {}", category, pageable);
        return productRepository.findByCategoryAndAvailableTrue(category, pageable);
    }

    /**
     * Buscar produtos por nome
     */
    public List<Product> findByName(String name) {
        logger.info("Buscando produtos por nome: {}", name);
        return productRepository.findByNameContainingIgnoreCase(name);
    }

    /**
     * Buscar produtos por nome com paginação
     */
    public Page<Product> findByName(String name, Pageable pageable) {
        logger.info("Buscando produtos por nome com paginação: {} - {}", name, pageable);
        return productRepository.findByNameContainingIgnoreCaseAndAvailableTrue(name, pageable);
    }

    /**
     * Buscar produtos por faixa de preço
     */
    public List<Product> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        logger.info("Buscando produtos por faixa de preço: {} - {}", minPrice, maxPrice);
        return productRepository.findByPriceBetween(minPrice, maxPrice);
    }

    /**
     * Buscar produtos por tag
     */
    @Cacheable(value = "productsByTag", key = "#tag")
    public List<Product> findByTag(String tag) {
        logger.info("Buscando produtos por tag: {}", tag);
        return productRepository.findByTagsContaining(tag);
    }

    /**
     * Buscar produtos com estoque baixo
     */
    public List<Product> findLowStockProducts(Integer maxStock) {
        logger.info("Buscando produtos com estoque baixo: {}", maxStock);
        return productRepository.findByStockLessThanEqualAndAvailableTrue(maxStock);
    }

    /**
     * Atualizar estoque do produto
     */
    @CachePut(value = "products", key = "#productId")
    public Product updateStock(String productId, Integer newStock) {
        logger.info("Atualizando estoque do produto: {} para {}", productId, newStock);
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado: " + productId));
        
        product.setStock(newStock);
        return productRepository.save(product);
    }

    /**
     * Contar produtos por categoria
     */
    @Cacheable(value = "productCountByCategory", key = "#category")
    public long countByCategory(String category) {
        logger.info("Contando produtos por categoria: {}", category);
        return productRepository.countByCategory(category);
    }

    /**
     * Contar produtos disponíveis
     */
    @Cacheable(value = "availableProductCount")
    public long countAvailableProducts() {
        logger.info("Contando produtos disponíveis");
        return productRepository.countByAvailableTrue();
    }

    /**
     * Limpar cache de produtos
     */
    @CacheEvict(value = {"products", "availableProducts", "productsByCategory", "productsByTag", 
                         "productCountByCategory", "availableProductCount"}, allEntries = true)
    public void clearCache() {
        logger.info("Limpando cache de produtos");
    }
}

