#!/bin/bash
set -euo pipefail

echo "⏳ Waiting for services to be ready..."

# Wait for PostgreSQL
until docker-compose exec -T db pg_isready -U tsb -d demofortsb; do
  echo "  🔄 Waiting for PostgreSQL..."
  sleep 2
done
echo "  ✅ PostgreSQL is ready"

# Wait for Redis (with password for Docker)
until docker-compose exec -T redis redis-cli -a redis123 ping | grep -q PONG; do
  echo "  🔄 Waiting for Redis..."
  sleep 2
done
echo "  ✅ Redis is ready"

echo "✅ All services are ready!"