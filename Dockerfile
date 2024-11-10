# Build stage
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy the parent POM
COPY habit-tracker-app/pom.xml .

# Copy all module POMs first
COPY audit-spring-boot-starter/pom.xml audit-spring-boot-starter/
COPY swagger-spring-boot-starter/pom.xml swagger-spring-boot-starter/
COPY habit-tracker-app/pom.xml habit-tracker-app/

# Copy source code
COPY audit-spring-boot-starter/src audit-spring-boot-starter/src/
COPY swagger-spring-boot-starter/src swagger-spring-boot-starter/src/
COPY habit-tracker-app/src habit-tracker-app/src/

# Build the complete project
RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Copy the JAR file from build stage
COPY --from=build /app/habit-tracker-app/target/habit-tracker.jar ./app.jar

# Set environment variables
ENV JAVA_OPTS="-Xms512m -Xmx1024m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=256m"
ENV SPRING_PROFILES_ACTIVE="dev"

# Install curl for health check
RUN apt-get update && \
    apt-get install -y curl && \
    rm -rf /var/lib/apt/lists/*

# Create directory for logs
RUN mkdir -p /app/logs && \
    chmod 777 /app/logs

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/api/system/health || exit 1

# Expose port
EXPOSE 8080

# Start application
CMD ["java", "-jar", "app.jar"]