# ================================
# Multi-stage Dockerfile - alpha-service
# ================================

# ---------- Stage 1: Build ----------
FROM gradle:8.5-jdk17 AS builder

WORKDIR /build

# Copy Gradle wrapper and configs
COPY gradle gradle/
COPY gradlew .
COPY gradlew.bat .
COPY build.gradle .
COPY settings.gradle .

# Copy shared module
COPY common-lib common-lib/

# Copy service module
COPY alpha-service alpha-service/

# Build jar
RUN gradle :alpha-service:bootJar --no-daemon -x test


# ---------- Stage 2: Runtime ----------
FROM eclipse-temurin:17-jre-alpine

LABEL maintainer="enterprise-team@enterprise.com"
LABEL service="alpha-service"
LABEL version="1.0.0"

# 🔑 REQUIRED for HEALTHCHECK
RUN apk add --no-cache wget

# Create non-root user
RUN addgroup -g 1001 appgroup && \
    adduser -D -u 1001 -G appgroup appuser

WORKDIR /app

# Copy jar
COPY --from=builder /build/alpha-service/build/libs/alpha-service.jar app.jar

# Fix permissions
RUN chown -R appuser:appgroup /app

USER appuser

# App port (same for all services)
EXPOSE 8080

# Healthcheck
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# JVM options
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+UseG1GC"

# Start app
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
