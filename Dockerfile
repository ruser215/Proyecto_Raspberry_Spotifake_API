# ---------- Paso 1: Build ----------
FROM arm32v7/gradle:8-jdk17 AS build

COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src

RUN ./gradlew buildFatJar --no-daemon


# ---------- Paso 2: Ejecución ----------
FROM arm32v7/eclipse-temurin:17-jre

WORKDIR /app
EXPOSE 8001

COPY --from=build /home/gradle/src/build/libs/*.jar /app/ktor-app.jar

ENTRYPOINT ["java", "-jar", "/app/ktor-app.jar"]

