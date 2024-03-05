FROM openjdk:19

WORKDIR /app

COPY build/libs/registry-service-0.0.1-SNAPSHOT.jar registry-service.jar

EXPOSE 8761

CMD ["java", "-jar", "registry-service.jar"]