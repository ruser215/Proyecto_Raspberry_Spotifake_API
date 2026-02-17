FROM arm32v7/eclipse-temurin:17-jre

WORKDIR /app
EXPOSE 8001

COPY build/libs/*.jar /app/ktor-app.jar

ENTRYPOINT ["java", "-jar", "/app/ktor-app.jar"]

