FROM alpine:latest
LABEL authors="bobost"

WORKDIR /app

COPY ./java ./java
COPY ./build/libs/*.jar application.jar

COPY ./server ./server

ENTRYPOINT ["./java/21/bin/java","-jar","application.jar"]
CMD []