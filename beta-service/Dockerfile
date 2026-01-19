# ================================
# Multi-stage Dockerfile - beta-service
# ================================

# ---------- Stage 1: Build ----------
FROM gradle:8.5-jdk17 AS builder

WORKDIR /build

# Copy Gradle wrapper and configs first (better caching)
COPY gradle gradle/
COPY gradlew .
COPY gradlew.bat .
COPY build.gradle .
COPY settings.gradle .

# Copy shared module
COPY common-lib common-lib/

# Copy service
COPY beta-service beta-service/

# Build fat jar
RUN gradle :beta-service:bootJar --no-daemon -x test


# ---------- Stage 2: Runtime ----------
FROM eclipse-temurin:17-jre-alpine

LABEL maintainer="enterprise-team@enterprise.com"
LABEL service="beta-service"
LABEL version="1.0.0"

# Install wget for healthcheck
RUN apk add --no-cache wget

# Create non-root user
RUN addgroup -g 1001 appgroup && \
    adduser -D -u 1001 -G appgroup appuser

WORKDIR /app

# Copy JAR
COPY --from=builder /build/beta-service/build/libs/beta-service.jar app.jar

# Fix permissions
RUN chown -R appuser:appgroup /app

USER appuser

# Container listens on 8080
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# JVM tuning
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+UseG1GC"

# API versioning (optional)
ENV API_VERSION=v1

# Start app
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dapi.version=$API_VERSION -jar app.jar"]
