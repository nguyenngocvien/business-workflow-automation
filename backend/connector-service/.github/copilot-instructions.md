# E-Connector AI Coding Guidelines

## Project Overview
E-Connector is a Spring Boot service integration platform that enables dynamic execution of REST APIs, database queries, email services, and orchestrated pipelines. It provides a unified interface for managing connections, services, and scheduled jobs with comprehensive logging and monitoring.

## Architecture Patterns

### Layered Architecture
- **API Layer**: Controllers extend `AbstractCrudController<REQUEST, RESPONSE, ID>` for standard CRUD operations
- **Application Layer**: Services extend `AbstractCrudApplicationService<REQUEST, RESPONSE, ENTITY, ID>` for business logic
- **Domain Layer**: JPA entities, repositories, and enums

### Service Execution Flow
Services are executed dynamically via `ServiceExecutionApplicationService.execute()`:
1. Resolve service by `appId`, `serviceCode`, `serviceVersion`
2. Apply `ServiceExecutionMapper` for request/response transformation (based on `ServiceType` and `serviceCode`)
3. Execute via `TypedServiceExecutor` (e.g., `DbServiceExecutor` for DB queries)
4. Log execution details in `ec_log` table

### Key Components
- **Connections**: Configured via JSONB in `ec_connection` (types: DB, REST, SOAP, SFTP, SMTP, KAFKA)
- **Services**: Defined in `ec_service` with JSONB configs, linked to connections
- **Pipelines**: Chain services via `ec_pipeline` and `ec_pipeline_step` with transformation scripts
- **Jobs**: Scheduled via Quartz in `ec_schedule_job` (SERVICE or PIPELINE type)
- **Logging**: Partitioned `ec_log` table with full request/response JSONB data

## Development Workflow

### Building & Running
```bash
# Local development with Docker
docker-compose up -d  # Starts Postgres
./mvnw spring-boot:run  # Runs app on port 8080

# Production build
./mvnw clean package
docker build -t e-connector .
```

### Database Migrations
- Use Flyway scripts in `src/main/resources/db/migration/`
- Schema uses JSONB for flexible configs (e.g., connection credentials, service parameters)
- Tables prefixed with `ec_` (e-connector)

### Testing
- Extend `AbstractCrudApplicationService` for consistent service testing
- Mock repositories and external connections
- Validate JSONB structures in integration tests

## Code Patterns & Conventions

### CRUD Implementation
```java
// Controller
@RestController
@RequestMapping("/api/services")
public class EcServiceController extends AbstractCrudController<EcServiceRequest, EcServiceResponse, Long> {
    public EcServiceController(EcServiceApplicationService service) { super(service); }
}

// Service
@Service
public class EcServiceApplicationService extends AbstractCrudApplicationService<EcServiceRequest, EcServiceResponse, EcService, Long> {
    @Override
    protected void updateEntity(EcService entity, EcServiceRequest request, boolean creating) {
        // Map request to entity
    }
}
```

### Service Execution
- Implement `TypedServiceExecutor` for new service types (e.g., REST, EMAIL)
- Use `ServiceExecutionMapper` for service-specific transformations
- Handle JSON payloads with Jackson `ObjectMapper`

### Configuration Management
- Store connection/service configs as JSONB
- Use `ServiceConfigSupportService` for type-safe deserialization
- Example DB config: `{"url": "jdbc:...", "username": "...", "password": "..."}`

### Error Handling
- Use `ResourceNotFoundException` for missing entities
- Log detailed errors in `ec_log` with stacktraces
- Return structured error responses with status codes

## Key Files to Reference
- `AbstractCrudController.java` - Base controller pattern
- `AbstractCrudApplicationService.java` - Base service pattern  
- `ServiceExecutionApplicationService.java` - Dynamic execution logic
- `DbServiceExecutor.java` - DB execution example
- `V1__init_schema.sql` - Database schema
- `docker-compose.yml` - Development environment</content>
<parameter name="filePath">d:/projects/e-connector/.github/copilot-instructions.md