# Use official OpenJDK image as base
FROM openjdk:22-jdk-slim

# Add a volume to persist logs (optional)
VOLUME /tmp

# Copy the jar built by Spring Boot into the container
#COPY target/JeyahShopAPI-0.0.1-SNAPSHOT.jar app.jar
COPY JeyahShopAPI/target/JeyahShopAPI-0.0.1-SNAPSHOT.jar app.jar

# Expose port 8080
EXPOSE 8080

# Run the jar
ENTRYPOINT ["java","-jar","/app.jar"]