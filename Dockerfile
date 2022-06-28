FROM openjdk:11
LABEL MAINTAINER="choikj33@gmail.com"

#CMD ["./gradlew", "clean", "package"]
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]