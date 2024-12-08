FROM openjdk:21

WORKDIR /app

COPY target/playpal-group-service-0.0.1-SNAPSHOT.jar group.jar

EXPOSE 3005

ENTRYPOINT ["java", "-jar", "group.jar"]