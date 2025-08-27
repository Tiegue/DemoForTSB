# ---- Project settings ----
APP_NAME := demofortsb
VERSION  ?= local
IMAGE    := $(APP_NAME):$(VERSION)
MVN      ?= mvn

# ---- Helper ----
.PHONY: help
help:
	@echo "Usage:"
	@echo "  make build          # Build JAR (skip tests)"
	@echo "  make test           # Run tests"
	@echo "  make run            # Run the built JAR locally"
	@echo "  make docker-build   # Build Docker image"
	@echo "  make up             # docker compose up -d"
	@echo "  make down           # docker compose down"
	@echo "  make logs           # Tail app logs"
	@echo "  make ps             # List compose services"
	@echo "  make clean          # mvn clean and remove target"

# ---- Java build/run ----
.PHONY: build test run clean
build:
	$(MVN) -q -DskipTests package

test:
	$(MVN) -q test

run: build
	java -jar target/*-SNAPSHOT.jar

clean:
	$(MVN) -q clean
	rm -rf target

# ---- Docker / Compose ----
.PHONY: docker-build up down logs ps
docker-build:
	docker build -t $(IMAGE) .

up:
	docker compose up -d --build

down:
	docker compose down -v

logs:
	docker compose logs -f app

ps:
	docker compose ps

# ---- Only app ----
.PHONY: app-up app-rebuild app-restart clean-app

# Rebuild image and (re)start only the app service
app-up:
	docker compose up -d --build app

# Rebuild image only (no start)
app-rebuild:
	docker compose build app

# Fast restart app with rebuild, without touching db/prom/grafana
app-restart:
	docker compose up -d --no-deps --build app

# Remove only the app container/image (keeps volumes/data)
clean-app:
	docker compose rm -sf app || true
	docker rmi demofortsb:local || true

# ---- Run local app with docker prometheus and grafana----
.PHONY: dev-local dev-local-fast dev-local-jar obs-up obs-down obs-logs


# Clean install (runs tests), then run from sources with the local profile
dev-local:
	$(MVN) -q clean install -Dspring.profiles.active=local -Dspring.liquibase.contexts=local
	SPRING_PROFILES_ACTIVE=local $(MVN) -q spring-boot:run

# Faster loop: skip tests on install
dev-local-fast:
	$(MVN) -q clean install -DskipTests
	SPRING_PROFILES_ACTIVE=local $(MVN) -q spring-boot:run

# Clean install, then run the packaged JAR with the local profile
dev-local-jar:
	$(MVN) -q clean install
	java -jar target/*-SNAPSHOT.jar --spring.profiles.active=local

obs-up:
	docker compose up -d prometheus grafana

obs-logs:
	docker compose logs -f prometheus grafana

obs-down:
	docker compose stop prometheus grafana