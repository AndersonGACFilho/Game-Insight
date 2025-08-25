# Games Data ETL Service

Status: Alpha (internal evolution)  
Owner: Game Insight Platform Team  
Primary Source: IGDB (canonical)  
Secondary Sources (planned): Steam (enrichment)

## 1. Purpose
Ingest, transform, enrich, and persist video game catalog data from external provider APIs into the internal domain model while maintaining referential integrity, observability, and extensibility.

## 2. High-Level Flow
Extract (paged IGDB API) -> Transform (normalize + base entity) -> Enrich (modular steps add dimensions & associations) -> Load (graph upsert) -> Metrics (Prometheus).

```
┌─────────┐   ┌──────────┐   ┌────────────┐   ┌─────────┐   ┌────────────┐
│ IGDB API│--▶│ Extractor│--▶│ Transformer│--▶│ Enricher│--▶│ Loader (DB) │
└─────────┘   └──────────┘   └────────────┘   └─────────┘   └────────────┘
                                                 │                │
                                                 └─► Metrics ◄────┘
```

## 3. Code Structure
```
internal/
  api/igdb        → HTTP client & query helpers
  db              → Connection + config helpers
  domain/         → Entities (persistent) + IGDB DTO models + enums
  etl/            → Pipeline, extractor, transformer, enrichment steps, loader, metrics, helpers
  platform/logger → Logging (zerolog wrapper)
  platform/repositories → Game + Dimension repositories (GORM)
```

## 4. Key Interfaces
- Extractor: Extract(offset, limit) → raw source models
- Transformer: Transform(raw) → *entities.Game (base fields only)
- Enricher: Enrich(game, raw) → mutates aggregate (composition of steps)
- Loader: Save(*Game) → graph upsert
- Metrics: Prometheus counters + histogram per step

## 5. Enrichment Architecture (DRY + Open/Closed)
Old monolithic enrichment was replaced by `CompositeEnricher`, which orchestrates focused `GameEnrichment` steps:
- named_dimensions
- platforms
- companies
- alt_names
- release_dates
- media
- multiplayer_modes
- language_supports
- age_ratings
- achievements
- websites
- videos

Shared caches + helper generics live in `helpers.go`. Adding a new enrichment requires:
1. Create a struct implementing `GameEnrichment`
2. Append it in `NewCompositeEnricher`
3. Use shared helpers for fetch/ensure patterns.

## 6. SOLID Mapping
- Single Responsibility: Each enrichment step has one concern; Transformer avoids enrichment logic; Loader only persists.
- Open/Closed: New enrichment steps are additive without modifying existing ones (composition list only).
- Liskov: Interfaces are minimal; substituting any Extractor / Loader honours expected contracts.
- Interface Segregation: Narrow contracts (Extractor, Transformer, Loader) instead of giant service interface.
- Dependency Inversion: Pipeline depends on abstractions (Extractor, Transformer, Enricher, Loader) rather than concrete implementations.

## 7. DRY Improvements
| Concern | Before | After |
|--------|--------|-------|
| Named dimension fetch/upsert | Repeated code blocks | Generic `fetchEnsure` + `diffMissing` helpers |
| Media URL normalization | Duplicated logic | Central helper `normalizeIGDBImageURL` |
| Pointer constructor boilerplate | Duplicated per file | Shared helpers in `helpers.go` |
| Enrichment caching | Scattered maps | Unified `EnrichmentContext` |

Legacy `DimensionEnricher` replaced by alias wrapper (backward compatibility). Use `NewCompositeEnricher` going forward.

## 8. Metrics (Prometheus)
| Metric | Type | Description |
|--------|------|-------------|
| etl_games_extracted_total | counter | Raw games pulled from source |
| etl_games_processed_total | counter | Successfully processed (saved) games |
| etl_games_deferred_total | counter | Child games deferred (parent missing) |
| etl_transform_errors_total | counter | Transformation failures |
| etl_enrich_errors_total | counter | Non-fatal enrichment issues |
| etl_load_errors_total | counter | Persistence errors |
| etl_step_duration_seconds{step} | histogram | Duration per ETL step (extract|transform|enrich|load) |

Scrape endpoint: `:METRICS_PORT/metrics` (default 9108).

## 9. Environment Variables
| Variable | Default | Description |
|----------|---------|-------------|
| IGDB_CLIENT_ID | dqo79q2m2xekhsb38anwwa9fodctel | IGDB OAuth client id (override in prod) |
| IGDB_SECRET | bfuou8knx9hzqrx57x8kyr832ryz62 | IGDB OAuth secret (override) |
| BATCH_LIMIT | 500 | Page size per extract loop |
| METRICS_PORT | 9108 | Metrics server port |
| DATABASE_URL | (constructed) | Postgres DSN (via LoadConfig) |

## 10. Running Locally
Prereqs: Go >= 1.22/1.23, Postgres running (see root docker-compose for DB service).

Build:
```
go build ./...
```
Run:
```
IGDB_CLIENT_ID=xxx IGDB_SECRET=yyy go run ./main.go
```
Visit metrics: http://localhost:9108/metrics

## 11. Persistence Notes
`GameRepository.UpsertGraph` handles cascading inserts/updates for related slices. Dimension repositories ensure idempotent upserts for named dimensions, companies, platforms, etc.

## 12. Error & Retry Strategy
- Extract errors: abort current run (fail fast) to avoid partial state ambiguity.
- Enrichment errors: logged & counted but non-fatal (game still loads with partial associations).
- Deferred children: retried up to 2 passes per batch; remaining rely on future parent ingestion.

## 13. Adding a New Source (Example: Steam)
1. Implement `Extractor` for Steam (or secondary enrichment fetcher inside a new enrichment step).
2. Introduce new enrichment step (e.g. `steamEnrichment`) pulling external refs & metrics.
3. Update repositories & migrations for new columns.
4. Append step in `NewCompositeEnricher`.
5. Add metrics counters if needed.

## 14. Testing Strategy (Planned)
- Unit: mock `Extractor`, `Loader` to validate pipeline control flow + deferral logic.
- Enrichment: table-driven tests per step (input raw struct -> expected entity mutation).
- Integration: spin ephemeral Postgres; assert upsert graph correctness.

## 15. Extensibility Guidelines
- Keep new step side effects isolated; prefer pure data augmentation.
- Reuse generic helpers; avoid in-step custom duplicate fetch patterns.
- When caching new entity types, add map in `EnrichmentContext` rather than local ephemeral map for cross‑step reuse (if shared).

## 16. Security Considerations
- Never commit real secrets; environment defaults here are placeholders only.
- Avoid logging full raw JSON payloads containing potentially sensitive future enrichment data.

## 17. Known Gaps
| Gap | Impact | Planned Action |
|-----|--------|----------------|
| No paging watermark (updated_at) | Reprocesses all pages each run | Introduce updated_at > last watermark extractor variant |
| Limited unit tests | Risk of regressions | Add tests after interface stabilization |
| Parent game resolution limited to existence check | Might skip children longer | Add queue-based retry across runs |

## 18. Quick Reference
| Task | Command |
|------|---------|
| Build | `go build ./...` |
| Lint (suggestion) | `golangci-lint run` (not yet configured) |
| Run service | `go run ./main.go` |
| Metrics | curl :9108/metrics |

## 19. Changelog (Service-Level)
- 2025-08-19: Refactored enrichment to modular steps (CompositeEnricher), centralized helpers, added documentation.

---
Contributions welcome: follow root CONTRIBUTING.md; propose enrichment additions via PR with focused tests.

