package com.example.app.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuração geral da aplicação
 */
@Configuration
public class AppConfig {

    /**
     * Configuração CORS para permitir requisições de diferentes origens
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                        .allowedOrigins("*")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(false);
            }
        };
    }

    /**
     * Propriedades customizadas da aplicação
     */
    @Bean
    @ConfigurationProperties(prefix = "app")
    public AppProperties appProperties() {
        return new AppProperties();
    }

    /**
     * Classe para propriedades customizadas
     */
    public static class AppProperties {
        private ExternalApi externalApi = new ExternalApi();
        private Cache cache = new Cache();

        public ExternalApi getExternalApi() {
            return externalApi;
        }

        public void setExternalApi(ExternalApi externalApi) {
            this.externalApi = externalApi;
        }

        public Cache getCache() {
            return cache;
        }

        public void setCache(Cache cache) {
            this.cache = cache;
        }

        public static class ExternalApi {
            private String baseUrl;
            private int timeout;
            private int retryAttempts;

            public String getBaseUrl() {
                return baseUrl;
            }

            public void setBaseUrl(String baseUrl) {
                this.baseUrl = baseUrl;
            }

            public int getTimeout() {
                return timeout;
            }

            public void setTimeout(int timeout) {
                this.timeout = timeout;
            }

            public int getRetryAttempts() {
                return retryAttempts;
            }

            public void setRetryAttempts(int retryAttempts) {
                this.retryAttempts = retryAttempts;
            }
        }

        public static class Cache {
            private int defaultTtl;
            private int userTtl;

            public int getDefaultTtl() {
                return defaultTtl;
            }

            public void setDefaultTtl(int defaultTtl) {
                this.defaultTtl = defaultTtl;
            }

            public int getUserTtl() {
                return userTtl;
            }

            public void setUserTtl(int userTtl) {
                this.userTtl = userTtl;
            }
        }
    }
}

