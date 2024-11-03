# Build stage
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Run stage
FROM tomcat:10.1-jdk17-openjdk-slim

# Remove default Tomcat applications
RUN rm -rf /usr/local/tomcat/webapps/*

# Copy the WAR file from build stage
COPY --from=build /app/target/habit-tracker.war /usr/local/tomcat/webapps/ROOT.war

# Set environment variables
ENV JAVA_OPTS="-Xms512m -Xmx1024m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=256m"
ENV CATALINA_OPTS="-Dspring.profiles.active=dev"

# Install curl for health check
RUN apt-get update && \
    apt-get install -y curl && \
    rm -rf /var/lib/apt/lists/*

# Optional: Copy custom Tomcat configuration if needed
# COPY conf/server.xml /usr/local/tomcat/conf/
# COPY conf/context.xml /usr/local/tomcat/conf/

# Create directory for logs
RUN mkdir -p /usr/local/tomcat/logs && \
    chmod 777 /usr/local/tomcat/logs

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/api/system/health || exit 1

# Expose port
EXPOSE 8080

# Start Tomcat
CMD ["catalina.sh", "run"]