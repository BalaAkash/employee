# Stage 1: Build stage
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /build

# Copy Maven configuration
COPY .mvn .mvn
COPY mvnw mvnw
COPY pom.xml pom.xml

# Download dependencies (cached layer)
RUN mvn dependency:go-offline -B || true

# Copy source code
COPY src src

# Build application
RUN mvn clean package -DskipTests -q

# Stage 2: Runtime stage
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Copy JAR from build stage
COPY --from=builder /build/target/*.jar app.jar

# Expose port
EXPOSE 8080

# Run application
ENTRYPOINT ["java", "-jar", "app.jar"]
