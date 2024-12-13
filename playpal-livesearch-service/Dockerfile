FROM openjdk:21

WORKDIR /app

COPY target/playpal-livesearch-service-0.0.1-SNAPSHOT.jar livesearch.jar

EXPOSE 9099

ENTRYPOINT ["java", "-jar", "livesearch.jar"]