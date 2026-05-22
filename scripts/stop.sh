#!/usr/bin/env sh
set -eu

SCRIPT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
PROJECT_ROOT=$(CDPATH= cd -- "$SCRIPT_DIR/.." && pwd)
COMPOSE_FILE="$PROJECT_ROOT/docker-compose.yml"

if docker compose version >/dev/null 2>&1; then
  COMPOSE_CMD="docker compose"
elif command -v docker-compose >/dev/null 2>&1; then
  COMPOSE_CMD="docker-compose"
else
  echo "Docker Compose is not available."
  exit 1
fi

cd "$PROJECT_ROOT"

echo "Stopping local stack from $PROJECT_ROOT"
if [ -n "${COMPOSE_ENV_FILE:-}" ]; then
  echo "Using env file: $COMPOSE_ENV_FILE"
  if [ "$#" -gt 0 ]; then
    $COMPOSE_CMD --env-file "$COMPOSE_ENV_FILE" -f "$COMPOSE_FILE" down --remove-orphans "$@"
  else
    $COMPOSE_CMD --env-file "$COMPOSE_ENV_FILE" -f "$COMPOSE_FILE" down --remove-orphans
  fi
else
  if [ "$#" -gt 0 ]; then
    $COMPOSE_CMD -f "$COMPOSE_FILE" down --remove-orphans "$@"
  else
    $COMPOSE_CMD -f "$COMPOSE_FILE" down --remove-orphans
  fi
fi
