FROM maven:3.9.9-amazoncorretto-23 AS build
WORKDIR /app
COPY pom.xml .

RUN mvn dependency:go-offline -B

COPY src ./src

RUN mvn package -DskipTests -Dmaven.compiler.source=23 -Dmaven.compiler.target=23

FROM amazoncorretto:23-alpine-jdk
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]