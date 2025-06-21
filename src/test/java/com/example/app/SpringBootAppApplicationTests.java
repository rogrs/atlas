package com.example.app;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Teste básico para verificar se a aplicação Spring Boot carrega corretamente
 */
@SpringBootTest
@ActiveProfiles("test")
class SpringBootAppApplicationTests {

    @Test
    void contextLoads() {
        // Este teste verifica se o contexto da aplicação carrega sem erros
        // Se chegou até aqui, significa que todas as configurações estão corretas
    }
}

