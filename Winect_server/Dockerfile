FROM openjdk:11.0.16-jdk
ARG JAR_FILE_PATH=build/libs/Winect_server-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE_PATH} app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]