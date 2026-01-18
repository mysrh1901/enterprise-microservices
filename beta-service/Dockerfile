# Multi-stage Dockerfile for Beta Service
# Supports both V1 and V2 deployments via API_VERSION env variable
# Stage 1: Build
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /build

# Copy parent pom and common-lib first (for better layer caching)
COPY pom.xml .
COPY common-lib common-lib/

# Copy beta-service
COPY beta-service beta-service/

# Build the application (skip tests for faster builds)
RUN mvn clean package -pl beta-service -am -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-alpine

# Add labels for metadata
LABEL maintainer="enterprise-team@enterprise.com"
LABEL service="beta-service"
LABEL version="1.0.0"

# Create non-root user for security
RUN addgroup -g 1001 appgroup && \
    adduser -D -u 1001 -G appgroup appuser

WORKDIR /app

# Copy the built JAR from builder stage
COPY --from=builder /build/beta-service/target/beta-service.jar app.jar

# Change ownership to non-root user
RUN chown -R appuser:appgroup /app

# Switch to non-root user
USER appuser

# Expose port
EXPOSE 8082

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost:8082/actuator/health || exit 1

# Set JVM options for containerized environment
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+UseG1GC"

# API_VERSION can be set to 'v1' or 'v2' during deployment
# This helps with logging and metrics differentiation
ENV API_VERSION=v1

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dapi.version=$API_VERSION -jar app.jar"]
