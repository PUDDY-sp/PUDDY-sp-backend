# FROM openjdk:17
# WORKDIR /app
# COPY ./build/libs/puddy-0.0.1-SNAPSHOT.jar app.jar
# ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]

FROM gradle:8.5-jdk17 AS builder
COPY . /usr/src
WORKDIR /usr/src
RUN gradle wrapper --gradle-version 8.5
RUN ./gradlew clean build -x test

FROM openjdk:17-jdk-alpine
COPY --from=builder /usr/src/build/libs/puddy-0.0.1-SNAPSHOT.jar /usr/app/app.jar
COPY src/main/resources/application-prod.yml /app/src/main/resources/
ENTRYPOINT ["java", "-jar", "/usr/app/app.jar", "--spring.profiles.active=prod"]
