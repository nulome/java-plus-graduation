FROM amazoncorretto:21-alpine-jdk
RUN apk --update --no-cache add curl
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]