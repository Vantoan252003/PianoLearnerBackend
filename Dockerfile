FROM eclipse-temurin:21-jdk
WORKDIR /app/PianoLearn
COPY target/PianoLearn-0.0.1-SNAPSHOT.jar .
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "PianoLearn-0.0.1-SNAPSHOT.jar"]
