# Service & ETL Implementation Plan

Title: Service & ETL Implementation Plan (Gateway, Game, User, Reco, IGDB ETL, Steam Enrichment)  
Version: 0.1.1  
Last Updated: 2025-08-15  
Owner: Anderson (Sole Maintainer) <andersonfilho09@gmail.com>  
Status: Draft  
Decision: Provide implementation blueprint (phased) for each microservice & data pipeline to reach MVP (Phase 1–4)  

> Scope: This plan operationalizes previously approved strategy & domain design docs (see /docs/domain & ETL runbooks). Console (PSN/XBL) remains DESIGN-ONLY (feature‑flagged, no ingestion). Document is implementation‑oriented, listing concrete tasks, dependencies, acceptance criteria, and success metrics.

## 1. High-Level Timeline (Indicative, Single Maintainer)
| Phase | Weeks | Focus | Exit Snapshot |
|-------|-------|-------|---------------|
| Phase 0 | 1 | Foundations (DB, CI, observability skeleton) | Migrations automated, smoke tests pass |
| Phase 1 | 2–4 | Canonical Catalog (IGDB), baseline APIs, simple content-based recommendations | <30m freshness, core read endpoints live |
| Phase 2 | 5–8 | Steam enrichment (linking, playtime, popularity composite) | Popularity composite populated, link coverage >60% |
| Phase 3 | 9–11 | Achievements & extended metadata, improved recommender | Achievements coverage targets, enriched ranking |
| Phase 4 | 12–16 | Hardening, performance, DQ & partition planning | SLOs met, backlog groomed |

Assumption: Sequential focus, limited parallelism. Adjust if team grows.

## 2. Cross-Cutting Foundations (Phase 0 Core)
| Area | Task | Detail | Acceptance |
|------|------|--------|-----------|
| Migrations | Add automation script | `make db.migrate` (Makefile added) | One-command fresh setup |
| Smoke SQL | Script | `make db.smoke` runs scripts/db_smoke.sql | Pass on CI |
| Observability | OTEL wiring baseline | Traces for gateway + game-service + IGDB ETL | Spans visible in collector logs |
| Config | `.env.example` audit | Remove unused, add required (IGDB keys) | Updated example |
| Security | Role separation | `app_user` vs `migrator` (0004 migration) | Roles created & used |
| Docs | Add Implementation Plan (this) | Linked in index & changelog | Link present |
| Timezone | Force UTC | DB parameter + application default | All timestamps UTC |

## 2.1 Solo Execution Addendum
Optimizations because only one maintainer executes all phases:
- Strict WIP Limit: Max 1 service + 1 ETL task concurrently to avoid context loss.
- Daily Flow (Ideal): 1) Migrations & schema adj, 2) ETL ingestion test batch, 3) API feature, 4) Observability increment, 5) Short retro (notes in CHANGELOG Unreleased).
- Timeboxing: If an ETL batch exceeds 2h debugging, park & log issue; proceed with next API slice.
- Automation Priority Order: Migrations > Smoke Tests > Metrics > Retry logic > Caching.
- Definition of Done Gate: Reject merge if: missing metrics OR missing update to revision history OR no smoke success.
- Fast Rebuild Command Set: `make db.setup` then run docker-compose (documented in root README TODO).
- Weekly Debt Sweep (Friday): review dq_issue + ingestion_watermark drift anomalies.

## 3. Shared New Tables / DDL (Planned)
| Table | Purpose | Columns (Key) | Phase |
|-------|---------|---------------|-------|
| ingestion_watermark | Track per-source incremental progress | entity, source, last_value, updated_at | 1 |
| ingestion_dlq | Persist failed payloads | id, topic, payload, error_code, attempts, first_seen_at | 2 |
| dq_issue | Data quality tracking | issue_id, code, entity_type, entity_id, severity, status, created_at | 2 |
| steam_playtime_30d | Aggregated recent minutes | game_id PK, recent_minutes_30d, updated_at | 2 |
| steam_link_candidate | Store unresolved/ambiguous matches | id, game_id, attempt_count, reason, last_attempt_at | 2 |
| popularity_recompute_audit | Track recompute batches | batch_id, started_at, finished_at, row_count | 2 |

