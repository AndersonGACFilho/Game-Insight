# Estágio 1: Build
FROM maven:3.8.6-openjdk-18-slim AS build

# Copie todos os arquivos para o contêiner
COPY .. .

# Liste os arquivos e diretórios para inspeção
RUN ls -R

# Empacote a aplicação
RUN mvn clean package -DskipTests

# Exponha a porta da aplicação
EXPOSE 8080

# Comando para rodar a aplicação
ENTRYPOINT ["java", "-jar", "target/GameService-0.0.1-SNAPSHOT.jar"]
