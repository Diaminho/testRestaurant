FROM adoptopenjdk/openjdk11:alpine-jre

WORKDIR /opt/app
COPY build/libs/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]