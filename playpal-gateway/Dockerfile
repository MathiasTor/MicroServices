FROM openjdk:21

WORKDIR /app

COPY target/playpal-gateway-0.0.1-SNAPSHOT.jar gateway.jar

EXPOSE 3004

ENTRYPOINT ["java", "-jar", "gateway.jar"]