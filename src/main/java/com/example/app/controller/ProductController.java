package com.example.app.controller;

import com.example.app.entity.Product;
import com.example.app.service.ProductService;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Controller REST para operações com produtos
 */
@RestController
@RequestMapping("/products")
@Tag(name = "Products", description = "API para gerenciamento de produtos")
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private ProductService productService;

    @Operation(summary = "Criar novo produto", description = "Cria um novo produto no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Produto criado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Product.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PostMapping
    public ResponseEntity<Product> createProduct(
            @Parameter(description = "Dados do produto a ser criado", required = true)
            @Valid @RequestBody Product product) {
        
        logger.info("Criando produto: {}", product.getName());
        
        Product createdProduct = productService.createProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }

    @Operation(summary = "Buscar produto por ID", description = "Retorna um produto específico pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produto encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Product.class))),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(
            @Parameter(description = "ID do produto", required = true)
            @PathVariable String id) {
        
        logger.info("Buscando produto por ID: {}", id);
        
        Optional<Product> product = productService.findById(id);
        return product.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Listar produtos disponíveis", description = "Retorna lista de todos os produtos disponíveis")
    @ApiResponse(responseCode = "200", description = "Lista de produtos",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Product.class)))
    @GetMapping
    public ResponseEntity<List<Product>> getAvailableProducts() {
        logger.info("Listando produtos disponíveis");
        
        List<Product> products = productService.findAvailableProducts();
        return ResponseEntity.ok(products);
    }

    @Operation(summary = "Listar produtos com paginação", description = "Retorna lista paginada de produtos disponíveis")
    @ApiResponse(responseCode = "200", description = "Página de produtos")
    @GetMapping("/page")
    public ResponseEntity<Page<Product>> getAvailableProductsWithPagination(
            @Parameter(description = "Número da página (0-based)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo para ordenação")
            @RequestParam(defaultValue = "name") String sortBy,
            @Parameter(description = "Direção da ordenação (asc/desc)")
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        logger.info("Listando produtos com paginação: page={}, size={}", page, size);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                   Sort.by(sortBy).descending() : 
                   Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Product> products = productService.findAvailableProducts(pageable);
        
        return ResponseEntity.ok(products);
    }

    @Operation(summary = "Buscar produtos por categoria", description = "Retorna produtos de uma categoria específica")
    @ApiResponse(responseCode = "200", description = "Lista de produtos da categoria")
    @GetMapping("/category/{category}")
    public ResponseEntity<List<Product>> getProductsByCategory(
            @Parameter(description = "Nome da categoria", required = true)
            @PathVariable String category) {
        
        logger.info("Buscando produtos por categoria: {}", category);
        
        List<Product> products = productService.findByCategory(category);
        return ResponseEntity.ok(products);
    }

    @Operation(summary = "Buscar produtos por categoria com paginação", description = "Retorna produtos de uma categoria com paginação")
    @ApiResponse(responseCode = "200", description = "Página de produtos da categoria")
    @GetMapping("/category/{category}/page")
    public ResponseEntity<Page<Product>> getProductsByCategoryWithPagination(
            @Parameter(description = "Nome da categoria", required = true)
            @PathVariable String category,
            @Parameter(description = "Número da página (0-based)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página")
            @RequestParam(defaultValue = "10") int size) {
        
        logger.info("Buscando produtos por categoria com paginação: {} - page={}, size={}", category, page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productService.findByCategory(category, pageable);
        
        return ResponseEntity.ok(products);
    }

    @Operation(summary = "Buscar produtos por nome", description = "Busca produtos que contenham o nome especificado")
    @ApiResponse(responseCode = "200", description = "Lista de produtos encontrados")
    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProductsByName(
            @Parameter(description = "Nome a ser buscado", required = true)
            @RequestParam String name) {
        
        logger.info("Buscando produtos por nome: {}", name);
        
        List<Product> products = productService.findByName(name);
        return ResponseEntity.ok(products);
    }

    @Operation(summary = "Buscar produtos por faixa de preço", description = "Retorna produtos dentro de uma faixa de preço")
    @ApiResponse(responseCode = "200", description = "Lista de produtos na faixa de preço")
    @GetMapping("/price-range")
    public ResponseEntity<List<Product>> getProductsByPriceRange(
            @Parameter(description = "Preço mínimo", required = true)
            @RequestParam BigDecimal minPrice,
            @Parameter(description = "Preço máximo", required = true)
            @RequestParam BigDecimal maxPrice) {
        
        logger.info("Buscando produtos por faixa de preço: {} - {}", minPrice, maxPrice);
        
        List<Product> products = productService.findByPriceRange(minPrice, maxPrice);
        return ResponseEntity.ok(products);
    }

    @Operation(summary = "Buscar produtos por tag", description = "Retorna produtos que contenham uma tag específica")
    @ApiResponse(responseCode = "200", description = "Lista de produtos com a tag")
    @GetMapping("/tag/{tag}")
    public ResponseEntity<List<Product>> getProductsByTag(
            @Parameter(description = "Tag a ser buscada", required = true)
            @PathVariable String tag) {
        
        logger.info("Buscando produtos por tag: {}", tag);
        
        List<Product> products = productService.findByTag(tag);
        return ResponseEntity.ok(products);
    }

    @Operation(summary = "Buscar produtos com estoque baixo", description = "Retorna produtos com estoque menor ou igual ao valor especificado")
    @ApiResponse(responseCode = "200", description = "Lista de produtos com estoque baixo")
    @GetMapping("/low-stock")
    public ResponseEntity<List<Product>> getLowStockProducts(
            @Parameter(description = "Estoque máximo para considerar baixo")
            @RequestParam(defaultValue = "10") Integer maxStock) {
        
        logger.info("Buscando produtos com estoque baixo: {}", maxStock);
        
        List<Product> products = productService.findLowStockProducts(maxStock);
        return ResponseEntity.ok(products);
    }

    @Operation(summary = "Atualizar produto", description = "Atualiza os dados de um produto existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produto atualizado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Product.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(
            @Parameter(description = "ID do produto", required = true)
            @PathVariable String id,
            @Parameter(description = "Dados atualizados do produto", required = true)
            @Valid @RequestBody Product product) {
        
        logger.info("Atualizando produto: {}", id);
        
        try {
            product.setId(id);
            Product updatedProduct = productService.updateProduct(product);
            return ResponseEntity.ok(updatedProduct);
        } catch (IllegalArgumentException e) {
            logger.warn("Erro ao atualizar produto: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Atualizar estoque do produto", description = "Atualiza apenas o estoque de um produto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estoque atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    @PatchMapping("/{id}/stock")
    public ResponseEntity<Product> updateProductStock(
            @Parameter(description = "ID do produto", required = true)
            @PathVariable String id,
            @Parameter(description = "Novo valor do estoque", required = true)
            @RequestParam Integer stock) {
        
        logger.info("Atualizando estoque do produto: {} para {}", id, stock);
        
        try {
            Product updatedProduct = productService.updateStock(id, stock);
            return ResponseEntity.ok(updatedProduct);
        } catch (IllegalArgumentException e) {
            logger.warn("Erro ao atualizar estoque: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Desativar produto", description = "Desativa um produto (soft delete)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produto desativado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Product> deactivateProduct(
            @Parameter(description = "ID do produto", required = true)
            @PathVariable String id) {
        
        logger.info("Desativando produto: {}", id);
        
        try {
            Product deactivatedProduct = productService.deactivateProduct(id);
            return ResponseEntity.ok(deactivatedProduct);
        } catch (IllegalArgumentException e) {
            logger.warn("Erro ao desativar produto: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Deletar produto", description = "Remove permanentemente um produto do sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Produto deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(
            @Parameter(description = "ID do produto", required = true)
            @PathVariable String id) {
        
        logger.info("Deletando produto: {}", id);
        
        try {
            productService.deleteProduct(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            logger.warn("Erro ao deletar produto: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Contar produtos por categoria", description = "Retorna o número de produtos em uma categoria")
    @ApiResponse(responseCode = "200", description = "Número de produtos na categoria")
    @GetMapping("/count/category/{category}")
    public ResponseEntity<Long> countProductsByCategory(
            @Parameter(description = "Nome da categoria", required = true)
            @PathVariable String category) {
        
        logger.info("Contando produtos por categoria: {}", category);
        
        long count = productService.countByCategory(category);
        return ResponseEntity.ok(count);
    }

    @Operation(summary = "Contar produtos disponíveis", description = "Retorna o número total de produtos disponíveis")
    @ApiResponse(responseCode = "200", description = "Número de produtos disponíveis")
    @GetMapping("/count")
    public ResponseEntity<Long> countAvailableProducts() {
        logger.info("Contando produtos disponíveis");
        
        long count = productService.countAvailableProducts();
        return ResponseEntity.ok(count);
    }

    @Operation(summary = "Limpar cache de produtos", description = "Remove todos os dados de produtos do cache Redis")
    @ApiResponse(responseCode = "200", description = "Cache limpo com sucesso")
    @PostMapping("/clear-cache")
    public ResponseEntity<String> clearCache() {
        logger.info("Limpando cache de produtos");
        
        productService.clearCache();
        return ResponseEntity.ok("Cache de produtos limpo com sucesso");
    }
}

