FROM openjdk:19

WORKDIR /app

COPY build/libs/bank-service-0.0.1-SNAPSHOT.jar bank-service.jar

EXPOSE 8083

CMD ["java", "-jar", "bank-service.jar"]