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



# Use official Maven + OpenJDK image for building
FROM maven:3.9.6-eclipse-temurin-22 AS build

# Set working directory inside container
WORKDIR /app

# Copy Maven config and source
COPY pom.xml .
COPY src ./src

# Build the JAR inside Docker
RUN mvn clean package -DskipTests

# Use a lightweight Java 22 image for running the app
FROM openjdk:22-jdk-slim

# Add a volume to persist logs (optional)
VOLUME /tmp

# Copy built JAR from the build stage
COPY --from=build /app/target/JeyahShopAPI-0.0.1-SNAPSHOT.jar app.jar

# Expose port 8080
EXPOSE 8080

# Run the JAR
ENTRYPOINT ["java", "-jar", "/app.jar"]
