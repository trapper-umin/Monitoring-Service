FROM maven:3.8.4-openjdk-17 as build
WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package

FROM openjdk:17-jdk-slim
WORKDIR /app

COPY --from=build /app/target/Monitoring-Service-2.0.2.jar /app/app.jar

CMD ["java", "-jar", "app.jar"]