# Dockerfile for Beta Service

# Stage 1: Build
FROM gradle:8.5-jdk17 AS builder
WORKDIR /build
COPY gradle gradle/
COPY gradlew .
COPY gradlew.bat .
COPY build.gradle .
COPY settings.gradle .
COPY common-lib common-lib/
COPY beta-service beta-service/
RUN gradle :beta-service:bootJar --no-daemon -x test

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-alpine
LABEL maintainer="enterprise-team@enterprise.com"
LABEL service="beta-service"
LABEL version="1.0.0"
RUN addgroup -g 1001 appgroup && adduser -D -u 1001 -G appgroup appuser
WORKDIR /app
COPY --from=builder /build/beta-service/build/libs/beta-service.jar app.jar
RUN chown -R appuser:appgroup /app
USER appuser
EXPOSE 8081
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost:8081/actuator/health || exit 1
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+UseG1GC"
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
