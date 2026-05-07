# API Gateway

Spring Boot 4 + Spring Cloud Gateway MVC API Gateway for routing requests to downstream services with:

- Keycloak JWT resource server security
- Custom JWT role mapping for Keycloak
- Centralized JSON error responses
- CORS configuration
- Request logging
- Swagger/OpenAPI aggregation
- Resilience4j circuit breaker fallback for document service

## Tech Stack

- Java 17
- Spring Boot 4.0.6
- Spring Cloud Gateway Server MVC
- Spring Security OAuth2 Resource Server
- Spring Cloud CircuitBreaker + Resilience4j
- springdoc-openapi
- Eureka Client

## Project Structure

- `infrastructure/config` - security, CORS, and gateway configuration
- `infrastructure/security` - Keycloak JWT authority mapping
- `infrastructure/exception` - API error model and global exception handling
- `infrastructure/logging` - request logging and route resolution
- `infrastructure/fallback` - gateway fallback controller for circuit breaker responses

## Features

### Security

- Validates JWTs from Keycloak
- Maps Keycloak `realm_access.roles` and `resource_access.*.roles` to Spring authorities
- Returns JSON `401` and `403` responses instead of HTML pages

### CORS

- Configurable CORS rules from `application.yaml`
- Supports browser preflight `OPTIONS` requests

### Logging

- Logs every request that passes through the gateway
- Captures:
  - request source IP
  - downstream service
  - target URI
  - response status
  - request duration

### OpenAPI / Swagger

- Swagger UI is available at the gateway
- Aggregates docs from:
  - Identity Service
  - Document Service
  - Workflow Service
  - Integration Service

### Resilience

- Circuit breaker enabled for the document route
- Fallback returns a temporary JSON response when the service is unavailable

## API Routes

Current gateway routes are configured in `src/main/resources/application.yaml`:

- `/identity/**` -> `lb://IDENTITY-SERVICE`
- `/document/**` -> `lb://DOCUMENT-SERVICE`
- `/workflow/**` -> `lb://WORKFLOW-SERVICE`
- `/integration/**` -> `lb://INTEGRATION-SERVICE`

The gateway strips the first path segment before forwarding the request.

## Swagger UI

Open the consolidated API docs at:

- `http://localhost:8080/swagger-ui.html`

Available API docs entries:

- `/identity/v3/api-docs`
- `/document/v3/api-docs`
- `/workflow/v3/api-docs`
- `/integration/v3/api-docs`

## Fallback API

If the document service fails or times out, the gateway forwards to:

- `GET /fallback/document-service`

Fallback response message:

- `Service is currently unavailable, please try again later.`

## Configuration

Main configuration lives in `src/main/resources/application.yaml`.

### Keycloak

Update these values for your environment:

- `spring.security.oauth2.resourceserver.jwt.issuer-uri`
- `spring.security.oauth2.resourceserver.jwt.jwk-set-uri`

### Eureka

Update the Eureka server URL if needed:

- `eureka.client.service-url.defaultZone`

### CORS

Frontend origins are configured under:

- `app.cors`

### Request Logging

Route-to-service mapping is configured under:

- `app.request-logging.routes`

### Circuit Breaker

Document service circuit breaker settings are configured under:

- `resilience4j.circuitbreaker.instances.documentServiceCircuitBreaker`
- `resilience4j.timelimiter.instances.documentServiceCircuitBreaker`

## Run

```bash
mvn spring-boot:run
```

Or using the Maven wrapper:

```bash
./mvnw spring-boot:run
```

## Notes

- This gateway uses Spring Cloud Gateway MVC, not WebFlux.
- The gateway does not host the downstream business APIs; it proxies requests to child services.
- Each child service must expose its own OpenAPI spec at `/v3/api-docs` for Swagger aggregation to work.
