# Local Setup

## 1. Prerequisites

- Docker
- Docker Compose v2+

## 2. Start the stack

From the repository root:

```bash
./scripts/start.sh
```

If you want to start a specific Compose target, pass the service name:

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

### Backend Services

- Workflow Service: http://localhost:8083
- Integration Service: http://localhost:8084

### Camunda 8

- Operate: http://localhost:8081
- Tasklist: http://localhost:8082
- Zeebe Gateway: localhost:26500

### Identity and Messaging

- Keycloak: http://localhost:8080
- RabbitMQ UI: http://localhost:15672
- RabbitMQ AMQP: localhost:5672

### Storage and Search

- MinIO API: http://localhost:9000
- MinIO Console: http://localhost:9001
- Elasticsearch: http://localhost:9200

## 5. Useful Checks

```bash
curl http://localhost:8083
curl http://localhost:8084
curl http://localhost:8081
curl http://localhost:8082
curl http://localhost:8080
curl http://localhost:9000/minio/health/live
curl http://localhost:9200
```

## 6. Notes

- The local stack is intended for development only
- PostgreSQL uses the `bpm` database
- Workflow Service and Integration Service are built from the Dockerfiles under `backend/`
- MinIO, PostgreSQL, Elasticsearch, and RabbitMQ use Docker volumes for persistence
