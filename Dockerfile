# Usa a imagem oficial do Amazon Corretto 21 (Java 21)
FROM amazoncorretto:21

# Define o diretório de trabalho dentro do contêiner
WORKDIR /app

# Copia o arquivo JAR da sua aplicação para dentro do contêiner
# Certifique-se de que o build gerou o .jar corretamente em build/libs/
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

# Expõe a porta padrão (ajuste conforme necessário)
EXPOSE 8080

# Comando para iniciar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]
