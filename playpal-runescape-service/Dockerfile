FROM openjdk:21

WORKDIR /app

COPY target/playpal-runescape-service-0.0.1-SNAPSHOT.jar runescape.jar

EXPOSE 8089

ENTRYPOINT ["java", "-jar", "runescape.jar"]