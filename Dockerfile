FROM eclipse-temurin:17-jdk-alpine
COPY ./target/*.jar order-service.jar 
ENTRYPOINT ["java","-jar","order-service.jar"]