# Use an official OpenJDK runtime as a parent image
FROM eclipse-temurin:17-jdk

# Set the working directory
WORKDIR /app

# Copy the built JAR file from the target directory
COPY target/spring-boot-core-bank-*.jar app.jar

# Expose port 8080 for the application
EXPOSE 8080

# Define environment variables for configuration injection
ENV SPRING_CONFIG_LOCATION=classpath:/application.yml
ENV JAVA_OPTS=""

# Command to run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
