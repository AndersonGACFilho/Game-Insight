# Use an official OpenJDK runtime as a parent image
FROM openjdk:21-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the Maven build file to the container
COPY pom.xml .

# Copy the source code to the container
COPY src ./src

# Package the application
RUN ./mvnw clean package -DskipTests

# Expose the application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "target/UserService-0.0.1-SNAPSHOT.jar"]
