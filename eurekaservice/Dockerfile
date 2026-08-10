# Build Stage
FROM eclipse-temurin:25-jdk AS builder

WORKDIR /app

COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
COPY src src

RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests


# Runtime Stage
FROM eclipse-temurin:25-jre

WORKDIR /app

COPY --from=builder /app/target/eureka-service.jar eureka-service.jar

EXPOSE 8761

ENTRYPOINT ["java", "-jar", "eureka-service.jar"]