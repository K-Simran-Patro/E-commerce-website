FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY . .
RUN ./mvnw clean package -DskipTests
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "target/*.jar"]