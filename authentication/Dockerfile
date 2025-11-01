# ---- Build ----
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Descarga dependencias sin copiar todo (cache eficiente)
COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline

# Código fuente del módulo eureka
COPY src ./src
RUN mvn -q -DskipTests package

# ---- Runtime ----
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Copia el jar construido
COPY --from=build /app/target/*.jar app.jar

# Ajustes JVM opcionales (memoria en contenedor)
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75 -XX:+UseG1GC"

EXPOSE 8585
# Healthcheck simple (si usas Actuator, cambia la URL)
HEALTHCHECK --interval=10s --timeout=5s --retries=12 CMD wget -qO- http://localhost:8585 || exit 1

ENTRYPOINT ["java","-jar","/app/app.jar"]
