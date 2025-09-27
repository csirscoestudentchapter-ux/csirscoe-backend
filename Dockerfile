# --------------------
# 1. BUILD STAGE (Builds the JAR file)
# --------------------
# Use a specific, stable Maven image with Java 21
FROM maven:3.9.4-eclipse-temurin-21 AS build

# Set the working directory inside the container for all subsequent steps
WORKDIR /app

# Define the source directory path relative to the Dockerfile's location (the root C:\)
# This path points to where your pom.xml, src/, etc., actually live.
ENV PROJECT_SRC=csi-backend-master/csi_backend/csi_backend

# 1. Copy only the pom.xml first to download dependencies and leverage Docker caching.
# We copy the pom.xml from its deep source location (PROJECT_SRC/pom.xml) 
# to the current working directory in the container (.).
COPY ${PROJECT_SRC}/pom.xml .
RUN mvn dependency:go-offline -B

# 2. Copy the rest of the project files (src/, .mvn, mvnw, etc.)
# We copy the contents of the PROJECT_SRC directory to the current working directory in the container (./)
COPY ${PROJECT_SRC}/. .

# 3. Compile the code and build the final fat JAR
RUN mvn clean package -DskipTests

# --------------------
# 2. RUNTIME STAGE (Runs the application using a smaller base image)
# --------------------
# Use a smaller, secure base image for the final deployment with Java 21 JRE
FROM eclipse-temurin:21-alpine

# Set the working directory for the runtime stage
WORKDIR /app

# Copy the generated JAR from the build stage.
# We use a wildcard to capture the full JAR name (e.g., csi_backend-0.0.1-SNAPSHOT.jar) 
# and rename it to a simple, consistent name: demo.jar
COPY --from=build /app/target/*.jar demo.jar

# Expose the application port
EXPOSE 8080

# The startup command to run the application
ENTRYPOINT ["java", "-jar" ,"demo.jar"]
