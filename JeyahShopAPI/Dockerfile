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




# Stage 1: Build the JAR
FROM maven:3.9.3-eclipse-temurin-22 AS build
WORKDIR /app
COPY JeyahShopAPI/pom.xml .
COPY JeyahShopAPI/src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run the JAR
FROM openjdk:22-jdk-slim
WORKDIR /app
COPY --from=build /app/target/JeyahShopAPI-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
