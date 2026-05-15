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

Environment variables supported by the application:

- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `JWT_ISSUER_URI`
- `SERVER_PORT`
- `EUREKA_DEFAULT_ZONE`
- `KEYCLOAK_AUTH_SERVER_URL`
- `KEYCLOAK_REALM`
- `KEYCLOAK_CLIENT_ID`
- `KEYCLOAK_CLIENT_SECRET`

See [`.env.example`](.env.example) for the default local values.

## Run Locally

Create a local `.env` file from the example and set any secrets or host-specific values:

```powershell
Copy-Item .env.example .env
```

Then load those variables in your shell or IDE before starting the app.

Using the Maven Wrapper:

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

## Run In Docker

Build the image:

```bash
docker build -t identity-service .
```

Run the container with the env file and map the app port:

```bash
docker run --rm --env-file .env -e SERVER_PORT=8080 -p 8080:8080 identity-service
```

The `SERVER_PORT=8080` override keeps the container aligned with the `EXPOSE 8080` setting in the Dockerfile.

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
