# ---- Build ----
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
WORKDIR /app/admin-service
RUN mvn -q -DskipTests package

# ---- Runtime ----
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /app/admin-service/target/*.jar app.jar
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75 -XX:+UseG1GC"
EXPOSE 9000
HEALTHCHECK --interval=10s --timeout=5s --retries=12 CMD wget -qO- http://localhost:9000/actuator/health || exit 1
ENTRYPOINT ["java","-jar","/app/app.jar"]
