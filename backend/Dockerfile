# ---- Build stage: compile the Spring Boot jar with the Maven wrapper ----
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

# Copy only the Maven files first: dependencies are downloaded once and cached
# as long as pom.xml does not change.
COPY mvnw pom.xml ./
COPY .mvn .mvn
RUN chmod +x mvnw && ./mvnw -B dependency:go-offline

# Then the sources. Tests are skipped here because the CI already runs them.
COPY src src
RUN ./mvnw -B package -DskipTests && cp target/*.jar app.jar

# ---- Runtime stage: only a JRE and the jar ----
FROM eclipse-temurin:17-jre
WORKDIR /app

# Run the application as a non-root user
RUN useradd --system --create-home app
COPY --from=build /app/app.jar app.jar
USER app

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
