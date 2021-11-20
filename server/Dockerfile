FROM eclipse-temurin:11-jdk-alpine
COPY app.jar /app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
