# ─────────────────────────────────────────────────────────────────────────────
# STAGE 1: Build Stage
# Uses a full Maven + JDK image to compile and package the application.
# This stage is ONLY for building — it won't be part of the final image.
# ─────────────────────────────────────────────────────────────────────────────
FROM maven:3.8.6-openjdk-11 AS builder

# Set the working directory inside the container
WORKDIR /app

# ── Layer caching trick ──────────────────────────────────────────────────────
# Copy ONLY pom.xml first and download dependencies.
# Docker caches this layer. If pom.xml doesn't change, Maven won't re-download
# on the next build — this makes rebuilds much faster!
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Now copy the source code and build the JAR
COPY src ./src
RUN mvn clean package -DskipTests -B

# ─────────────────────────────────────────────────────────────────────────────
# STAGE 2: Runtime Stage
# Uses a slim JRE image (no compiler, no Maven) — much smaller and more secure.
# Only the compiled JAR is copied from Stage 1.
# ─────────────────────────────────────────────────────────────────────────────
FROM eclipse-temurin:11-jre-alpine

# Add metadata labels (best practice)
LABEL maintainer="SPE Student"
LABEL description="Calculator REST API — SPE Mini-Project"
LABEL version="1.0.0"

# ── Security: run as non-root user ──────────────────────────────────────────
# Never run containers as root in production — security best practice.
# Create a dedicated user and group for the application.
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Set the working directory
WORKDIR /app

# Copy ONLY the executable JAR from the builder stage (not the full Maven tree)
COPY --from=builder /app/target/calculator-1.0.0.jar app.jar

# Give the app user ownership of the JAR
RUN chown appuser:appgroup app.jar

# Switch to the non-root user
USER appuser

# ── Expose the port the app listens on ─────────────────────────────────────
# This is documentation + allows Docker to map it. The app uses port 8080.
EXPOSE 8080

# ── Health check ────────────────────────────────────────────────────────────
# Docker itself will ping /actuator/health every 30s.
# After 3 failures, the container is marked "unhealthy".
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# ── Entry point ─────────────────────────────────────────────────────────────
# Run the Spring Boot fat JAR directly.
# Using exec form (JSON array) instead of shell form — best practice.
ENTRYPOINT ["java", "-jar", "app.jar"]
