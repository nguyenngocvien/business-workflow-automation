# Local Setup

## 1. Prerequisites

- Docker
- Docker Compose v2, or the legacy `docker-compose` CLI

## 2. Configure Environment

From the repository root, copy the sample env file:

```powershell
Copy-Item .env.example .env
```

Then edit `.env` if you need to override ports, credentials, or external endpoints.

## 3. Start the Stack

Start the full stack:

```bash
./scripts/start.sh
```

Start a subset of services:

```bash
./scripts/start.sh postgres keycloak api-gateway workflow-service
```

The script runs `docker compose -f docker-compose.yml up -d --build --remove-orphans` from the repository root and falls back to `docker-compose` when needed.

## 4. Stop the Stack

Stop the full stack:

```bash
./scripts/stop.sh
```

Stop and remove volumes:

```bash
./scripts/stop.sh -v
```

## 5. Access URLs

### Application Services

- API Gateway: http://localhost:8080
- Identity Service: http://localhost:8081
- Connector Service: http://localhost:8082
- Workflow Service: http://localhost:8083
- Document Service: http://localhost:8084
- Discovery Server: http://localhost:8761

### Shared Infrastructure

- Keycloak: http://localhost:8180
- pgAdmin: http://localhost:5050
- PostgreSQL: localhost:5432
- Elasticsearch: http://localhost:9200
- Kibana: http://localhost:5601
- Zeebe Gateway: localhost:26500
- Zeebe Monitoring: http://localhost:9600
- Zeebe Management API: http://localhost:8090
- Operate: http://localhost:8088
- MinIO API: http://localhost:9000
- MinIO Console: http://localhost:9001
- RabbitMQ Management UI: http://localhost:15672
- RabbitMQ AMQP: localhost:5672

## 6. Useful Checks

```bash
curl http://localhost:8080
curl http://localhost:8081/actuator/health
curl http://localhost:8082/actuator/health
curl http://localhost:8083/actuator/health
curl http://localhost:8084/actuator/health
curl http://localhost:8088
curl http://localhost:8761
curl http://localhost:8180
curl http://localhost:9000/minio/health/live
curl http://localhost:9200
```

## 7. Notes

- The local stack is development-only.
- PostgreSQL is initialized with multiple databases, including `bpm`, `keycloak`, `edocument`, `identity_db`, and `connector`.
- `identity-service` runs on host port `8081`.
- `operate` runs on host port `8088`.
- `workflow-service` uses PostgreSQL, RabbitMQ, Zeebe, and Keycloak.
- `document-service` uses PostgreSQL and MinIO.
- `connector-service` uses PostgreSQL and the shared mail/database integration stack.
- `docker-compose.yml` is the source of truth for service dependencies and ports.
