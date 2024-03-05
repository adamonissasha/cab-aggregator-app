FROM openjdk:19

WORKDIR /app

COPY build/libs/rides-service-0.0.1-SNAPSHOT.jar rides-service.jar

EXPOSE 8082

CMD ["java", "-jar", "rides-service.jar"]