(Actual migrations to be added incrementally; see task lists.)

---
## 4. Service Implementation Blueprints
Each section uses a consistent structure: Scope, Responsibilities, Interfaces, Config, Phased Tasks, Acceptance, Metrics, Risks, Backlog.

### 4.1 Gateway Service (Go)
**Core Role:** Single external entrypoint, request authentication, routing to internal services, basic rate limiting, future circuit breaking.

**In Scope (MVP):** Reverse proxy to user-service, game-service, reco-service; API key auth (temporary), request logging, correlation id propagation.  
**Out of Scope (Now):** OAuth, WebSockets, caching of large payloads, advanced traffic shaping.

**External Interfaces:**
- HTTP (Public): `/api/v1/games`, `/api/v1/recommendations`, `/api/v1/users/*`
- Emits (future): Access logs (structured), request metrics.

**Configuration:** `PORT`, `LOG_LEVEL`, `KAFKA_BROKERS` (placeholder), `RATE_LIMIT_RPS`, `ALLOWED_API_KEYS`.

**Phased Tasks:**
| Phase | Task | Detail | Done When |
|-------|------|--------|-----------|
| 1 | Basic reverse proxy | Map URI prefixes -> internal services | All core paths reachable |
| 1 | API key middleware | Header check + 401 | Negative tests pass |
| 1 | Structured logging | JSON logs with trace id | Sample log shows trace_id |
| 2 | Rate limiting | In-memory token bucket per key/IP | 429s appear under load test |
| 2 | Metrics | Prometheus / OTEL counters & latency hist | Metrics endpoint returns data |
| 3 | Response caching (etag) | Cache GET /games/{id} for short TTL | Cache HIT metric >0 |
| 4 | Circuit breakers | Trip on downstream p95 latency or 5xx | Breaker metrics present |

**Acceptance (Phase 1):** p95 added latency <10ms local, error handling returns JSON envelope.

**Metrics:** request_count, error_ratio, upstream_latency, rate_limit_drops.

**Risks:** Over-complexity early (Mitigate: keep minimal until traffic justifies).  
**Backlog:** JWT/OAuth integration, GraphQL façade, global caching tier.

### 4.2 User Service (Java / Spring)
**Scope:** Basic user registration/login (local), store minimal profile, issue JWT (short-lived) – placeholder for external identity later.

**Interfaces:** `/users/register`, `/users/login`, `/users/{id}` (GET basic info).  
**Data Model (Minimal):** `user(id UUID, email, password_hash, created_at, updated_at)` (PII limited).  
**Security:** Bcrypt salts; no plaintext logs.

**Phased Tasks:**
| Phase | Task | Detail | Exit |
|-------|------|--------|------|
| 1 | Domain & entity | JPA entity + repository | Migration & CRUD pass |
| 1 | Registration | Validate email + hash pass | Returns 201 with id |
| 1 | Login & JWT | Issue signed token (HS256) | Login test passes |
| 2 | Public profile fields | avatar_url (optional), display_name | Fields persisted |
| 3 | Consent flags | steam_data_consent boolean | Flag gate enforced |
| 4 | Audit logging | User change events | Event count recorded |

**Acceptance (Phase 1):** Register/login flows tested; password never returned.

**Metrics:** user_signup_total, login_failures, jwt_issued.

**Risks:** Password leak risk (Mitigate: central secret mgmt & no debug logging).  
**Backlog:** Social graph, session revocation, OAuth providers.

### 4.3 Game Service (Java / Spring)
**Scope:** Read-facing API for catalog + enrichment fields; later achievements & metrics endpoint.

