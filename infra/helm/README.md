# Helm

This folder contains the Helm entrypoint for the local business workflow stack.

## Chart Location

```text
infra/helm/business-workflow-automation
```

## Install

```bash
helm install bwa ./infra/helm/business-workflow-automation \
  --namespace bpm-dev \
  --create-namespace
```

## Upgrade

```bash
helm upgrade --install bwa ./infra/helm/business-workflow-automation \
  --namespace bpm-dev
```

## Uninstall

```bash
helm uninstall bwa --namespace bpm-dev
```

## Render Manifests

```bash
helm template bwa ./infra/helm/business-workflow-automation \
  --namespace bpm-dev
```

## Notes

- The chart is intended to mirror the local stack defined in `docker-compose.yml`
- Values are kept in `values.yaml`
- You can create additional environment-specific overrides with `-f values-dev.yaml`
