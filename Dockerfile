# --------------------
# 1. BUILD STAGE (Builds the JAR file)
# --------------------
    FROM maven:3.9.4-eclipse-temurin-17 AS build

    # Set the working directory inside the container
    WORKDIR /app
    
    # The .dockerignore file ensures that target/ and other unnecessary files are not copied.
    # Copy necessary Maven wrapper files
    COPY mvnw .
    COPY .mvn .mvn
    
    # Copy only the pom.xml first to download dependencies and leverage Docker caching.
    # If pom.xml doesn't change, this step is cached.
    COPY pom.xml .
    RUN mvn dependency:go-offline -B
    
    # Copy all the remaining source code (src directory, README, etc.)
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
    
    # Copy the generated JAR from the build stage. We use /app/target/ since we set WORKDIR /app 
    # in the build stage.
    # NOTE: The JAR name is csi_backend-0.0.1-SNAPSHOT.jar from your target folder structure.
    # We will keep the wildcard for flexibility, but be aware of the exact name.
    COPY --from=build /app/target/*.jar demo.jar
    
    # Expose the application port
    EXPOSE 8080
    
    # Run the application
    ENTRYPOINT ["java", "-jar" ,"demo.jar"]
    