# Use uma imagem oficial do Maven para construir o aplicativo
FROM maven:3.8.6-openjdk-21-slim AS build

# Defina o diretório de trabalho
WORKDIR /UserService

# Copie o arquivo pom.xml e as dependências do Maven
COPY pom.xml .
COPY src ./src

# Empacote a aplicação
RUN mvn clean package -DskipTests

# Use uma imagem do OpenJDK para rodar a aplicação
FROM openjdk:21-jdk-slim

# Defina o diretório de trabalho no contêiner
WORKDIR /UserService

# Copie o JAR gerado pelo Maven na etapa anterior
COPY --from=build /app/target/UserService-0.0.1-SNAPSHOT.jar /app/UserService.jar

# Exponha a porta da aplicação
EXPOSE 8080

# Comando para rodar a aplicação
ENTRYPOINT ["java", "-jar", "UserService.jar"]
