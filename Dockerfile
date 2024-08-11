# Use a base image with OpenJDK
FROM openjdk:21-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the Maven build output to the container
COPY target/task_management_system-0.0.1.jar app.jar

# Expose the port the app runs on
EXPOSE 8080

# Define the command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
