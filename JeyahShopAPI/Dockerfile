## Use official OpenJDK image as base
#FROM openjdk:22-jdk-slim
#
## Add a volume to persist logs (optional)
#VOLUME /tmp
#
## Copy the jar built by Spring Boot into the container
##COPY target/JeyahShopAPI-0.0.1-SNAPSHOT.jar app.jar
#COPY JeyahShopAPI/target/JeyahShopAPI-0.0.1-SNAPSHOT.jar app.jar
#
## Expose port 8080
#EXPOSE 8080
#
## Run the jar
#ENTRYPOINT ["java","-jar","/app.jar"]



# ---------- Stage 1: Build the JAR ----------
FROM maven:3.9.9-eclipse-temurin-22 AS build
WORKDIR /app

# Copy Maven config and source
COPY JeyahShopAPI/pom.xml .
COPY JeyahShopAPI/src ./src

# Build the JAR inside Docker
RUN mvn clean package -DskipTests

# ---------- Stage 2: Run the JAR ----------
FROM eclipse-temurin:22-jdk-jammy
WORKDIR /app

# Copy the built JAR from the previous stage
COPY --from=build /app/target/JeyahShopAPI-0.0.1-SNAPSHOT.jar app.jar

# Expose Spring Boot port
EXPOSE 8080

# Start the Spring Boot app
ENTRYPOINT ["java", "-jar", "app.jar"]
