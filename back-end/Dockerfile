# Estágio 1: Build (Construção)
FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /app

# Copia os arquivos de configuração do Maven
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Dá permissão ao wrapper e baixa dependências (cache otimizado do Docker)
RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline -B

# Copia todo o código fonte e gera o .jar
COPY src src
RUN ./mvnw package -DskipTests

# Estágio 2: Run (Execução)
# Usa apenas a JRE (menor e mais leve) em vez da JDK completa
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Copia o .jar gerado no Estágio 1
COPY --from=build /app/target/*.jar app.jar

# A porta padrão do Spring Boot
EXPOSE 8080

# Comando para iniciar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]
