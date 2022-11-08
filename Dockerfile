FROM gradle:jdk17-alpine as build
COPY src /home/app/src
COPY build.gradle.kts /home/app/build.gradle.kts
COPY settings.gradle.kts /home/app/settings.gradle.kts
RUN gradle clean build -p /home/app --no-daemon

FROM openjdk:17-jdk-slim
COPY --from=build /home/app/build/libs/techassingment-0.0.1-SNAPSHOT.jar /app.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","/app.jar"]
