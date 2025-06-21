package com.example.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

/**
 * Classe principal da aplicação Spring Boot
 * 
 * Esta aplicação demonstra a integração de:
 * - MongoDB como banco de dados principal
 * - Redis para cache
 * - WebClient para consumo de APIs REST
 * - Swagger/OpenAPI para documentação
 */
@SpringBootApplication
@EnableCaching
@EnableMongoAuditing
public class SpringBootAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootAppApplication.class, args);
    }
}

