FROM openjdk:19

WORKDIR /app

COPY build/libs/passenger-service-0.0.1-SNAPSHOT.jar passenger-service.jar

EXPOSE 8080

CMD ["java", "-jar", "passenger-service.jar"]