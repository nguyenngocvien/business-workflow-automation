# Identity Service

Spring Boot service for identity and access management.

It provides APIs for managing:

- users
- roles
- groups
- identity providers
- user-role assignments
- user-group memberships
- role-permission grants

The service uses PostgreSQL for persistence, Liquibase for schema management, Spring Security as an OAuth2 resource server, and Eureka for service discovery.

## Tech Stack

- Java 17
- Spring Boot 4
- Spring Data JPA
- Spring Security
- Spring Cloud Netflix Eureka Client
- Liquibase
- PostgreSQL
- Keycloak Admin Client
- springdoc-openapi

## Prerequisites

- JDK 17
- Maven 3.9+ or the included Maven Wrapper
- PostgreSQL
- Keycloak or another JWT issuer compatible with the configured issuer URI
- Eureka server if you want service registration enabled

## Configuration

Default runtime settings are defined in [`src/main/resources/application.yaml`](src/main/resources/application.yaml).

Important defaults:

- application port: `8081`
- database URL: `jdbc:postgresql://localhost:5432/identity_db`
- database user: `postgres`
- database password: `postgres`
- JWT issuer: `http://localhost:8080/realms/your-realm`
- Eureka registry: `http://localhost:8761/eureka/`
- Keycloak auth server: `http://localhost:8080`

Update these values before running against your own environment.

## Run Locally

Start the application with the Maven Wrapper:

```bash
./mvnw spring-boot:run
```

On Windows:

```powershell
mvnw.cmd spring-boot:run
```

Package and run the jar:

```bash
./mvnw clean package
java -jar target/identity-0.0.1-SNAPSHOT.jar
```

## API Documentation

When the application is running, OpenAPI docs are available through springdoc:

- Swagger UI: `http://localhost:8081/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8081/v3/api-docs`

## API Surface

### Users

- `POST /api/users`
- `PUT /api/users/{id}`
- `GET /api/users/{id}`
- `GET /api/users`
- `GET /api/users/username/{username}`
- `GET /api/users/external/{externalId}`
- `PUT /api/users/{id}/deactivate`

### Roles

- `POST /api/roles`
- `PUT /api/roles/{id}`
- `GET /api/roles/{id}`
- `GET /api/roles`
- `GET /api/roles/code/{code}`

### Groups

- `POST /api/groups`
- `PUT /api/groups/{id}`
- `GET /api/groups/{id}`
- `GET /api/groups`
- `GET /api/groups/code/{code}`

### Identity Providers

- `POST /api/identity-providers`
- `PUT /api/identity-providers/{id}`
- `GET /api/identity-providers/{id}`
- `GET /api/identity-providers`
- `GET /api/identity-providers/code/{providerCode}`

### Role Permissions

- `POST /api/role-permissions`
- `DELETE /api/role-permissions/{roleId}/{permissionId}`
- `GET /api/role-permissions/{roleId}/{permissionId}`
- `GET /api/role-permissions/role/{roleId}`

### User Groups

- `POST /api/user-groups`
- `DELETE /api/user-groups/{userId}/{groupId}`
- `GET /api/user-groups/{userId}/{groupId}`
- `GET /api/user-groups/user/{userId}`

### User Roles

- `POST /api/user-roles`
- `DELETE /api/user-roles/{userId}/{roleId}`
- `GET /api/user-roles/{userId}/{roleId}`
- `GET /api/user-roles/user/{userId}`

## Database Migrations

Liquibase changelogs live under [`src/main/resources/db/changelog`](src/main/resources/db/changelog).

The master changelog is:

- [`src/main/resources/db/changelog/db.changelog-master.xml`](src/main/resources/db/changelog/db.changelog-master.xml)

## Tests

Run the test suite with:

```bash
./mvnw test
```

On Windows:

```powershell
mvnw.cmd test
```
