# demo-for-tsb development log

```

make dev-local         # runs tests
make dev-local-fast    # skips tests (faster)
make dev-local-jar     # runs the built JAR
make obs-up      # start Prometheus + Grafana in Docker
make obs-logs    # tail their logs
make obs-down    # stop them
```

## Stage1 
### Step 1.1: Create project directory structure
- build architecture (project tree)
  - customer entity and related controller, service.
  - Disable all auth.
- basic pom.xml
### Step 1.2: Design an exception handler aspect based on the one in HappyGigs.
- BASE EXCEPTION HIERARCHY
  - Exception for resource not found scenarios
  - Exception for unauthorized access
  - Exception for forbidden operations
  - Exception for data validation errors
- DOMAIN-SPECIFIC EXCEPTIONS(Customer & Account)
### Step 1.3: Configure docker 
- Add docker-compose.yml, Makefile, Dockerfile, and .dockerignore
### Step 1.4 Add observability features
- Implement prometheus, grafana, postgresql, app
- Add ops directory for prometheus, grafana, postgresql
- Add health check.
- Add seq structured logging.
- postgresql in docker, and h2 in local
success!!!
#### Quick checks
##### 1) App health
curl -s http://localhost:8080/actuator/health

##### 2) Prometheus can scrape the app
open http://localhost:9090/targets     # demofortsb-app should be UP
open http://localhost:9090/graph        # try: up{job="demofortsb-app"}

##### 3) Metrics exposed by Spring/ Micrometer
open http://localhost:8080/actuator/prometheus

##### 4) Grafana login
open http://localhost:3000              # admin / admin

Add Grafana → Prometheus datasource (one-time)

In Grafana UI:

Settings (gear) → Data sources → Add data source → Prometheus

URL: http://prometheus:9090

Save & test → should show “Data source is working”.

Then Dashboards → Import and try these Micrometer-friendly dashboards:

JVM (Micrometer): 4701

Spring Boot 3 / Micrometer: 19004 (or any you prefer)

##### 5) Optional: auto-provision Grafana (skip the UI clicks)
docker-compose.yml – add to the grafana service:

grafana:
# ...
volumes:
- grafana-data:/var/lib/grafana
- ./ops/grafana/provisioning:/etc/grafana/provisioning:ro


ops/grafana/provisioning/datasources/datasource.yml

apiVersion: 1
datasources:
- name: Prometheus
  type: prometheus
  access: proxy
  url: http://prometheus:9090
  isDefault: true


ops/grafana/provisioning/dashboards/dashboards.yml

apiVersion: 1
providers:
- name: 'Default'
  orgId: 1
  folder: ''
  type: file
  options:
  path: /etc/grafana/provisioning/dashboards/json


ops/grafana/provisioning/dashboards/json/README.md

Drop exported dashboard JSON files here to auto-import on startup.

```aiignore
postgresdb command in docker cli
psql -U tsb -d demofortsb
enter pw tsb
pg_isready -U tsb -d demofortsb
\dt # show table
\l
\q # quit
```

## to-do list
- structured logging: seq, logback
- exception handling deal with traceid
- health check

## Structured logging desgin
- logback: add both correlationId and businessId which shows readable info in console.
- Seq
### Multipole layers sensitive data solution.
masking by MaskingConverter.class
compare safe logging helper, and wechat procode chouchang tuoming
- configure local and docker profiles
local plain, docker json
- docer





