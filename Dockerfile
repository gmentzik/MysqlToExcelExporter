
# ===== Stage 1: Build =====
FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /build

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source and build
COPY src ./src
RUN mvn clean package -DskipTests

# ===== Stage 2: Runtime =====
#FROM eclipse-temurin:21-jdk AS runtime
FROM eclipse-temurin:21-jre-alpine AS runtime
WORKDIR /app

# Copy JAR from builder
ARG JAR_FILE=DBExporter*.jar
COPY --from=builder /build/target/${JAR_FILE} app.jar

# Add labels
LABEL maintainer="Georgios Mentzikof"
LABEL version="1.x.x"
LABEL description="Mysql DB to excel exporter"

# Default command
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
