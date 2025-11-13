# Sử dụng OpenJDK 21 làm base image
FROM eclipse-temurin:21-jdk

# Thiết lập thông tin metadata
LABEL maintainer="Online Exam Platform"
LABEL version="1.0"
LABEL description="Spring Boot application for Online Exam Platform"

# Cài đặt curl cho health check
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Tạo thư mục làm việc
WORKDIR /app

# Copy Maven wrapper và pom.xml
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Cấp quyền thực thi cho Maven wrapper
RUN chmod +x mvnw

# Download dependencies (cache layer)
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src src

# Build ứng dụng
RUN ./mvnw clean package -DskipTests

# Tạo thư mục cho logs
RUN mkdir -p /app/logs

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Command để chạy ứng dụng
CMD ["java", "-jar", "target/exam-test-0.0.1-SNAPSHOT.jar"] 
