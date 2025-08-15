# Service Implementation Backlog (Phase 1)

Title: Service Implementation Backlog (Phase 1)
Version: 0.1.0
Last Updated: 2025-08-15
Owner: Anderson (Sole Maintainer) <andersonfilho09@gmail.com>
Status: Draft
Decision: Seed actionable GitHub issues for core service enablement

> Purpose: Copy/paste the sections below into new GitHub issues (or use `gh issue create -F <file>` after splitting) to bootstrap structured tracking. Each epic references granular tasks that can be split further if scope grows.

Label Suggestions:
- component/gateway, component/game-service, component/user-service, component/reco-service, component/igdb-etl
- epic, feature, task, tech-debt, security, docs
- priority/P1 (critical path), priority/P2, priority/P3

---

## EPIC: Gateway Service Foundations
**Goal**: Production-ready lightweight API edge with health, structured logging, and error handling.
**Labels**: epic, component/gateway
**Success Metrics**: p95 < 50ms for health & config endpoints locally; basic 99% uptime in dev runs; structured logs with trace_id.

### Child Issues
1. TASK: Gateway – Project Restructure & Module Layout
   - Create internal packages: config, http, middleware, handlers
   - Add Makefile targets (build, lint, test)
   - Acceptance: `go build ./...` passes; lint script placeholder added.
2. TASK: Gateway – Health & Info Endpoints
   - `/health` (liveness), `/ready` (readiness), `/info` (version, commit hash)
   - Include VERSION file read
3. TASK: Gateway – Central Error Handling Middleware
   - Map errors to JSON `{code,message,trace_id}`
4. TASK: Gateway – Request Logging & Correlation
   - Generate/propagate `trace_id`; log method, path, status, latency
5. TASK: Gateway – Config Management
   - Load env vars (DATABASE_URL, KAFKA_BROKERS, RABBITMQ_URL, LOG_LEVEL)
   - Fail fast on required missing
6. TASK: Gateway – Graceful Shutdown & Timeouts
   - HTTP server with context cancellation, 60s shutdown timeout
7. TASK: Gateway – Basic Rate Limiter Skeleton (Flagged)
   - Token bucket or placeholder; disabled by default via env flag
8. TASK: Gateway – OpenTelemetry Instrumentation
   - Spans for requests; export OTLP when endpoint provided

---

## EPIC: Game Service Domain & Persistence
**Goal**: CRUD/read endpoints for core Game catalog using existing Postgres schema.
**Labels**: epic, component/game-service
**Success Metrics**: GET /games/{id} median < 40ms local; integration tests green.

### Child Issues
1. TASK: Game Service – Adopt Flyway or Disable Auto DDL
   - Ensure JPA doesn’t attempt schema generation (schema is migration-managed)
2. TASK: Game Service – JPA Entity Modeling (Phase 1 subset)
   - Entities: Game, Genre, Theme, Platform, GameGenre link (minimal fields)
3. TASK: Game Service – Repositories & DTO Mappers
4. TASK: Game Service – Read Endpoints
   - `/games/{id}`, `/games?slug=`, `/games/popular?limit=20`
5. TASK: Game Service – Popularity Composite Endpoint
   - Returns `popularity`, `popularity_composite`
6. TASK: Game Service – Pagination & Basic Filtering
   - limit/offset, filter by genre_id
7. TASK: Game Service – Integration Tests (Testcontainers)
8. TASK: Game Service – Caching Layer (Optional P2)
   - Simple in-memory for hot game lookups
9. TASK: Game Service – Metrics (Micrometer)
   - Timers per endpoint, DB query count gauge
10. TECH-DEBT: Game Service – Entity Validation Annotations

---

## EPIC: User Service Basic Profiles
**Goal**: Internal user representation + authentication stub for linking external accounts.
**Labels**: epic, component/user-service

### Child Issues
1. TASK: User Service – User Entity + Repository
2. TASK: User Service – Auth Stub (JWT) with In-Memory Secret
3. TASK: User Service – Account Linking Placeholder (Steam only)
4. TASK: User Service – User Profile GET Endpoint
5. TASK: User Service – Validation & Error Model Alignment with Gateway
6. TASK: User Service – Integration Tests (Testcontainers)
7. TASK: User Service – Security Headers & Basic Rate Limit (Optional)

---

## EPIC: IGDB ETL Incremental Ingestion
**Goal**: Reliable incremental pull of games & dimensions using watermark.
**Labels**: epic, component/igdb-etl
**Success Metrics**: Processes delta batch (≤100 games) under 30s; watermark advances each run.

