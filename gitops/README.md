# GitOps Deployment Guide

This folder contains the Kubernetes deployment assets for the backend platform and its supporting infrastructure.

The layout is intentionally split into three layers:

- `gitops/infra/` for shared cluster services such as PostgreSQL, Keycloak, RabbitMQ, MinIO, Elasticsearch, and Camunda components
- `gitops/apps/` for one Helm chart per backend service
- `gitops/argocd/` for Argo CD bootstrap and per-service Application manifests

## What Gets Deployed

### Backend Services

The current backend service set covered by GitOps is:

- `api-gateway`
- `identity-service`
- `workflow-service`
- `connector-service`
- `document-service`
- `discovery-server`
- `notification-service`

### Shared Infrastructure

The infra layer provides the shared runtime dependencies used by the backend services:

- PostgreSQL
- Keycloak
- RabbitMQ
- MinIO
- Elasticsearch
- Camunda platform components

## Folder Layout

```text
gitops/
  apps/<service>/
    Chart.yaml
    values.yaml
    templates/
  environments/<env>/<service>/values.yaml
  argocd/
    project.yaml
    infra-root.yaml
    <service>-<env>.yaml
  infra/<component>/
    Chart.yaml
    values.yaml
```

## Deployment Model

The recommended deployment order is:

1. Install or sync the GitOps controller and the `argocd` namespace.
2. Apply the Argo CD project definition.
3. Deploy the shared infrastructure charts.
4. Deploy the backend service applications.
5. Override per-environment values as needed.

The service charts are intentionally simple and match the existing `identity-service` Helm pattern:

- `Chart.yaml` defines the chart name and version
- `values.yaml` contains default image, port, and environment settings
- `templates/deployment.yaml` creates the workload
- `templates/service.yaml` exposes the workload
- `templates/ingress.yaml` is used only by `api-gateway`

## Prerequisites

Before applying anything, make sure the target cluster has:

- `kubectl` access to the cluster
- `helm` installed locally
- Argo CD installed in the cluster if you want to use the Argo workflow
- an ingress controller if you want `api-gateway` ingress to work
- a storage class for persistent workloads used by the infra layer

## Namespace Strategy

This repo uses separate namespaces by environment:

- `dev`
- `staging`
- `prod`

The Argo CD manifests are written to create or target those namespaces, depending on the environment file you apply.

## Shared Infrastructure

The infra manifests live under `gitops/infra/`.

Those charts are meant to be deployed before the services because the backend applications depend on them for:

- database connections
- authentication and JWT issuance
- message transport
- object storage
- workflow indexing and process runtime support

If you are deploying to a fresh cluster, install infra first and confirm the endpoints are healthy before syncing app workloads.

## Backend Service Charts

Each backend service has its own Helm chart under `gitops/apps/<service>/`.

Example:

```text
gitops/apps/identity-service/
gitops/apps/api-gateway/
gitops/apps/workflow-service/
```

Each chart is deployed independently, which makes it easier to:

- roll one service forward without touching the rest
- tune replicas and tags per environment
- keep service-specific config close to the service chart

## Environment Overrides

Each environment has service-specific overrides under:

- `gitops/environments/dev/<service>/values.yaml`
- `gitops/environments/staging/<service>/values.yaml`
- `gitops/environments/prod/<service>/values.yaml`

These files are where you should change:

- image tags
- replica counts
- ingress hostnames
- environment-specific Spring profiles

Keep secrets out of these files when possible. If a value is sensitive, move it to a Kubernetes `Secret` or a sealed-secret flow in your cluster setup.

## Argo CD Manifests

The Argo CD directory contains:

- `project.yaml` for the backend service AppProject
- `infra-root.yaml` for the infrastructure bootstrap entrypoint
- one `Application` manifest per service and environment

Examples:

```text
gitops/argocd/identity-service-dev.yaml
gitops/argocd/api-gateway-staging.yaml
gitops/argocd/workflow-service-prod.yaml
```

Each application manifest points Argo CD at the chart path under `gitops/apps/<service>` and loads the matching environment override file.

