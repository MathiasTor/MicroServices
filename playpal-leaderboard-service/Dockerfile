FROM openjdk:21

WORKDIR /app

COPY target/playpal-leaderboard-service-0.0.1-SNAPSHOT.jar leaderboard.jar

EXPOSE 8060

ENTRYPOINT ["java", "-jar", "leaderboard.jar"]