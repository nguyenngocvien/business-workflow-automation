FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /workspace

COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn
COPY src src

RUN chmod +x mvnw && ./mvnw -q -DskipTests package

FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY --from=build /workspace/target /workspace/target
RUN set -eux; \
    JAR_FILE="$(find /workspace/target -maxdepth 1 -name '*.jar' ! -name '*.original.jar' | head -n 1)"; \
    cp "$JAR_FILE" /app/app.jar; \
    rm -rf /workspace/target

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
