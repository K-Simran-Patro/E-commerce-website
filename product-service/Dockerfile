#Use Java 21 as the base image - provides the Java environment needed to run Spring Boot application
FROM eclipse-temurin:21-jdk
# Set /app as the working directory inside the container - all commands will run here
WORKDIR /app

# Copy all project files from your computer into the /app folder in the container
COPY . .
# Build the Spring Boot project using Maven - creates the JAR file in target/ folder
# -DskipTests means skip running tests to make the build faster
RUN ./mvnw clean package -DskipTests
# Tell Docker/Render that this app runs on port 8082 - important for routing traffic to the container
EXPOSE 8082
# Start the Spring Boot application by running the built JAR file - this is the command that runs when the container starts
ENTRYPOINT ["java", "-jar", "target/product_service-0.0.1-SNAPSHOT.jar"]