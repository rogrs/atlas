# Spring Boot Application

Uma aplicação Java completa demonstrando integração com MongoDB, Redis, cliente REST e documentação Swagger usando Spring Boot 3.4.4 e Java 21.

## 🚀 Características

- **Spring Boot 3.4.4** com Java 21
- **MongoDB** como banco de dados principal
- **Redis** para cache e sessões
- **WebClient** para consumo de APIs REST externas
- **Swagger/OpenAPI** para documentação da API
- **Validação** de dados com Bean Validation
- **Auditoria** automática de entidades
- **Cache** distribuído com Redis
- **Testes** unitários e de integração

## 📋 Pré-requisitos

- Java 21 ou superior
- Maven 3.6 ou superior
- MongoDB 4.4 ou superior
- Redis 6.0 ou superior

## 🛠️ Instalação

### 1. Clone o repositório
```bash
git clone <repository-url>
cd springboot-app
```

### 2. Configure o MongoDB
```bash
# Instalar MongoDB (Ubuntu/Debian)
sudo apt update
sudo apt install -y mongodb

# Iniciar serviço
sudo systemctl start mongodb
sudo systemctl enable mongodb
```

### 3. Configure o Redis
```bash
# Instalar Redis (Ubuntu/Debian)
sudo apt update
sudo apt install -y redis-server

# Iniciar serviço
sudo systemctl start redis-server
sudo systemctl enable redis-server
```

### 4. Compile e execute a aplicação
```bash
# Compilar
mvn clean compile

# Executar
mvn spring-boot:run

# Ou executar com perfil de desenvolvimento
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

## ⚙️ Configuração

### Arquivo application.yml
```yaml
server:
  port: 8080

spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/springboot_db
  redis:
    host: localhost
    port: 6379
```

### Variáveis de Ambiente
```bash
export MONGODB_URI=mongodb://localhost:27017/springboot_db
export REDIS_HOST=localhost
export REDIS_PORT=6379
```

## 📚 Documentação da API

Após iniciar a aplicação, acesse:

- **Swagger UI**: http://localhost:8080/api/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api/api-docs
- **Actuator Health**: http://localhost:8080/api/actuator/health

## 🔗 Endpoints Principais

### Usuários
- `GET /api/users` - Listar usuários ativos
- `GET /api/users/{id}` - Buscar usuário por ID
- `POST /api/users` - Criar novo usuário
- `PUT /api/users/{id}` - Atualizar usuário
- `DELETE /api/users/{id}` - Deletar usuário
- `PATCH /api/users/{id}/deactivate` - Desativar usuário

### Produtos
- `GET /api/products` - Listar produtos disponíveis
- `GET /api/products/{id}` - Buscar produto por ID
- `POST /api/products` - Criar novo produto
- `PUT /api/products/{id}` - Atualizar produto
- `DELETE /api/products/{id}` - Deletar produto
- `GET /api/products/category/{category}` - Buscar por categoria

### API Externa
- `GET /api/external/posts` - Listar posts da API externa
- `GET /api/external/posts/{id}` - Buscar post por ID
- `GET /api/external/users` - Listar usuários da API externa
- `POST /api/external/posts` - Criar post na API externa

### Cache
- `GET /api/cache/names` - Listar caches disponíveis
- `DELETE /api/cache/{cacheName}` - Limpar cache específico
- `DELETE /api/cache/all` - Limpar todos os caches
- `GET /api/cache/redis/stats` - Estatísticas do Redis

## 🏗️ Estrutura do Projeto

```
src/
├── main/
│   ├── java/com/example/app/
│   │   ├── SpringBootAppApplication.java    # Classe principal
│   │   ├── config/                          # Configurações
│   │   │   ├── AppConfig.java
│   │   │   ├── MongoConfig.java
│   │   │   ├── RedisConfig.java
│   │   │   ├── WebClientConfig.java
│   │   │   └── OpenApiConfig.java
│   │   ├── entity/                          # Entidades JPA/MongoDB
│   │   │   ├── BaseEntity.java
│   │   │   ├── User.java
│   │   │   └── Product.java
│   │   ├── repository/                      # Repositórios
│   │   │   ├── UserRepository.java
│   │   │   └── ProductRepository.java
│   │   ├── service/                         # Serviços de negócio
│   │   │   ├── UserService.java
│   │   │   └── ProductService.java
│   │   ├── controller/                      # Controllers REST
│   │   │   ├── UserController.java
│   │   │   ├── ProductController.java
│   │   │   ├── ExternalApiController.java
│   │   │   └── CacheController.java
│   │   ├── client/                          # Clientes REST
│   │   │   └── ExternalApiClient.java
│   │   └── dto/                             # DTOs
│   │       ├── ExternalPostDto.java
│   │       └── ExternalUserDto.java
│   └── resources/
│       ├── application.yml                  # Configuração principal
│       └── application-dev.yml              # Configuração desenvolvimento
└── test/
    ├── java/com/example/app/
    │   └── SpringBootAppApplicationTests.java
    └── resources/
        └── application-test.yml             # Configuração testes
```

## 🧪 Testes

```bash
# Executar todos os testes
mvn test

# Executar testes com relatório de cobertura
mvn test jacoco:report

# Executar apenas testes unitários
mvn test -Dtest="*Test"

# Executar apenas testes de integração
mvn test -Dtest="*IT"
```

## 🐳 Docker

### Dockerfile
```dockerfile
FROM openjdk:21-jdk-slim

WORKDIR /app
COPY target/springboot-app-1.0.0.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Docker Compose
```yaml
version: '3.8'
services:
  app:
    build: .
    ports:
      - "8080:8080"
    depends_on:
      - mongodb
      - redis
    environment:
      - MONGODB_URI=mongodb://mongodb:27017/springboot_db
      - REDIS_HOST=redis

  mongodb:
    image: mongo:7
    ports:
      - "27017:27017"
    volumes:
      - mongodb_data:/data/db

  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"

volumes:
  mongodb_data:
```

## 🔧 Desenvolvimento

### Executar em modo desenvolvimento
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Hot reload
O DevTools está configurado para recarregar automaticamente as alterações durante o desenvolvimento.

### Logs
Os logs são configurados para diferentes níveis:
- **DEBUG**: Pacote `com.example.app`
- **INFO**: Aplicação geral
- **WARN**: Frameworks

## 📊 Monitoramento

### Actuator Endpoints
- `/actuator/health` - Status da aplicação
- `/actuator/info` - Informações da aplicação
- `/actuator/metrics` - Métricas da aplicação
- `/actuator/prometheus` - Métricas para Prometheus

### Cache Monitoring
- Estatísticas do Redis via endpoint `/api/cache/redis/stats`
- Limpeza de cache via endpoints `/api/cache/*`

## 🚀 Deploy

### Build para produção
```bash
mvn clean package -Pprod
```

### Variáveis de ambiente para produção
```bash
export SPRING_PROFILES_ACTIVE=prod
export MONGODB_URI=mongodb://prod-mongo:27017/springboot_prod
export REDIS_HOST=prod-redis
export REDIS_PORT=6379
```

## 🤝 Contribuição

1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

## 📝 Licença

Este projeto está licenciado sob a Licença MIT - veja o arquivo [LICENSE](LICENSE) para detalhes.

## 📞 Suporte

Para suporte, envie um email para dev@example.com ou abra uma issue no GitHub.

## 🔄 Changelog

### v1.0.0
- Implementação inicial
- Integração MongoDB e Redis
- Cliente REST com WebClient
- Documentação Swagger/OpenAPI
- Cache distribuído
- Testes unitários e de integração

