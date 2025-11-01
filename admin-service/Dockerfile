FROM openjdk:21-slim

WORKDIR /app

# Copy the JAR file
COPY target/admin-service-1.0-SNAPSHOT.jar admin-service.jar

# Expose admin server port
EXPOSE 9000

# Run the application
ENTRYPOINT ["java", "-jar", "admin-service.jar"]
