# Local Setup

## 1. Prerequisites

- Docker
- Docker Compose v2+

## 2. Start the stack

From the repository root:

```bash
./scripts/start.sh
```

If you want to start a specific Compose service, pass the service name:

```bash
./scripts/start.sh workflow-service
```

## 3. Stop the stack

```bash
./scripts/stop.sh
```

To remove volumes as well:

```bash
./scripts/stop.sh -v
```

## 4. Access URLs

### Camunda Platform

- Orchestration: http://localhost:8088
- Connectors: http://localhost:8086
- Optimize: http://localhost:8083
- Identity: http://localhost:8084
- Console: http://localhost:8087
- Web Modeler: http://localhost:8070
- Zeebe Gateway: localhost:26500

### Backend Services

- Workflow Service: http://localhost:8085
- Integration Service: http://localhost:8089

### Identity and Utilities

- Keycloak: http://localhost:8080/auth
- RabbitMQ UI: http://localhost:15672
- RabbitMQ AMQP: localhost:5672
- MinIO API: http://localhost:9000
- MinIO Console: http://localhost:9001
- Elasticsearch: http://localhost:9200

## 5. Useful Checks

```bash
curl http://localhost:8088
curl http://localhost:8086
curl http://localhost:8083
curl http://localhost:8084
curl http://localhost:8087
curl http://localhost:8070
curl http://localhost:8085/actuator/health
curl http://localhost:8089/actuator/health
curl http://localhost:8080/auth
curl http://localhost:9000/minio/health/live
curl http://localhost:9200
```

## 6. Notes

- The local stack is intended for development only
- PostgreSQL uses the `bpm` database
- Camunda runs in self-managed full stack mode
- If you already have an existing `postgres_data` volume, remove it once so the Keycloak database/user init script can run
- MinIO, PostgreSQL, Elasticsearch, and RabbitMQ use Docker volumes for persistence
- Workflow Service and Integration Service are built from the Dockerfiles under `backend/`
