FROM  maven:3-eclipse-temurin-21-alpine AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package

FROM bellsoft/liberica-openjdk-alpine-musl:21

WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
RUN mkdir -p /app/vstorage
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]