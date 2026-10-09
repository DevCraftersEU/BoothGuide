FROM maven:3.9.12-eclipse-temurin-21-alpine AS build-stage

WORKDIR /app
COPY pom.xml ./
COPY src ./src
RUN mvn --batch-mode package -DskipTests

FROM eclipse-temurin:21.0.12_8-jre-alpine-3.24 AS production-stage

COPY --from=build-stage /app/target/*.jar /app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]
