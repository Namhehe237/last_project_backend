# ===== Stage 1: Build =====
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

# Copy Maven wrapper và pom.xml
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

RUN chmod +x mvnw

# Download dependencies
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src ./src

# Build ứng dụng
RUN ./mvnw clean package -DskipTests

# ===== Stage 2: Run =====
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy file jar từ stage build sang stage run
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

# Health check (optional)
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run ứng dụng
CMD ["java", "-jar", "app.jar"]
