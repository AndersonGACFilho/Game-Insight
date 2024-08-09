# Estágio 1: Build
FROM maven:3.8.6-openjdk-18-slim AS build

# Copie todos os arquivos para o contêiner
COPY . .

# Defina o diretório de trabalho no contêiner
WORKDIR /UserService

# Liste os arquivos e diretórios para inspeção
RUN ls -R

# Empacote a aplicação
RUN mvn clean package -DskipTests

# Exponha a porta da aplicação
EXPOSE 8080

# Comando para rodar a aplicação
ENTRYPOINT ["java", "-jar", "target/UserService-0.0.1-SNAPSHOT.jar"]
