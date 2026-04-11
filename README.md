# Local Development Stack - README

This setup provides a complete local environment including:

* Camunda 8 (Zeebe, Operate, Tasklist)
* Keycloak (IAM)
* MinIO (S3-compatible storage)
* RabbitMQ (message broker)

---

## Prerequisites

* Docker
* Docker Compose (v2+ recommended)

---

## Start the stack

```bash
docker compose up -d
```

Check containers:

```bash
docker ps
```

---

## Services Overview

### Camunda 8 Components

| Service       | URL                   | Description                   |
| ------------- | --------------------- | ----------------------------- |
| Operate       | http://localhost:8081 | Monitor and inspect workflows |
| Tasklist      | http://localhost:8082 | Human task UI                 |
| Zeebe Gateway | localhost:26500       | gRPC endpoint for clients     |

---

### Identity (Keycloak)

| Service                | URL                   |
| ---------------------- | --------------------- |
| Keycloak Admin Console | http://localhost:8080 |

**Default credentials:**

```
username: admin
password: admin
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

Used internally by Camunda (Operate, Tasklist).

---

## Quick Health Checks

```bash
# Zeebe Gateway
nc -z localhost 26500

# Elasticsearch
curl http://localhost:9200

# Keycloak
curl http://localhost:8080

# MinIO
curl http://localhost:9000/minio/health/live

# RabbitMQ
curl http://localhost:15672
```

---

## Useful Commands

### Stop services

```bash
docker compose down
```

### Stop and remove volumes

```bash
docker compose down -v
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
* Keycloak runs in **dev mode** (no persistence)
* MinIO data is persisted via Docker volume
* Camunda is running in **self-managed minimal mode**
* RabbitMQ is not wired to Camunda by default