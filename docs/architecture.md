# Architecture Overview

This repository is organized as a workflow automation platform with two backend services, a frontend, and a shared local infrastructure stack.

## System Goals

- Manage workflow definitions and workflow runtime
- Integrate with external systems through connector-style services
- Provide local development infrastructure for persistence, messaging, identity, and workflow orchestration

## High-Level Layout

The codebase is split into these main areas:

- `backend/workflow-service`: workflow runtime, task handling, and business workflow persistence
- `backend/integration-service`: connector and integration logic for external systems
- `frontend`: user-facing application
- `infra/k8s`: Kubernetes manifests that mirror the local stack
- `docker-compose.yml`: local development stack for all infrastructure services

## Main Components

### 1. Workflow Service

The workflow service is the core business backend. It is responsible for:

- workflow definition management
- workflow execution and step progression
- task assignment, claiming, reassignment, and completion
- audit/history data for process tracking

It uses PostgreSQL for persistence and Flyway for schema migration.

### 2. Integration Service

The integration service is responsible for external integrations and connector-related logic. It typically acts as a bridge between workflow events and third-party systems such as APIs, storage, or messaging endpoints.

It also uses PostgreSQL and Flyway for database-backed configuration and runtime data.

### 3. Frontend

The frontend provides the user interface for interacting with the platform. It is expected to communicate with the backend services through HTTP APIs.

### 4. Infrastructure Stack

The local stack includes:

- PostgreSQL for relational data
- RabbitMQ for asynchronous messaging
- MinIO for object storage
- Keycloak for identity and authentication
- Elasticsearch for Camunda search and indexing
- Zeebe, Operate, and Tasklist for workflow orchestration and monitoring

## Runtime Flow

Typical request flow looks like this:

1. A user interacts with the frontend.
2. The frontend calls the workflow service or integration service.
3. The backend service persists business data in PostgreSQL.
4. If needed, the backend publishes messages to RabbitMQ or stores files in MinIO.
5. Workflow orchestration is handled by Camunda components through Zeebe.
6. Operate and Tasklist are used to inspect and manage workflow state.

## Data Ownership

- PostgreSQL owns transactional business data
- MinIO owns binary or document-like artifacts
- RabbitMQ carries asynchronous events and jobs
- Elasticsearch stores Camunda workflow indexing data

## Environment Strategy

The repository supports two infrastructure styles:

- `docker-compose.yml` for fast local development
- `infra/k8s` for Kubernetes-based deployment

Both are intended to expose the same logical services so the development setup stays close to deployment behavior.

## Configuration Notes

- Local database defaults use `postgres` / `postgres`
- The shared workflow database name in compose is `bpm`
- Keycloak runs in development mode
- Camunda services are configured for a self-managed local stack

## Suggested Diagram

If you want to add a visual architecture diagram later, a good layout would be:

- Frontend at the top
- Workflow Service and Integration Service in the middle
- PostgreSQL, RabbitMQ, MinIO, Keycloak, and Camunda services at the bottom

That diagram should show the backend services as the main application layer and the infrastructure components as shared dependencies.
