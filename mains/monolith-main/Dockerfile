FROM openjdk:17-jdk-slim
VOLUME /tmp
ARG JAR_FILE=build/libs/monolith-main.jar
COPY ${JAR_FILE} jiniaslog.jar
ENTRYPOINT ["java","-jar","/jiniaslog.jar"]