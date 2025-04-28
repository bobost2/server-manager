FROM eclipse-temurin:21-alpine
LABEL authors="bobost"

WORKDIR /app

RUN mkdir -p /app/java/21 && cp -r /opt/java/openjdk/* /app/java/21/

#COPY ./java ./java - We used to have the java executable in the folder
COPY ./build/libs/*.jar application.jar

COPY ./server ./server

ENTRYPOINT ["java","-jar","application.jar"]
CMD []