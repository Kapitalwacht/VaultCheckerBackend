# Dockerfile for the VaultChecker Platform
#
# Multi-stage build: compiles the Spring Boot application with Maven and runs it
# on a lightweight Temurin 26 JRE. The 'prod' profile is active at runtime and the
# service listens on the port defined by the PORT variable.

# Step 1: Build the application using a Maven image that bundles Maven and a Temurin 26 JDK.
FROM maven:3.9.16-eclipse-temurin-26 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests -B

# Step 2: Create the runtime image
FROM eclipse-temurin:26-jre AS runtime
ENV SPRING_PROFILES_ACTIVE=prod
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Step 3: Configure and run the application
EXPOSE 8092
ENTRYPOINT ["java", "-jar", "app.jar"]

# Runtime configuration comes from two environment variables (see .env.production):
# - DATABASE_URL  Full JDBC URL including credentials, e.g.
#                 jdbc:postgresql://host:5432/vaultchecker?user=...&password=...
# - JWT_SECRET    Secret used to sign JWT tokens.
# Optional:
# - PORT          Port the application listens on (default 8092).
