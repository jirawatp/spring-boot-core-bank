# Spring Boot Core Bank

![Build and Test](https://github.com/jirawatp/spring-boot-core-bank/actions/workflows/build.yml/badge.svg)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=jirawatp_spring-boot-core-bank&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=jirawatp_spring-boot-core-bank)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=jirawatp_spring-boot-core-bank&metric=coverage)](https://sonarcloud.io/summary/new_code?id=jirawatp_spring-boot-core-bank)


## Overview

This project is a Spring Boot-based banking application providing core banking functionalities such as account management, transactions, and security integration. It includes database interactions via PostgreSQL, caching via Redis, API gateway via Kong, and supports authentication via JWT, API Key, and Keycloak.

## Project Information

- **Java version**: 17
- **Spring Boot version**: 3.2.3
- **Packaging**: JAR
- **License**: No License

## Tech Stack

- Java 17
- Spring Boot
- PostgreSQL
- Redis (caching)
- Flyway (DB migrations)
- JWT and API Key for authentication
- Keycloak (Identity Provider)
- Kong (API Gateway)
- Swagger (OpenAPI)
- SonarQube (Code Quality)

## Project Structure
```
com.pattanayutanachot.jirawat.core.bank
├── controller/
├── model/
├── repository/
├── service/
├── config/
├── exception/
├── validation/
└── util/
```

## CI/CD

- **Unit tests and SonarQube analysis** run on every PR.
- **Docker Image** created and published to GitHub Container Registry upon merging to the `main` branch.

## Local Development

### Prerequisites
- Java 17
- Docker (PostgreSQL, Redis, Kong)

## Local Environment Variables (`.env`)

```sh
POSTGRES_DB=core_bank
POSTGRES_USER=postgres
POSTGRES_PASSWORD=password
REDIS_HOST=localhost
REDIS_PORT=6379
SPRING_PROFILES_ACTIVE=local
```

## Local Application Properties (`application-local.properties`)

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/core_bank
spring.datasource.username=${POSTGRES_USER}
spring.datasource.password=${POSTGRES_PASSWORD}
spring.datasource.driver-class-name=org.postgresql.Driver

spring.redis.host=${REDIS_HOST}
spring.redis.port=${REDIS_PORT}

spring.flyway.enabled=false

spring.application.name=spring-boot-core-bank
spring.application.version=1.0.0
```

## Docker (for local development)

Run PostgreSQL, Redis, and Kong locally:

```sh
docker-compose up -d
```

## Building & Running

### Using Maven:

```sh
mvn clean install
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

### IntelliJ IDEA Setup

- Open `Run -> Edit Configurations`
- Add `local` in the "Active profiles" field or VM options:

```sh
-Dspring.profiles.active=local
```

## Monitoring and Health Checks

Check application health at:

```sh
GET /api/health
```

Response:

```json
{
  "application": "spring-boot-core-bank",
  "version": "1.0.0",
  "database": "UP",
  "redis": "UP",
  "status": "UP"
}
```

## Swagger API Docs

Visit [Swagger UI](http://localhost:8080/swagger-ui.html)

### Deployment
On merging PRs to `main`, a Docker image is built and published to GitHub Container Registry (GHCR).

## License
No license.