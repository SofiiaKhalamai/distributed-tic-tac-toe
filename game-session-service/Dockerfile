FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY . .
# maven.test.skip (not just skipTests) so test sources aren't compiled — game-session-service has a
# test-scope dependency on game-engine-service (used only by CrossServiceIntegrationTest) that would
# otherwise have to be built into this image for no reason.
RUN mvn -q -B -pl game-session-service -am -Dmaven.test.skip=true package

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /workspace/game-session-service/target/game-session-service-1.0-SNAPSHOT.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]
