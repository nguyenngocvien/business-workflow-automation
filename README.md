# Business Workflow Automation

Local development stack for the business workflow automation platform.

The root `docker-compose.yml` brings up the following services:

- PostgreSQL
- pgAdmin
- Elasticsearch
- Kibana
- Keycloak
- Zeebe
- Operate
- MinIO
- RabbitMQ
- Discovery Server
- API Gateway
- Identity Service
- Connector Service
- Workflow Service
- Document Service

## Prerequisites

- Docker
- Docker Compose v2, or the legacy `docker-compose` CLI

## Environment

Docker Compose automatically reads a root `.env` file for variable substitution.

Use the checked-in template to create your local development environment file:

```powershell
Copy-Item .env.example .env
```

For staging or prod, use the matching template and point the helper script at it:

```powershell
Copy-Item .env.staging.example .env.staging
Copy-Item .env.prod.example .env.prod
```

Then start with `COMPOSE_ENV_FILE` set to the profile you want, for example:

```powershell
$env:COMPOSE_ENV_FILE=".env.staging"
./scripts/start.sh
```

The Keycloak realm import file is selected per environment through `KEYCLOAK_IMPORT_FILE`, so dev, staging, and prod can each use a separate realm, client, role, and user set.

## Start the stack

Use the helper script:

```bash
./scripts/start.sh
```

Or run Compose directly:

```bash
docker compose -f docker-compose.yml up -d --build --remove-orphans
```

To start only selected services, pass them to the script:

```bash
./scripts/start.sh postgres keycloak api-gateway
```

## Stop the stack

```bash
./scripts/stop.sh
```

To remove volumes as well:

```bash
./scripts/stop.sh -v
```

## Services and ports

| Service | Host URL / Port | Notes |
| --- | --- | --- |
| API Gateway | http://localhost:8080 | Routes requests into the backend stack |
| Identity Service | http://localhost:8081 | Spring Boot service |
| Connector Service | http://localhost:8082 | Integration APIs |
| Workflow Service | http://localhost:8083 | Workflow runtime and APIs |
| Document Service | http://localhost:8084 | Document storage API |
| Discovery Server | http://localhost:8761 | Eureka registry |
| Keycloak | http://localhost:8180 | Admin console on the standard Keycloak port inside the container |
| PostgreSQL | localhost:5432 | `postgres` / `postgres` |
| pgAdmin | http://localhost:5050 | `admin@example.com` / `admin` |
| Elasticsearch | http://localhost:9200 | Used by Zeebe and Kibana |
| Kibana | http://localhost:5601 | Elasticsearch UI |
| Zeebe Gateway | localhost:26500 | gRPC endpoint for workflow clients |
| Zeebe Monitoring | http://localhost:9600 | Health and monitoring endpoint |
| Zeebe Management API | http://localhost:8090 | Container port 8080 mapped to host 8090 |
| Operate | http://localhost:8088 | Process monitoring UI |
| MinIO API | http://localhost:9000 | `minio` / `minio123` |
| MinIO Console | http://localhost:9001 | Same MinIO credentials |
| RabbitMQ | http://localhost:15672 | `guest` / `guest` |

## Default credentials

### PostgreSQL

```text
username: postgres
password: postgres
```

The Compose file initializes these databases:

- `bpm`
- `keycloak`
- `edocument`
- `identity_db`
- `connector`

### Keycloak

```text
username: admin
password: dev-admin
```

The committed environment templates import these realms:

- `baw-dev`
- `baw-staging`
- `baw-prod`

Each realm includes an `api-gateway` client, an `identity-service` client, and example `admin`, `manager`, and `user` accounts.

### pgAdmin

```text
username: admin@example.com
password: admin
```

### MinIO

```text
username: minio
password: minio123
```

### RabbitMQ

```text
username: guest
password: guest
```

## Notes

- This stack is intended for local development only.
- `identity-service` is exposed on host port `8081`, and `operate` is exposed on host port `8088`.
- `api-gateway`, `identity-service`, `workflow-service`, `connector-service`, `document-service`, and `discovery-server` are built from the Dockerfiles in `backend/`.
- The startup script uses `docker compose -f docker-compose.yml up -d --build --remove-orphans` when Docker Compose v2 is available, and falls back to `docker-compose` if needed.
- Persistent data is stored in Docker volumes for PostgreSQL, pgAdmin, Elasticsearch, Zeebe, MinIO, and RabbitMQ.

## Helpful Commands

```bash
docker compose ps
docker compose logs -f
docker compose restart api-gateway
```
