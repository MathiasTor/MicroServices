FROM openjdk:21

WORKDIR /app

COPY target/playpal-recorder-0.0.1-SNAPSHOT.jar recorder.jar

EXPOSE 3006

ENTRYPOINT ["java", "-jar", "recorder.jar"]