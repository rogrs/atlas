# Usar imagem base do OpenJDK 21
FROM openjdk:21-jdk-slim

# Definir diretório de trabalho
WORKDIR /app

# Copiar arquivo JAR da aplicação
COPY target/springboot-app-1.0.0.jar app.jar

# Expor porta da aplicação
EXPOSE 8080

# Definir variáveis de ambiente padrão
ENV SPRING_PROFILES_ACTIVE=docker
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Comando para executar a aplicação
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

# Adicionar healthcheck
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/api/actuator/health || exit 1

