FROM openjdk:21

WORKDIR /app

COPY target/playpal-profile-service-0.0.1-SNAPSHOT.jar profile.jar

EXPOSE 8087

ENTRYPOINT ["java", "-jar", "profile.jar"]