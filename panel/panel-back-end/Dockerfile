FROM eclipse-temurin:21-alpine
LABEL authors="bobost"

WORKDIR /app
COPY ./build/libs/*.jar application.jar

ENTRYPOINT ["java","-jar","application.jar"]