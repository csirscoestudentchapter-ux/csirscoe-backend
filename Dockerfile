# -------------------
# Build stage
# -------------------
    FROM maven:3.9.4-eclipse-temurin-17 AS build
    WORKDIR /workspace
    
    # Copy only Maven wrapper and project descriptor first (for caching)
    COPY pom.xml mvnw ./
    COPY .mvn .mvn
    
    # Copy source folder
    COPY src ./src
    
    # Build the project
    RUN mvn clean package -DskipTests
    
    # -------------------
    # Runtime stage
    # -------------------
    FROM eclipse-temurin:17-jdk
    WORKDIR /app
    
    # Copy the built jar from the build stage
    COPY --from=build /workspace/target/*.jar demo.jar
    
    EXPOSE 8080
    ENTRYPOINT ["java", "-jar", "demo.jar"]
    