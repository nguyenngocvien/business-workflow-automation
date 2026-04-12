# Local Development Stack - README

This setup provides a complete local environment including:

* Camunda Orchestration, Optimize, Identity, Console, Connectors, Web Modeler
* Workflow Service
* Integration Service
* Keycloak (IAM)
* PostgreSQL
* MinIO (S3-compatible storage)
* RabbitMQ (message broker)

---

## Prerequisites

* Docker
* Docker Compose (v2+ recommended)

---

## Start the stack

```bash
./scripts/start.sh
```

Check containers:

```bash
docker ps
```

---

## Services Overview

### Camunda Platform Components

| Service       | URL                   | Description                   |
| ------------- | --------------------- | ----------------------------- |
| Orchestration  | http://localhost:8088 | Runtime, Operate, and Tasklist |
| Connectors     | http://localhost:8086 | Connector runtime              |
| Optimize       | http://localhost:8083 | Process analytics              |
| Identity       | http://localhost:8084 | Camunda identity service       |
| Console        | http://localhost:8087 | Platform administration UI     |
| Web Modeler    | http://localhost:8070 | Modeler UI                     |
| Zeebe Gateway  | localhost:26500      | gRPC endpoint for clients      |

### Backend Services

| Service            | URL                   | Description                          |
| ------------------ | --------------------- | ------------------------------------ |
| Workflow Service    | http://localhost:8085 | Workflow runtime and task APIs       |
| Integration Service | http://localhost:8089 | Connector and integration APIs       |

---

### Identity (Keycloak)

| Service                | URL                        |
| ---------------------- | -------------------------- |
| Keycloak Admin Console | http://localhost:8080/auth |

**Default credentials:**

```
username: admin
password: admin_password
```

---

### Object Storage (MinIO)

| Service       | URL                   |
| ------------- | --------------------- |
| MinIO API     | http://localhost:9000 |
| MinIO Console | http://localhost:9001 |

**Default credentials:**

```
username: minio
password: minio123
```

---

### Messaging (RabbitMQ)

| Service     | URL                    |
| ----------- | ---------------------- |
| RabbitMQ UI | http://localhost:15672 |
| AMQP Port   | localhost:5672         |

**Default credentials:**

```
username: guest
password: guest
```

---

### Elasticsearch (internal use)

| Service       | URL                   |
| ------------- | --------------------- |
| Elasticsearch | http://localhost:9200 |

Used internally by Camunda components.

---

## Quick Health Checks

```bash
# Orchestration Gateway
nc -z localhost 26500

# Elasticsearch
curl http://localhost:9200

# Keycloak
curl http://localhost:8080/auth

# MinIO
curl http://localhost:9000/minio/health/live

# RabbitMQ
curl http://localhost:15672

# Workflow Service
curl http://localhost:8085/actuator/health

# Integration Service
curl http://localhost:8089/actuator/health
```

---

## Useful Commands

### Stop services

```bash
./scripts/stop.sh
```

### Stop and remove volumes

```bash
./scripts/stop.sh -v
```

### View logs

```bash
docker compose logs -f
```

### Restart a service

```bash
docker compose restart <service-name>
```

---

## Notes

* This setup is intended for **local development only**
* PostgreSQL uses the `bpm` database
* Keycloak is configured for the Camunda identity stack
* If you already have an existing `postgres_data` volume, remove it once so the Keycloak database/user init script can run
* MinIO, PostgreSQL, Elasticsearch, and RabbitMQ use Docker volumes for persistence
* Camunda is running in **self-managed full stack mode**
* Workflow Service and Integration Service run from the Dockerfiles in `backend/`
