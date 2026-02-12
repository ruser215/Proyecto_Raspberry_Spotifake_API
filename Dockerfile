# Paso 1: Build con Gradle (Usamos una imagen que soporte ARMv7)
FROM gradle:8.5-jdk21 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
# Usamos el gradlew del propio proyecto para asegurar compatibilidad
RUN ./gradlew buildFatJar --no-daemon

# Paso 2: Ejecución (Cambiamos openjdk por eclipse-temurin, que es mejor para ARM)
FROM eclipse-temurin:21-jre-focal
EXPOSE 8080
RUN mkdir /app
COPY --from=build /home/gradle/src/build/libs/*.jar /app/ktor-app.jar
ENTRYPOINT ["java", "-jar", "/app/ktor-app.jar"]