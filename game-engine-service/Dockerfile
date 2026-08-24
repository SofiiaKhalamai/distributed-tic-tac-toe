FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY . .
RUN mvn -q -B -pl game-engine-service -am -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /workspace/game-engine-service/target/game-engine-service-1.0-SNAPSHOT-exec.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
