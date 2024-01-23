FROM openjdk:19

WORKDIR /app

COPY build/libs/gateway-service-0.0.1-SNAPSHOT.jar gateway-service.jar

EXPOSE 8765

CMD ["java", "-jar", "gateway-service.jar"]