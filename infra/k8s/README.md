# Kubernetes Stack

This directory mirrors the local `docker-compose.yml` stack using Kustomize.

## Deploy

```bash
kubectl apply -k infra/k8s/overlays/dev
```

## Remove

```bash
kubectl delete -k infra/k8s/overlays/dev
```

## Notes

- The base layer defines Postgres, Elasticsearch, Zeebe, Operate, Tasklist, Keycloak, MinIO, and RabbitMQ.
- The `dev`, `staging`, and `prod` overlays each create their own namespace and point to the shared base.
- Postgres, Elasticsearch, and MinIO use persistent volume claims.
