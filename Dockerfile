FROM gradle:8.14.3-jdk21-alpine AS build
WORKDIR /app
COPY --chown=gradle:gradle . /app
RUN gradle buildFatJar --no-daemon

FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=build /app/build/libs/*.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]