**Initial Endpoints:**
- `GET /games/{id}`
- `GET /games/slug/{slug}`
- `GET /games?genre=...&platform=...&released_after=...`
- `GET /games/{id}/achievements` (Phase 3)
- `GET /games/{id}/metrics` (Phase 2)

**Phased Tasks:**
| Phase | Task | Detail | Exit |
|-------|------|--------|------|
| 1 | DTO + mappers | Map DB rows to API model | Unit tests green |
| 1 | Basic filtering | Genre/platform join queries | Query plan index usage |
| 1 | Pagination & sorting | Limit/offset, sort by release/popularity | Returns consistent ordering |
| 2 | Enrichment fields | Expose steam_* + popularity_composite | Fields present & doc updated |
| 2 | Metrics endpoint | Derived popularity breakdown | Returns composite & components |
| 3 | Achievements endpoint | Paginated achievements list | p95 < 200ms local |
| 3 | Caching layer | ETag or 304 support | Conditional requests pass |
| 4 | Aggregations | Achievement density, release timeline stats | Metrics validated |

**Indices Planned Additions:** `(slug UNIQUE)` already; potential `(genre_id, game_id)` via join, partial index popular games.

**Acceptance (Phase 1):** p95 < 120ms local for base queries (10k games synthetic).  
**Metrics:** games_query_count, db_time_ms, cache_hit_ratio (later).  
**Risks:** N+1 queries on dimension joins (Mitigate: batch fetch / explicit projections).  
**Backlog:** Full text search (trigram), faceted counts.

### 4.4 Recommendation Service (Python)
**Scope:** Provide recommendation endpoints using content-based + popularity hybrid early; integrate achievements later.

**Endpoints:** `GET /recommendations?game_id=...`, `GET /recommendations/user/{id}`.

**Feature Inputs (Phase 1):** genre vector, platform vector.  
**Phase 2:** add popularity_composite weighting.  
**Phase 3:** add achievement density & category diversity.

**Phased Tasks:**
| Phase | Task | Detail | Exit |
|-------|------|--------|------|
| 1 | Data access layer | Read minimal game slices (id, genre ids) | Returns list in tests |
| 1 | Similarity (cosine) | Precompute genre vectors | Latency < 150ms per 10k |
| 1 | User cold start fallback | Top popular by genre preference (if any) | Response code path covered |
| 2 | Hybrid scoring | 0.6 content + 0.4 popularity_composite | Score unit test validates formula |
| 3 | Achievement features | Add normalized achievement density | Re-rank integrated |
| 3 | Caching precomputed embeddings | In-memory TTL | Cache hit ratio >50% |
| 4 | Batch offline refresh | Periodic recomputation job | Scheduled success metrics |

**Metrics:** reco_latency_ms, cache_hits, model_version, feature_missing_ratio.

**Risks:** Cold start for completely new games (Mitigate: popularity fallback only).  
**Backlog:** Collaborative filtering, embedding learning pipeline, A/B experimentation harness.

### 4.5 IGDB Game ETL (Go Worker)
**Scope:** Incremental ingestion of canonical catalog & dimensions.

**Flow:** watermark -> fetch changed ids -> batch fetch -> transform -> upsert -> link tables -> events.

**Phased Tasks:**
| Phase | Task | Detail | Exit |
|-------|------|--------|------|
| 1 | `ingestion_watermark` table | `(entity, source, last_value)` | Row exists (game, IGDB) |
| 1 | Fetch updated ids | Query updated_at > watermark | Returns list non-empty |
| 1 | Dimension upserts | Platforms, genres, etc. | Idempotent re-run |
| 1 | Game upsert & link tables | UPSERT by source_ref | Duplicate safe |
| 1 | Watermark advance (transaction) | Row lock & update | No race conditions |
| 2 | DLQ integration | On persistent failure push row | Retries bounded |
| 2 | DQ issue emission | Unknown enum, missing FK -> dq_issue | Issues visible |
| 3 | Achievements ingestion | Populate `game_achievement` | Coverage metric > target |
| 4 | Performance batching | COPY / prepared statements | Throughput target met |

