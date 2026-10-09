FROM maven:3.9-eclipse-temurin-26-alpine AS build-stage

WORKDIR /app
COPY pom.xml ./
COPY src ./src
RUN mvn --batch-mode package -DskipTests

FROM eclipse-temurin:24-jre-alpine-3.22 AS production-stage

COPY --from=build-stage /app/target/*.jar /app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]
