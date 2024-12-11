# Imagen base de Maven para compilar y ejecutar pruebas
FROM maven:3.8.8-eclipse-temurin-17 AS builder

# Crear el directorio de trabajo
WORKDIR /app

# Copiar los archivos de configuración y código fuente
COPY pom.xml .
COPY src ./src

# Descargar dependencias y compilar el proyecto
RUN mvn clean package -DskipTests

# Segunda etapa: Imagen más ligera para ejecutar pruebas
FROM openjdk:17-jdk-slim

# Crear el directorio de trabajo
WORKDIR /app

# Copiar los artefactos generados en la etapa de compilación
COPY --from=builder /app/target/*.jar ./app.jar

# Comando por defecto para ejecutar las pruebas
CMD ["java", "-jar", "app.jar"]
