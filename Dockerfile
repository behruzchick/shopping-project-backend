FROM ubuntu:latest AS build

RUN apt-get update && apt-get install -y openjdk-17-jdk

WORKDIR /app
COPY . .

RUN chmod +x gradlew

RUN java -version
RUN ./gradlew --version

RUN ./gradlew clean bootJar --no-daemon

FROM openjdk:17-jdk-slim

WORKDIR /app
EXPOSE 8080

COPY --from=build /app/build/libs/demo-1.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
