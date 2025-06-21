package com.example.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.mapping.event.ValidatingMongoEventListener;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Optional;

/**
 * Configuração do MongoDB
 * 
 * Esta classe configura:
 * - Conversores customizados
 * - Validação de entidades
 * - Auditoria automática
 */
@Configuration
public class MongoConfig extends AbstractMongoClientConfiguration {

    @Override
    protected String getDatabaseName() {
        return "springboot_db";
    }

    /**
     * Configuração de conversores customizados para MongoDB
     */
    @Bean
    @Override
    public MongoCustomConversions customConversions() {
        return new MongoCustomConversions(Arrays.asList(
            // Converter para ZonedDateTime
            new ZonedDateTimeReadConverter(),
            new ZonedDateTimeWriteConverter()
        ));
    }

    /**
     * Validação de entidades antes de salvar no MongoDB
     */
    @Bean
    public ValidatingMongoEventListener validatingMongoEventListener() {
        return new ValidatingMongoEventListener(validator());
    }

    @Bean
    public LocalValidatorFactoryBean validator() {
        return new LocalValidatorFactoryBean();
    }

    /**
     * Configuração de auditoria - quem criou/modificou
     */
    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> Optional.of("system"); // Em produção, pegar do contexto de segurança
    }

    /**
     * Converter para leitura de ZonedDateTime
     */
    private static class ZonedDateTimeReadConverter implements org.springframework.core.convert.converter.Converter<java.util.Date, ZonedDateTime> {
        @Override
        public ZonedDateTime convert(java.util.Date date) {
            return date.toInstant().atZone(ZoneOffset.UTC);
        }
    }

    /**
     * Converter para escrita de ZonedDateTime
     */
    private static class ZonedDateTimeWriteConverter implements org.springframework.core.convert.converter.Converter<ZonedDateTime, java.util.Date> {
        @Override
        public java.util.Date convert(ZonedDateTime zonedDateTime) {
            return java.util.Date.from(zonedDateTime.toInstant());
        }
    }
}