### Child Issues
1. TASK: IGDB ETL – Config & OAuth Token Retrieval
2. TASK: IGDB ETL – Watermark Read/Write (ingestion_watermark)
3. TASK: IGDB ETL – Fetch Updated IDs > watermark
4. TASK: IGDB ETL – Batch Fetch Full Records (Rate Limit Aware)
5. TASK: IGDB ETL – Dimension Upsert (Genre/Theme/Platform minimal)
6. TASK: IGDB ETL – Game Upsert (core subset fields)
7. TASK: IGDB ETL – Retry & Backoff Strategy Implementation
8. TASK: IGDB ETL – Metrics (fetched_count, upsert_count, lag_seconds)
9. TASK: IGDB ETL – Structured Logging + Trace IDs
10. TASK: IGDB ETL – Dead Letter Handling Strategy (placeholder table or log)
11. TECH-DEBT: IGDB ETL – Add Achievements Ingestion (Phase 2 Flag)

---

## EPIC: Recommendation Service MVP
**Goal**: Baseline recommendation API using popularity fallback.
**Labels**: epic, component/reco-service

### Child Issues
1. TASK: Reco – Service Scaffold (FastAPI / Flask) + Dockerfile
2. TASK: Reco – /health and /ready Endpoints
3. TASK: Reco – Popularity-Based Recommendation Endpoint `/recommendations?user_id=`
4. TASK: Reco – Input Validation & Error Schema Alignment
5. TASK: Reco – Unit Tests (Algorithm & API)
6. TASK: Reco – OpenTelemetry & Prometheus Metrics (latency, rec_generation_time_ms)
7. TASK: Reco – Simple Ranking Logic (popularity_composite DESC + diversity constraint placeholder)
8. TASK: Reco – Configurable Recommendation Limit (env var)
9. TECH-DEBT: Reco – Candidate Caching Layer (Optional)

---

## EPIC: Observability & SLO Baseline
**Goal**: Consistent metrics, tracing, health endpoints aligned with docs.
**Labels**: epic, component/gateway, component/*

### Child Issues
1. TASK: Observability – Define Metrics Registry Names & Prefixes
2. TASK: Observability – Add Trace Context Propagation (gateway → services)
3. TASK: Observability – Standard Health & Readiness across services
4. TASK: Observability – Error Rate & Latency Dashboards (placeholders)
5. TASK: Observability – Logging Format Normalization (JSON) across languages
6. TASK: Observability – Add p95 Tracking for Key Endpoints

---

## EPIC: Security & Compliance Hardening Phase 1
**Goal**: Baseline security controls enforced early.
**Labels**: epic, security

### Child Issues
1. SECURITY: Add Dependency Scanning Config (placeholder script)
2. SECURITY: Secret Handling Audit (.env usage & readme warnings)
3. SECURITY: Gateway Security Headers Middleware (CSP/report-only, X-Content-Type-Options, etc.)
4. SECURITY: Add Basic Input Validation (length, regex) for Slug Params
5. SECURITY: Add Security Section to CHANGELOG for Hardening Changes
6. SECURITY: Threat Model Doc Link from README

---

## EPIC: Documentation & Governance Consistency
**Goal**: Keep docs synchronized with implementation changes.
**Labels**: epic, docs

### Child Issues
1. DOCS: Update README with Service Endpoints Table (after initial endpoints exist)
2. DOCS: Add API Reference Placeholder for Game & User Services
3. DOCS: Add Ingestion Flow Diagram (Mermaid) referencing ETL
4. DOCS: Update Glossary with New Terms (watermark lag, batch window)
5. DOCS: Add Backfill Procedure Script References

---

## Tracking Matrix (Initial Priorities)
| Issue (Short Name) | Epic | Priority | Notes |
|--------------------|------|----------|-------|
| Gateway health/info | Gateway Foundations | P1 | Enables readiness checks |
| Game entities JPA   | Game Service Domain | P1 | Core schema binding |
| IGDB watermark impl | IGDB ETL | P1 | Unlocks incremental ingestion |
| Reco popularity MVP | Reco MVP | P1 | Baseline recommendations |
| Observability trace | Observability | P1 | Cross-service debugging |
| Security headers    | Security Hardening | P2 | Mitigate basic threats |
| User auth stub      | User Service | P2 | Needed for personalized recs |

---

## Usage Instructions
1. Create the EPIC issues first using the Epic template.
2. Create child tasks referencing the EPIC via `Parent: #<epic-issue-id>`.
3. Apply labels immediately (automation can be added later).
4. Update this backlog file or close items as delivered (optional – treated as living doc).

## Revision History
- v0.1.0 (2025-08-15): Initial backlog seeding.