**Acceptance (Phase 1):** Freshness p95 < 30m, re-run idempotent; no orphan FKs.

**Metrics:** ingestion_lag_sec, rows_upserted_total, retry_count, dlq_size.

**Risks:** Rate limit; inconsistent dimension order (Mitigate: prefetch dimensions & retry).  
**Backlog:** Random drift revalidation sampler, partial soft-delete handling.

### 4.6 Steam Enrichment ETL (Go Worker)
**Scope:** Link games to Steam AppIDs, fetch playtime aggregates, compute popularity composite, achievements presence flags.

**Phased Tasks:**
| Phase | Task | Detail | Exit |
|-------|------|--------|------|
| 2 | External link discovery | Use IGDB external_games & fuzzy fallback | Link coverage metric baseline |
| 2 | `steam_playtime_30d` table | Store aggregated recent minutes | Table populated |
| 2 | Playtime aggregation job | Mock / sample data first | recent_minutes_30d updated |
| 2 | Popularity composite script integration | Reuse `scripts/popularity_composite.sql` | popularity_composite not null for enriched |
| 2 | Achievements presence flags | Global achievement % (if available) | steam_supports_achievements set |
| 2 | Watermark for app catalog scan | daily last_scan_at entry | Row present |
| 3 | Achievement metadata ingestion (optional) | IGDB or Steam global links | Additional metrics visible |
| 3 | Incremental recompute optimization | Only changed games | Recompute runtime reduced |

**Acceptance (Phase 2):** Popularity composite filled for >80% active games with playtime; link coverage >60% eligible.

**Metrics:** steam_link_coverage, composite_freshness_sec, ambiguous_link_rate, playtime_rows_refreshed.

**Risks:** Ambiguous fuzzy matches (Mitigate: candidate threshold + dq_issue).  
**Backlog:** Multi-source (GOG/Epic) popularity weighting, anomaly detection.

---
## 5. Cross-Cutting Quality & Observability
| Dimension | Implementation | Minimum Target |
|----------|----------------|----------------|
| Tracing | OTEL spans: fetch, transform, upsert | Spans for 95% ETL runtime |
| Logging | Structured JSON, correlation-id header | No plain stack traces without context |
| Metrics | Counter + histogram for critical paths | Dashboards show ingestion lag & API latency |
| Alerts | Lag, error rate, recompute freshness | 3 initial alert rules active |
| DQ | dq_issue table + weekly report | Unknown enum ratio <1% |

## 6. Testing Strategy
| Layer | Approach | Tools |
|-------|---------|-------|
| Unit | Pure functions (transformations, scoring) | Go test, JUnit, pytest |
| Integration | DB migrations + sample ingestion | docker-compose ephemeral |
| Contract (future) | Consumer-driven for gateway->services | Spring Cloud Contract (placeholder) |
| Performance | Basic load (read endpoints) | k6 / JMeter (later) |
| Data Quality | SQL assertions (FK, not null, counts) | psql scripts |

Definition of Done for a task includes: code + tests + docs updated + metrics instrumented (if applicable).

## 7. Risk Register (Focused)
| Risk | Phase Exposure | Impact | Mitigation |
|------|---------------|--------|-----------|
| IGDB rate limiting | 1+ | Freshness degradation | Adaptive backoff, watermark persistence |
| Fuzzy link false positive | 2 | Incorrect popularity mapping | Score threshold + manual dq_issue review |
| Query performance degradation (joins) | 2–3 | API latency | Add composite indices, analyze plans |
| Achievement volume | 3 | Storage & slow queries | Pre-plan partitioning, selective columns |
| Single maintainer load | All | Schedule slip | Strict scope discipline, backlog deferral |

