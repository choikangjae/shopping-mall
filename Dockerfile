FROM openjdk:11
LABEL MAINTAINER="choikj33@gmail.com"

EXPOSE 8080

VOLUME /tmp
ARG JAR_FILE=build/libs/app-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]