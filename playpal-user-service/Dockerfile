FROM openjdk:21

WORKDIR /app

COPY target/playpal-user-service-0.0.1-SNAPSHOT.jar user.jar

EXPOSE 9090

ENTRYPOINT ["java", "-jar", "user.jar"]