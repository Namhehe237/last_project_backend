# ===== BUILD STAGE =====
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

# Copy Maven wrapper & pom
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Fix CRLF trên Windows
RUN sed -i 's/\r$//' mvnw
RUN chmod +x mvnw

# Download dependencies trước
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src src

# Build application
RUN ./mvnw clean package -DskipTests

# ===== RUN STAGE =====
FROM eclipse-temurin:21-jdk

WORKDIR /app

# Copy jar từ stage build
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
