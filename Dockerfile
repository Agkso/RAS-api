# --- Stage 1: Build da aplicação com Maven ---
FROM maven:3.9.6-amazoncorretto-21 AS builder

# Copia o código-fonte
WORKDIR /app
COPY . .

# Build do projeto (gera o .jar em /app/target)
RUN mvn clean package -DskipTests

# --- Stage 2: Runtime com Corretto 21 ---
FROM amazoncorretto:21

WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]