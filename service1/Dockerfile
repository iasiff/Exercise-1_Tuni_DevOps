FROM  maven:3-eclipse-temurin-21-alpine AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package

FROM bellsoft/liberica-openjdk-alpine-musl:21
RUN addgroup -S mygrp && adduser -S appusr -G mygrp
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
RUN mkdir -p /app/vstorage && chown -R appusr:mygrp /app/vstorage
USER appusr
EXPOSE 8080
CMD ["java", "-XX:+UseContainerSupport", "-XX:+UseG1GC", "-XX:MaxRAM=256m", "-XX:+AlwaysPreTouch", "-jar", "app.jar"]