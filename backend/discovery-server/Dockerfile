FROM maven:3.9.9-eclipse-temurin-17 AS builder
WORKDIR /build

COPY pom.xml .
COPY src ./src

RUN mvn -DskipTests clean package
RUN cp "$(find target -maxdepth 1 -name '*.jar' ! -name '*.original' -print -quit)" /build/app.jar

FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=builder /build/app.jar /app/app.jar

EXPOSE 8761

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
