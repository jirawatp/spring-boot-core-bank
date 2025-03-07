# Spring Boot Core Bank Application

This project provides a secure and robust backend for core banking operations. The application uses Java, Spring Boot, PostgreSQL, Redis, Keycloak for authentication, Kong Gateway for API management, and supports JWT and API key-based security.

## Built With
- Java 17
- Spring Boot (Packaged as a JAR)
- PostgreSQL
- Redis (Caching)
- Keycloak (Identity Management)
- Kong Gateway (API Management)
- JWT & API Key Authentication
- RSA Encryption for secure authentication
- Flyway for database migration
- Swagger for API documentation
- Docker Compose (for local development)
- Lombok
- Spring Security
- Distributed Tracing
- Spring Boot DevTools
- GitHub Actions CI/CD pipeline
- SonarQube

## Getting Started

### Prerequisites
- Java 17
- Docker
- Docker Compose
- Maven
- IntelliJ IDEA or VS Code (recommended for local development)

### Running Application (Local Development via IDE)
When running the application via an IDE like IntelliJ, the following **environment variables** need to be set. These can be configured in an `.env` file or in an `application-local.properties` file.

#### `.env` file (for IntelliJ or Docker Compose override)
```ini
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/corebank
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres
SPRING_REDIS_HOST=localhost
SPRING_REDIS_PORT=6379
SPRING_SECURITY_OAUTH2_CLIENT_PROVIDER_KEYCLOAK_ISSUER_URI=http://localhost:8081/auth/realms/corebank
JAVA_OPTS=-Xms512m -Xmx1024m
```

#### `application-local.properties` (for Spring Boot profile)
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/corebank
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.redis.host=localhost
spring.redis.port=6379
spring.security.oauth2.client.provider.keycloak.issuer-uri=http://localhost:8081/auth/realms/corebank
```

### Running Supporting Services (Docker Compose)
For local development, supporting services (PostgreSQL, Redis, Keycloak, Kong) should be started using Docker Compose:
```bash
docker compose up -d
```

### API Documentation
Swagger UI:
```
http://localhost:8080/swagger-ui/index.html
```

### Kong Gateway
- Proxy API: `http://localhost:8000`
- Admin API: `http://localhost:8001`

### Keycloak Admin
- URL: `http://localhost:8081`
- Username/Password: `admin/admin`

### Database Migration
Flyway migrations run automatically on startup. Migration scripts:
```
src/main/resources/db/migration/
```

### Input Validation
Input validation is implemented for API endpoints using Hibernate Validator.

### Testing
Run unit tests:
```bash
mvn clean test
```

### CI/CD
CI/CD configured via GitHub Actions includes automated testing and SonarQube scanning.

### SonarQube Configuration
Properties in `pom.xml`:
```xml
<properties>
  <sonar.projectKey>jirawatp_spring-boot-core-bank</sonar.projectKey>
  <sonar.host.url>https://sonarcloud.io</sonar.host.url>
</properties>
```

### Deployment
On merging PRs to `main`, a Docker image is built and published to GitHub Container Registry (GHCR).

## License
No license.