## 8. Success Metrics (Aggregated)
| Metric | Target | Phase Due |
|--------|--------|-----------|
| Catalog Freshness p95 | < 20–30m | 1 |
| Steam Link Coverage | > 60% (initial) | 2 |
| Popularity Composite Coverage | > 80% active games | 2 |
| Achievement Coverage (supported games) | > 70% | 3 |
| API p95 (core game lookup) | < 120ms local baseline | 1 |
| Reco Latency p95 | < 250ms | 1 |
| Ingestion DLQ Rate | < 2% of rows | 2 |
| Unknown Enum Ratio | < 1% | 2 |

## 9. Backlog (Deferred / Not in MVP)
- Full-text fuzzy search service
- Collaborative filtering matrix factorization job
- Feature store persisted (Parquet + embeddings)
- Console data ingestion (await partnerships)
- External reviews ingestion & sentiment
- Price/deal ingestion integration
- A/B experimentation harness
- WebSockets for live presence / real-time updates

## 10. Change Management
1. Propose change (issue + lightweight ADR if architectural)  
2. Update this plan version + revision history  
3. Implement code + migrations + tests  
4. Update docs index + CHANGELOG  

## 11. Immediate Next Steps (Actionable)
Status legend: [ ] pending, [WIP] in progress, [x] done.  
2. [ ] Scaffold IGDB ETL worker (skeleton + config)  
3. [ ] Implement Game Service base endpoints + pagination  
4. [ ] Gateway reverse proxy + API key middleware  
5. [x] Add initial dashboards (placeholders) (otel collector config present; panels TBD)  

## 12. Appendix A – Draft DDL Snippets (Not Yet Applied)
```sql
CREATE TABLE ingestion_watermark (
  entity VARCHAR(32) NOT NULL,
  source VARCHAR(32) NOT NULL,
  last_value BIGINT NOT NULL,
  updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
  PRIMARY KEY(entity, source)
);

CREATE TABLE ingestion_dlq (
  dlq_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  topic VARCHAR(64) NOT NULL,
  payload JSONB NOT NULL,
  error_code VARCHAR(64) NOT NULL,
  attempts INT NOT NULL DEFAULT 0,
  first_seen_at TIMESTAMP NOT NULL DEFAULT NOW(),
  last_attempt_at TIMESTAMP NULL
);

CREATE TABLE dq_issue (
  issue_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  code VARCHAR(32) NOT NULL,
  entity_type VARCHAR(32) NOT NULL,
  entity_id UUID NULL,
  severity VARCHAR(8) NOT NULL,
  status VARCHAR(16) NOT NULL DEFAULT 'OPEN',
  created_at TIMESTAMP NOT NULL DEFAULT NOW(),
  resolved_at TIMESTAMP NULL,
  details JSONB NULL
);

CREATE TABLE steam_playtime_30d (
  game_id UUID PRIMARY KEY REFERENCES game(game_id) ON DELETE CASCADE,
  recent_minutes_30d BIGINT NOT NULL,
  updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE steam_link_candidate (
  steam_link_candidate_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  game_id UUID NOT NULL REFERENCES game(game_id) ON DELETE CASCADE,
  attempt_count INT NOT NULL DEFAULT 0,
  reason VARCHAR(32) NOT NULL,
  last_attempt_at TIMESTAMP NOT NULL DEFAULT NOW(),
  UNIQUE(game_id, reason)
);

CREATE TABLE popularity_recompute_audit (
  batch_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  started_at TIMESTAMP NOT NULL DEFAULT NOW(),
  finished_at TIMESTAMP NULL,
  row_count INT NULL,
  success BOOLEAN NULL
);
```

## 13. Revision History
| Version | Date | Changes |
|---------|------|---------|
| 0.1.1 | 2025-08-15 | Added Solo Execution Addendum, Makefile integration, updated foundations with migration 0004 |
| 0.1.0 | 2025-08-15 | Initial draft (all services & ETLs) |

---

