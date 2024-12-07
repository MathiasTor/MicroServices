FROM openjdk:21

WORKDIR /app

COPY target/friendservice-0.0.1-SNAPSHOT.jar friend.jar

EXPOSE 3003

ENTRYPOINT ["java", "-jar", "friend.jar"]