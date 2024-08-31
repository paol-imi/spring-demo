# Distributed Library Management System

## Project Overview

This Spring Boot application manages a distributed library system, providing RESTful APIs for book and location management. It allows seamless tracking of inventory across multiple library branches.

## Project Structure

The project follows a standard Spring Boot application structure:

- `controller`: REST API endpoints
- `service`: Business logic implementation
- `repository`: Data access layer
- `entity`: JPA entities
- `dto`: Data Transfer Objects
- `specification`: JPA Specifications for complex queries
- `lib`: Utility classes (like custom exceptions)
- `config`: Application configuration
- `aspect`: Aspect-oriented programming classes
- `validator`: Custom validators for DTOs

## Technical Details

### Environment Profiles

Two primary environment profiles are implemented:

- `dev`: Uses PostgreSQL for development
- `test`: Uses H2 in-memory database for testing

Configuration is managed through `application.properties`, `application-dev.properties`, and `application-test.properties`.

### Database Management

- Flyway is used for database migrations
- SQL schema is defined under `src/main/resources/db/migration`
- Enables version-controlled, incremental database schema changes

### API Documentation

- OpenAPI 3.0 and Swagger UI are configured
- Provides interactive API exploration and testing
- Accessible at `/swagger-ui.html` when the application is running

### Data Transfer and Validation

- DTOs (Data Transfer Objects) are used to separate API contracts from internal data models
- Bean Validation annotations are used on DTOs for input validation

### Exception Handling and Utilities

The `lib` package contains utility classes and custom exceptions:

1. `SafeRuntimeException`:
    - Custom RuntimeException for exceptions that can be safely exposed to clients
    - Exception message is directly reported to the client

2. `RestControllerExceptionHandler`:
    - Aspect for centralized exception handling
    - Catches `SafeRuntimeException` and reports its message clearly
    - Other exceptions are caught and wrapped in a generic error response

3. `RepositoryException`:
    - Base exception class (not RuntimeException) for service layer
    - Includes subclasses for specific error scenarios (e.g., `NotFound`, `Conflict`, `BadRequest`)

4. Exception handling in Services and Controllers:
    - Services define fine-grained exceptions
    - Controllers must explicitly handle these exceptions
    - Promotes deterministic API behavior and accurate Swagger documentation

## Testing Strategy

- Comprehensive unit tests for each component
- JUnit 5 as the test runner
- Mockito for creating mocks
- Adheres to component isolation principle, leveraging Inversion of Control (IoC)
- Integration tests using `@DataJpaTest` for repository layer
- BDD-style test naming for clarity

## Key Design Decisions

1. DTO Pattern: Separates API contracts from internal data models
2. Specification Pattern: Enables flexible and type-safe querying
3. Service Layer: Encapsulates business logic and transaction management
4. Repository Pattern: Abstracts data access and enables easier testing
5. Aspect-Oriented Programming (AOP): Used for cross-cutting concerns like exception handling

## Getting Started

1. Clone the repository
2. Set up PostgreSQL for the `dev` profile
3. Run `./gradlew bootRun` to start the application
4. Access Swagger UI at `http://localhost:8080/swagger-ui.html`
