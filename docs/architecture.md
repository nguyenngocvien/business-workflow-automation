# Architecture Overview

This repository is a local-first workflow automation platform built around a shared infrastructure stack and several Spring Boot backend services.

## Goals

- Run workflow orchestration with Camunda Zeebe
- Expose business APIs through a gateway
- Centralize identity with Keycloak
- Persist business, identity, and connector data in PostgreSQL
- Store documents and binary payloads in MinIO
- Support asynchronous jobs and integration-style processing with RabbitMQ

## Runtime Layers

### 1. Shared Infrastructure

The local stack provides these shared services:

- PostgreSQL for relational persistence
- Keycloak for authentication and JWT issuance
- RabbitMQ for asynchronous messaging
- MinIO for object storage
- Elasticsearch for Zeebe and Operate indexing/search
- Zeebe for workflow execution
- Operate for process monitoring
- pgAdmin and Kibana for operational access

### 2. Application Services

The backend application layer is split into dedicated services:

- `api-gateway`: public entry point for backend APIs
- `discovery-server`: service registry for service-to-service discovery
- `identity-service`: user, role, and access management
- `workflow-service`: workflow runtime, tasks, and process data
- `connector-service`: external integration, email, database, and job execution logic
- `document-service`: file metadata, versions, and MinIO-backed storage access

### 3. Frontend

The frontend is the user-facing admin portal (`frontend/admin-portal`) and talks to the backend through HTTP APIs, usually via the gateway.

## Data Ownership

- PostgreSQL owns transactional and configuration data
- MinIO owns binary file content and upload artifacts
- RabbitMQ carries background jobs and asynchronous integration events
- Elasticsearch stores Camunda workflow search/index data

## Request Flow

The typical request path is:

1. A user interacts with the frontend.
2. The frontend sends API requests to `api-gateway` or directly to a service during local development.
3. The gateway routes requests to the relevant backend service.
4. The service persists data in PostgreSQL or stores files in MinIO.
5. The workflow service talks to Zeebe for orchestration and uses RabbitMQ for async work where needed.
6. Operate observes running workflows through Elasticsearch and Zeebe.

## Deployment Modes

The repository supports three infrastructure entry points:

- `docker-compose.yml` for local development
- `infra/k8s` for Kubernetes manifests and overlays
- `infra/helm` for Helm-based installation scaffolding

The Kubernetes base mirrors the shared infra stack. The Helm chart is currently a thin wrapper that defines the namespace and configuration values, so the Compose stack remains the most complete runtime definition in the repo.

## Important Notes

- The Compose stack is intended for development, not production.
- PostgreSQL is initialized with multiple databases, including `bpm`, `keycloak`, `edocument`, `identity_db`, and `connector`.
- `identity-service` is exposed on host port `8081`.
- `operate` is exposed on host port `8088`.
- `docs/architecture.md` should be treated as the canonical high-level description of the current repo structure, not the older `integration-service` naming that appears in some stale notes.
