# Java 21 Virtual Threads — Phase 1

A controlled Spring Boot 3.3.13 benchmark comparing traditional Tomcat platform-thread request handling and Java 21 virtual-thread request handling. Same Java source, same DB connection pool configuration, different thread setting.

## Prerequisites
- Docker Desktop / Docker Engine with Compose v2
- Optional: Java 21 + Maven for running outside Docker

## Run
```bash
docker compose up --build -d
docker compose ps
```

## Test endpoints
```bash
curl 'http://localhost:8081/api/benchmark/io?delayMs=100'
curl 'http://localhost:8082/api/benchmark/io?delayMs=100'
curl 'http://localhost:8081/api/benchmark/db'
curl 'http://localhost:8082/api/benchmark/db'
curl 'http://localhost:8081/actuator/health'
curl 'http://localhost:8082/actuator/prometheus'
```

Expected: `virtualThread` false on 8081 and true on 8082 for `/io`.

## Important benchmark controls
- `/io` uses `Thread.sleep` to simulate I/O wait. It is NOT an actual network or DB workload.
- `/db` queries PostgreSQL via JDBC and HikariCP; connection pool is capped at 10 per app.
- Test one app at a time, warm up both, hold CPU/memory/DB limits and load profile constant, and repeat runs.
- Both containers use the same PostgreSQL instance. Shared resource contention can bias results; do not benchmark both concurrently.
- Platform mode uses Tomcat's configured max 200 request threads; virtual mode uses virtual threads for request handling.
- The development DB password and exposed ports are NOT production-safe. Replace with secrets, TLS, and restricted networking before deploying anywhere public.
- Metrics endpoint is exposed for local development only.
- Phase 2 adds a downstream HTTP service and realistic SQL queries; Phase 3 adds Prometheus/Grafana dashboards; Phase 4 adds k6 scripts.

## Stop
```bash
docker compose down
```
To delete DB volume as well: `docker compose down -v`.
