# Multi-stage Dockerfile for Gamma Service
# Stage 1: Build
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /build

# Copy parent pom and common-lib first (for better layer caching)
COPY pom.xml .
COPY common-lib common-lib/

# Copy gamma-service
COPY gamma-service gamma-service/

# Build the application (skip tests for faster builds)
RUN mvn clean package -pl gamma-service -am -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-alpine

# Add labels for metadata
LABEL maintainer="enterprise-team@enterprise.com"
LABEL service="gamma-service"
LABEL version="1.0.0"

# Create non-root user for security
RUN addgroup -g 1001 appgroup && \
    adduser -D -u 1001 -G appgroup appuser

WORKDIR /app

# Copy the built JAR from builder stage
COPY --from=builder /build/gamma-service/target/gamma-service.jar app.jar

# Change ownership to non-root user
RUN chown -R appuser:appgroup /app

# Switch to non-root user
USER appuser

# Expose port
EXPOSE 8083

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost:8083/actuator/health || exit 1

# Set JVM options for containerized environment
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+UseG1GC"

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
