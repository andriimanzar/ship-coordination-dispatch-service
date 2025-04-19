FROM maven:3.9.9-amazoncorretto-21-alpine AS build

WORKDIR /app

COPY pom.xml ./

RUN mvn dependency:go-offline

COPY . ./

RUN mvn clean package -DskipTests

FROM amazoncorretto:21.0.7-alpine AS runtime

WORKDIR /app

COPY --from=build /app/target/ship-coordination-dispatch-service.jar /app/app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]

EXPOSE 8080