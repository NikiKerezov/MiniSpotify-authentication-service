FROM openjdk:17-jdk-slim
WORKDIR /app
COPY /target/authentication-service-0.0.1-SNAPSHOT.jar ./app.jar
EXPOSE 8085
CMD ["java", "-jar", "app.jar"]