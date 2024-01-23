FROM openjdk:19

WORKDIR /app

COPY build/libs/driver-service-0.0.1-SNAPSHOT.jar driver-service.jar

EXPOSE 8081

CMD ["java", "-jar", "driver-service.jar"]