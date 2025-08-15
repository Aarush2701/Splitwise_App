FROM eclipse-temurin:24-jdk
WORKDIR /app
COPY . .
RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests
CMD ["java", "-jar", "target/splitwise-0.0.1-SNAPSHOT.jar"]
