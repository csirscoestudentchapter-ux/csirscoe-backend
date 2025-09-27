# --------------------
# 1. BUILD STAGE (Builds the JAR file)
# --------------------
    FROM maven:3.9.4-eclipse-temurin-17 AS build

    # Set the working directory inside the container
    WORKDIR /app
    
    # 1. Copy only the pom.xml first to download dependencies and leverage Docker caching.
    # This step is highly cacheable.
    COPY pom.xml .
    RUN mvn dependency:go-offline -B
    
    # 2. Copy the rest of the project files (src/, mvnw, .mvn, etc.).
    # The presence of a .dockerignore file should prevent the 'target/' folder from being copied.
    COPY . .
    
    # 3. Run the package command to compile the code and build the final JAR
    RUN mvn clean package -DskipTests
    
    # --------------------
    # 2. RUNTIME STAGE (Runs the application using a smaller base image)
    # --------------------
    FROM eclipse-temurin:17-alpine
    
    # Set the working directory for the runtime stage
    WORKDIR /app
    
    # Copy the generated JAR from the build stage.
    COPY --from=build /app/target/*.jar demo.jar
    
    # Expose the application port
    EXPOSE 8080
    
    # Run the application
    ENTRYPOINT ["java", "-jar" ,"demo.jar"]
    