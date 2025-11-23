# -----------------------------------------------------------
# Stage 1: Build the JAR file
# Use a JDK base image for building
FROM openjdk:17-jdk-slim AS builder

# Set the current directory inside the container
WORKDIR /app

# Copy the Maven project files (pom.xml)
COPY pom.xml .

# Copy source code and build the JAR
COPY src ./src

# Build the Spring Boot application (using the Maven wrapper)
RUN ./mvnw clean package -DskipTests

# -----------------------------------------------------------
# Stage 2: Create the final image (smaller runtime image)
# Use a lightweight JRE base image
FROM openjdk:17-jre-slim

# Set the working directory
WORKDIR /app

# Copy the built JAR file from the builder stage
COPY --from=builder /app/target/*.jar app.jar

# Define the entrypoint to run the JAR
ENTRYPOINT ["java", "-jar", "app.jar"]