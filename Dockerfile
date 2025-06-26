FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app
COPY /refrigerator/target/refrigerator*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]