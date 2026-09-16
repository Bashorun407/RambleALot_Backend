# Stage 1: Build the application using Maven
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Copy the pom.xml and download dependencies (this caches the layer to speed up builds)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy the source code and build the executable JAR
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Create the minimal production image
FROM amazoncorretto:21-alpine
WORKDIR /app

# Copy the built JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the port Spring Boot runs on
EXPOSE 8080

# Execute the application
ENTRYPOINT ["java", "-jar", "app.jar"]