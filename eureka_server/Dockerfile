# Usa una imagen base de OpenJDK para Java 17 en Alpine Linux
FROM openjdk:21 as builder

# Establece el directorio de trabajo
WORKDIR /app

# Copia solo el archivo JAR construido desde la imagen de compilación
COPY /target/eureka_server-1.0-SNAPSHOT.jar app.jar

# Usa una nueva imagen base para el entorno de ejecución
FROM openjdk:21

# Establece el directorio de trabajo
WORKDIR /app

# Copia solo el archivo JAR construido desde la imagen de compilación
COPY --from=builder /app/app.jar .

# Expone el puerto en el que se ejecuta tu aplicación Spring Boot
EXPOSE 8761

# Comando para ejecutar la aplicación cuando el contenedor se inicie
CMD ["java", "-jar", "app.jar"]
