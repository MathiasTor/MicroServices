FROM openjdk:21

WORKDIR /app

COPY target/playpal-communication-service-0.0.1-SNAPSHOT.jar communication.jar


EXPOSE 3002

ENTRYPOINT ["java", "-jar", "communication.jar"]