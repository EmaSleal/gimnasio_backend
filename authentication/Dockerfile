# ---- Build ----
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
WORKDIR /app/authentication
RUN mvn -q -DskipTests package

# ---- Runtime ----
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /app/authentication/target/*.jar app.jar
EXPOSE 8583
HEALTHCHECK --interval=10s --timeout=5s --retries=12 CMD wget -qO- http://localhost:8583/actuator/health || exit 1
ENTRYPOINT ["java","-jar","/app/app.jar"]
