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
- Config Server
- API Gateway
- Identity Service
- Workflow Service
- Integration Service
- Document Service

## Prerequisites

- Docker
- Docker Compose v2, or the legacy `docker-compose` CLI

## Environment

Docker Compose automatically reads a root `.env` file for variable substitution.

Use the checked-in template to create your local environment file:

```powershell
Copy-Item .env.example .env
```

Then edit `.env` for any secrets or host-specific overrides before starting the stack.

## Start the stack

Use the helper script:

```bash
./scripts/start.sh
```

Or run Compose directly:

```bash
docker compose up -d --build
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
| Workflow Service | http://localhost:8085 | Workflow runtime and APIs |
| Document Service | http://localhost:8086 | Document storage API |
| Integration Service | http://localhost:8089 | Integration APIs |
| Discovery Server | http://localhost:8761 | Eureka registry |
| Config Server | http://localhost:8888 | Spring Cloud Config server |
| Keycloak | http://localhost:8180 | Admin console on the standard Keycloak port inside the container |
| PostgreSQL | localhost:5432 | `postgres` / `postgres` |
| pgAdmin | http://localhost:5050 | `admin@example.com` / `admin` |
| Elasticsearch | http://localhost:9200 | Used by Zeebe and Kibana |
| Kibana | http://localhost:5601 | Elasticsearch UI |
| Zeebe Gateway | localhost:26500 | gRPC endpoint for workflow clients |
| Zeebe Monitoring | http://localhost:9600 | Health and monitoring endpoint |
| Zeebe Management API | http://localhost:8090 | Container port 8080 mapped to host 8090 |
| Operate | http://localhost:8081 | Process monitoring UI |
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

### Keycloak

```text
username: admin
password: admin
```

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
- `docker-compose.yml` maps both `operate` and `identity-service` to host port `8081`. If you run the full stack exactly as defined, that port conflict will need to be resolved before both services can bind successfully.
- `api-gateway`, `identity-service`, `workflow-service`, `integration-service`, `document-service`, `discovery-server`, and `config-server` are built from the Dockerfiles in `backend/`.
- The startup script uses `docker compose up -d --build` when Docker Compose v2 is available, and falls back to `docker-compose` if needed.
- Persistent data is stored in Docker volumes for PostgreSQL, pgAdmin, Elasticsearch, Zeebe, MinIO, and RabbitMQ.

## Helpful Commands

```bash
docker compose ps
docker compose logs -f
docker compose restart api-gateway
```
