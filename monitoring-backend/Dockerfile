# Use Maven with OpenJDK 17 to build the application
FROM maven:3.8.5-openjdk-17 AS build

# Set the working directory
WORKDIR /app

# Copy the pom.xml to download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the source code
COPY src ./src

# Build the application, skipping tests
RUN mvn clean package -DskipTests

# Use OpenJDK 17 on Alpine as the base image for the runtime
FROM openjdk:17-jdk-alpine

# Set the working directory
WORKDIR /app

# Copy the built JAR file from the builder stage
COPY --from=build /app/target/*.jar app.jar

# Expose the application's port
EXPOSE 8082

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
