# Use a base image with OpenJDK
FROM openjdk:20-jdk

VOLUME /tmp

ARG JAR_FILE=target/chatserver-1.1.3.jar

COPY ${JAR_FILE} chatserver.jar

# Expose the port on which your application will run (e.g., 8080)
EXPOSE 8080

ENTRYPOINT ["java","-jar","/chatserver.jar"]
