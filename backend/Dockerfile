# ---- Stage 1: Build ----
FROM eclipse-temurin:24-jdk AS builder

WORKDIR /app

# Copy project files
COPY . .

# Make Maven wrapper executable
RUN chmod +x mvnw

# Build the application (skip tests to save time)
RUN ./mvnw clean package -DskipTests


# ---- Stage 2: Runtime ----
FROM eclipse-temurin:24-jre

WORKDIR /app

# Copy only the final JAR from the builder stage
COPY --from=builder /app/target/splitwise-0.0.1-SNAPSHOT.jar app.jar

# Run the application
CMD ["java", "-jar", "app.jar"]
