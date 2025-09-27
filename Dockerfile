# -------------------
# Build stage
# -------------------
    FROM maven:3.9.4-eclipse-temurin-17 AS build
    WORKDIR /workspace
    
    # Copy only necessary files first (optimizes caching)
    COPY pom.xml mvnw ./
    COPY .mvn .mvn
    
    # Copy source folder (adjust if your src is nested)
    COPY src ./src
    
    # Build the project
    RUN mvn clean package -DskipTests
    
    # -------------------
    # Runtime stage
    # -------------------
    FROM eclipse-temurin:17-jdk
    WORKDIR /app
    COPY --from=build /workspace/target/*.jar demo.jar
    EXPOSE 8080
    ENTRYPOINT ["java", "-jar", "demo.jar"]
    