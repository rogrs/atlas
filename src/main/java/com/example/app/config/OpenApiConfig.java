package com.example.app.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuração do OpenAPI/Swagger para documentação da API
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Spring Boot Application API")
                        .description("API completa demonstrando integração com MongoDB, Redis, Cliente REST e documentação Swagger")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Equipe de Desenvolvimento")
                                .email("dev@example.com")
                                .url("https://example.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080/api")
                                .description("Servidor de Desenvolvimento"),
                        new Server()
                                .url("https://api.example.com")
                                .description("Servidor de Produção")))
                .tags(List.of(
                        new Tag()
                                .name("Users")
                                .description("Operações relacionadas a usuários"),
                        new Tag()
                                .name("Products")
                                .description("Operações relacionadas a produtos"),
                        new Tag()
                                .name("External API")
                                .description("Integração com APIs externas"),
                        new Tag()
                                .name("Cache")
                                .description("Operações de cache Redis"),
                        new Tag()
                                .name("Health")
                                .description("Monitoramento e saúde da aplicação")));
    }
}

