FROM openjdk:21

WORKDIR /app

COPY target/playpal-search-service-0.0.1-SNAPSHOT.jar search.jar

EXPOSE 9090

ENTRYPOINT ["java", "-jar", "search.jar"]