FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY user-service/pom.xml user-service/
COPY account-service/pom.xml account-service/
COPY payment-service/pom.xml payment-service/
COPY notification-service/pom.xml notification-service/
RUN mvn dependency:go-offline -pl payment-service -am -q
COPY payment-service/src payment-service/src
RUN mvn package -pl payment-service -am -DskipTests -q

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/payment-service/target/*.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "app.jar"]