## Recommended Bootstrap Flow

### 1. Prepare the cluster

Create or confirm the Argo CD namespace exists:

```bash
kubectl create namespace argocd --dry-run=client -o yaml | kubectl apply -f -
```

### 2. Apply the backend AppProject

```bash
kubectl apply -f gitops/argocd/project.yaml
```

This project is the policy boundary for backend application syncs.

### 3. Bootstrap infra

Apply the infra root manifest if you are using the GitOps bootstrap path:

```bash
kubectl apply -f gitops/argocd/infra-root.yaml
```

If your cluster uses a different app-of-apps or you manage infra separately, you can skip this step and apply the infra charts directly.

### 4. Sync shared infrastructure

If you are applying infra directly with Helm, render and install the charts under `gitops/infra/`.

Example:

```bash
helm template postgres gitops/infra/postgres
helm template keycloak gitops/infra/keycloak
```

Once the manifests look right, install or sync them through your preferred cluster workflow.

### 5. Sync a backend service

Apply the Argo CD Application for the service you want:

```bash
kubectl apply -f gitops/argocd/identity-service-dev.yaml
```

Repeat for other services and environments as needed.

## Local Chart Rendering

You can render any service chart locally before pushing to the cluster.

Example:

```bash
helm template api-gateway gitops/apps/api-gateway \
  -f gitops/environments/dev/api-gateway/values.yaml
```

For a non-HTTP service:

```bash
helm template workflow-service gitops/apps/workflow-service \
  -f gitops/environments/prod/workflow-service/values.yaml
```

Rendering locally is the fastest way to catch:

- indentation mistakes
- missing values
- invalid YAML
- path mismatches between Argo CD and the repo layout

## Updating a Service

When you change a backend service deployment, keep the update flow simple:

1. Update the chart under `gitops/apps/<service>/`
2. Update the matching environment file under `gitops/environments/<env>/<service>/`
3. Apply or sync the Argo CD Application for that service

Typical changes include:

- new image tags
- new environment variables
- replica changes
- ingress hostname changes
- health probe adjustments

## Updating Images

The charts expect service images to be built and published separately.

For example:

- `viennn/identity-service:dev`
- `viennn/identity-service:staging`
- `viennn/identity-service:prod`

If you use a different registry, update the `image.repository` value in the chart or the environment override file.

## Notes on Secrets

Several services need sensitive runtime values such as:

- database passwords
- mail passwords
- Keycloak client secrets
- encryption keys

Do not hardcode production secrets in the plain values files.

For real deployments, prefer one of these patterns:

- Kubernetes `Secret`
- External Secrets Operator
- sealed-secrets
- a cloud secret manager integration

## Notes on API Gateway

`api-gateway` is the public entry point for backend traffic.

It is the only service in this GitOps set that exposes ingress by default, and it routes requests to the backend services by service name inside the cluster.

## Notes on Discovery

`discovery-server` is deployed as its own service chart so the other services can register with it when service discovery is enabled.

If your cluster uses a different discovery strategy, you can keep the chart but disable the service-level registration settings in the backend application config.

## Troubleshooting

### Argo CD cannot find the chart path

Check that `spec.source.path` points to the repo-relative path:

- `gitops/apps/<service>`

### Service pods start but cannot reach dependencies

Verify the infra layer is deployed and healthy:

- PostgreSQL
- Keycloak
- RabbitMQ
- MinIO
- Elasticsearch

### Ingress does not route to the gateway

Check that:

- your cluster has an ingress controller
- the host name in the environment override matches your DNS or local test host
- the service is synced in the right namespace

### Helm render fails

Render the chart directly with the matching environment file and inspect the output:

```bash
helm template <release-name> gitops/apps/<service> -f gitops/environments/<env>/<service>/values.yaml
```

## Operational Checklist

Before promoting an environment, confirm:

- the image tag is updated
- the namespace exists or can be created by Argo CD
- secrets are available in the target cluster
- infra dependencies are healthy
- the chart renders without errors
- the Argo CD application points to the correct `gitops/apps/<service>` path
