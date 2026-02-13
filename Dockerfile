# ---------- Paso 1: Build ----------
# Imagen compatible ARMv7
FROM arm32v7/gradle:8-jdk17 AS build

COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src

# Usamos el wrapper del proyecto
RUN ./gradlew buildFatJar --no-daemon


# ---------- Paso 2: Ejecución ----------
FROM arm32v7/openjdk:17-jdk-slim

WORKDIR /app
EXPOSE 8001

COPY --from=build /home/gradle/src/build/libs/*.jar /app/ktor-app.jar

ENTRYPOINT ["java", "-jar", "/app/ktor-app.jar"]
