# 🚀 Guia de Início Rápido

## Executar com Docker (Recomendado)

```bash
# 1. Construir e executar todos os serviços
docker-compose up -d

# 2. Verificar se os serviços estão rodando
docker-compose ps

# 3. Acessar a aplicação
# - API: http://localhost:8080/api
# - Swagger: http://localhost:8080/api/swagger-ui.html
# - MongoDB Express: http://localhost:8081 (admin/admin123)
# - Redis Commander: http://localhost:8082
```

## Executar Localmente

```bash
# 1. Instalar dependências
sudo apt update
sudo apt install -y mongodb redis-server maven

# 2. Iniciar serviços
sudo systemctl start mongodb redis-server

# 3. Compilar e executar
mvn clean compile
mvn spring-boot:run
```

## Testar a API

```bash
# Criar usuário
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "name": "João Silva",
    "email": "joao@example.com",
    "phone": "(11) 99999-9999"
  }'

# Listar usuários
curl http://localhost:8080/api/users

# Criar produto
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Notebook Dell",
    "description": "Notebook para desenvolvimento",
    "price": 2999.99,
    "category": "Eletrônicos",
    "stock": 10
  }'

# Testar API externa
curl http://localhost:8080/api/external/posts

# Verificar cache Redis
curl http://localhost:8080/api/cache/redis/stats
```

## URLs Importantes

- **Aplicação**: http://localhost:8080/api
- **Swagger UI**: http://localhost:8080/api/swagger-ui.html
- **Health Check**: http://localhost:8080/api/actuator/health
- **MongoDB Express**: http://localhost:8081
- **Redis Commander**: http://localhost:8082

## Parar Serviços

```bash
# Docker
docker-compose down

# Local
sudo systemctl stop mongodb redis-server
```

