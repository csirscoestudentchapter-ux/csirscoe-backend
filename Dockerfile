# --------------------
# 1. BUILD STAGE (Builds the JAR file)
# --------------------
    FROM maven:3.9.4-eclipse-temurin-17 AS build

    # Set the working directory inside the container
    WORKDIR /app
    
    # Copy only the pom.xml first to download dependencies and leverage Docker caching.
    # If pom.xml doesn't change, this step is cached.
    COPY pom.xml .
    RUN mvn dependency:go-offline -B
    
    # Copy all the remaining source code
    COPY . .
    
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
    COPY --from=build /app/target/*.jar demo.jar
    
    # Expose the application port
    EXPOSE 8080
    
    # Run the application
    ENTRYPOINT ["java", "-jar" ,"demo.jar"]
    