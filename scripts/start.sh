#!/usr/bin/env sh
set -eu

SCRIPT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
PROJECT_ROOT=$(CDPATH= cd -- "$SCRIPT_DIR/.." && pwd)

if docker compose version >/dev/null 2>&1; then
  COMPOSE_CMD="docker compose"
elif command -v docker-compose >/dev/null 2>&1; then
  COMPOSE_CMD="docker-compose"
else
  echo "Docker Compose is not available."
  exit 1
fi

cd "$PROJECT_ROOT"

echo "Starting local stack from $PROJECT_ROOT"
if [ "$#" -gt 0 ]; then
  $COMPOSE_CMD up -d --build "$@"
else
  $COMPOSE_CMD up -d --build
fi
