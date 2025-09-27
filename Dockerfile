# --------------------
# 1. BUILD STAGE (Builds the JAR file)
# --------------------
    FROM maven:3.9.4-eclipse-temurin-17 AS build

    # Set the working directory inside the container
    WORKDIR /app
    
    # The build context error means Docker is failing to see files in the root folder. 
    # We explicitly copy the required root files first.
    COPY pom.xml .
    COPY mvnw .
    COPY .mvn .mvn
    
    # Run the dependency download step using the cached pom.xml
    RUN mvn dependency:go-offline -B
    
    # Copy the source code (the 'src' folder) 
    COPY src src
    
    # Run the package command to compile the code and build the final JAR
    # The resulting JAR will be at /app/target/your-app-name.jar
    RUN mvn clean package -DskipTests
    
    # --------------------
    # 2. RUNTIME STAGE (Runs the application using a smaller base image)
    # --------------------
    FROM eclipse-temurin:17-alpine
    
    # Set the working directory for the runtime stage
    WORKDIR /app
    
    # Copy the generated JAR from the build stage.
    # We will keep the wildcard for flexibility, but be aware of the exact name (csi_backend-0.0.1-SNAPSHOT.jar).
    COPY --from=build /app/target/*.jar demo.jar
    
    # Expose the application port
    EXPOSE 8080
    
    # Run the application
    ENTRYPOINT ["java", "-jar" ,"demo.jar"]
    