FROM openjdk:19

WORKDIR /app

COPY build/libs/ride-service-0.0.1-SNAPSHOT.jar ride-service.jar

EXPOSE 8082

CMD ["java", "-jar", "ride-service.jar"]