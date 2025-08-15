# Game Ingestion ETL (IGDB + Steam)

Title: Unified Game Extraction & Enrichment ETL (IGDB + Steam) <br>
Version: 0.1.0 <br>
Last Updated: 2025-08-15 <br>
Owner: Anderson (Sole Maintainer) <andersonfilho09@gmail.com> <br>
Status: Draft <br>
Decision: Establish unified pipeline using IGDB as canonical and Steam as enrichment <br>

> Compliance Scope (EN): This ETL design operates solely on IGDB (official API, licensed) as canonical catalog plus Steam Web API public endpoints for enrichment in a non‑commercial / portfolio context. No PSN/XBL data is ingested (design-only documents exist but are feature-flagged & inactive). No scraping of protected pages, session hijacking, or credential harvesting is performed. Any future production/commercial expansion (additional platforms, store pricing, console telemetry) requires formal partner agreements and renewed legal/privacy review.

## 1. Purpose
Provide an end-to-end design for extracting, reconciling, transforming and loading Game entities using IGDB as the canonical catalog and Steam as a secondary enrichment source (playtime, ownership signals, popularity, achievements coverage heuristics).

## 2. Scope
In-Scope:
- Incremental pull of IGDB games (updated_at watermark)
- On-demand / scheduled discovery of new Steam AppIDs and enrichment of existing games
- Cross-source identity reconciliation (IGDB <-> Steam)
- ExternalReference persistence (STEAM AppID linkage)
- Attribute enrichment: popularity composite, presence of Steam stats & achievements flags
- Data quality validation & metrics

Out of Scope (future docs): PSN/XBL merges, pricing, store regional availability.

## 3. Canonical Model
Canonical Game stored per igdb-source-mapping.md (Section 6.1). Steam adds enrichment columns (proposed extension):
- steam_app_id (INT, nullable)
- steam_supports_achievements (BOOLEAN)
- steam_has_stats (BOOLEAN)
- steam_release_date (DATE, nullable) (best-effort from Steam store API if later added)
- steam_review_sample_positive (SMALLINT, nullable) (future)
- steam_review_sample_total (INT, nullable) (future)
- steam_last_enriched_at (TIMESTAMP)

## 4. Source Hierarchy & Precedence
| Attribute | Canonical Source | Fallback / Merge Logic |
|----------|------------------|------------------------|
| Title / Name | IGDB.name | If null, Steam name; else ignore Steam rename unless IGDB deprecated |
| Genres / Tags | IGDB (genres/keywords) | Append Steam tags only if not mapped to IGDB keywords (Phase 2) |
| First Release Date | IGDB.first_release_date | If missing & Steam has release date -> use Steam date flagged source=STEAM |
| Achievements Count | Derived IGDB achievements if ingested | If not present & Steam indicates achievements -> mark has_achievements=true |
| Popularity | IGDB.popularity | Composite: max(IGDB.popularity_norm, steam_playtime_score) |

## 5. Data Flow Overview
1. Watermark Load (IGDB last_updated_at, Steam last_app_scan_at)
2. IGDB Incremental Fetch (changed games ids)
3. IGDB Dimension Resolution (genres, modes, etc.)
4. Upsert Game (canonical fields)
5. Discover Missing Steam Links:
   - Query external_games IGDB endpoint for STEAM categories
   - If absent, run name + release year fuzzy match against Steam app metadata cache
6. Steam Enrichment Batch:
   - Fetch Owned Games (for active users) -> accumulate playtime per app -> popularity signals
   - Fetch Global Achievement Percentages (for new app_ids) -> achievements presence flag
7. Compute Derived Metrics & Composite Popularity
8. Upsert ExternalReference(provider=STEAM)
9. Patch Game with enrichment columns
10. Emit GameEnriched event
11. Update Watermarks

## 6. Incremental Strategies
| Source | Delta Criterion | Mechanism |
|--------|----------------|-----------|
| IGDB | max(updated_at) | Store watermark; query updated_at > last |
| Steam App Catalog | Periodic diff (daily) | Full list (cached) diff vs known app_ids |
| Steam Playtime | Rolling (hourly) | Recompute only for apps with new playtime deltas |
| Steam Achievements Global | On first link + weekly refresh | TTL 7d |

## 7. Identity Resolution Logic
1. Direct IGDB external_games mapping (category=STEAM) -> authoritative
2. If absent, candidate resolution:
   - Exact case-insensitive title equality
   - Normalized title (remove punctuation, edition suffixes) equality
   - Fuzzy ratio >= 0.92 (Levenshtein / token sort) AND same release year (+/- 1 year window)
3. If multiple candidates tie, defer and flag DQ issue (ST-LINK-AMBIGUOUS)
4. Persist unresolved in table steam_link_candidate(game_id, attempt_count, last_attempt_at, reason)

## 8. Popularity Composite (v1)
Compute steam_playtime_score = log10(1 + total_recent_play_minutes_30d) normalized 0..1 over rolling 30d window.
Composite popularity_internal = 0.6 * normalize(igdb.popularity) + 0.4 * steam_playtime_score.
If IGDB popularity missing -> weight 1.0 on steam_playtime_score.
Store in separate column popularity_composite (DECIMAL(5,4)).

## 9. Transformations
| Step | Transformation | Validation |
|------|---------------|------------|
| T1 | Epoch -> UTC timestamps | Reject future > now+1d |
| T2 | Slug sanitize | Regenerate if invalid |
| T3 | Steam title normalization | Lowercase, trim, remove edition tokens (GOTY, Definitive) |
| T4 | Fuzzy match scoring | threshold=0.92, algorithm token_sort_ratio |
| T5 | Popularity scaling | Min-max over last 30d snapshot; outliers (p99) cap |

## 10. Storage Extensions (Proposed DDL Snippets)
(Not yet applied migrations.)
```
ALTER TABLE game ADD COLUMN steam_app_id INT NULL;
ALTER TABLE game ADD COLUMN steam_supports_achievements BOOLEAN NULL;
ALTER TABLE game ADD COLUMN steam_has_stats BOOLEAN NULL;
ALTER TABLE game ADD COLUMN steam_last_enriched_at TIMESTAMP NULL;
ALTER TABLE game ADD COLUMN popularity_composite DECIMAL(5,4) NULL;
CREATE TABLE external_reference (
  external_reference_id UUID PRIMARY KEY,
  game_id UUID NOT NULL,
  provider VARCHAR(16) NOT NULL,
  category VARCHAR(32) NOT NULL,
  external_id VARCHAR(64) NOT NULL,
  url TEXT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT NOW(),
  UNIQUE(provider, category, external_id)
);
CREATE INDEX idx_external_reference_game ON external_reference(game_id);
```
(Adjust if already exists; see future migration file.)

## 11. Orchestration & Scheduling
| Job | Frequency | Dependency | SLA |
|-----|-----------|------------|-----|
| igdb_game_incremental | 15m | watermark ready | 5m completion |
| steam_app_catalog_scan | 24h | none | 30m |
| steam_enrichment_playtime | 1h | ownership snapshot | 10m |
| steam_achievement_refresh | 24h | link established | 20m |
| popularity_recompute | 1h | steam_enrichment_playtime | 5m |

Coordinator: Airflow / Temporal (TBD) — each task idempotent & fenced by watermark row lock.

## 12. Error Handling
| Failure | Action |
|---------|--------|
| HTTP 429 | Exponential backoff + jitter; push task to tail if >3 retries |
| IGDB partial page fail | Retry page; if still fail mark page checkpoint and continue remaining IDs (skip) |
| Steam link ambiguous | Insert DQ issue; exclude enrichment until resolved |
| DB deadlock | Retry transaction up to 3 times |
| Missing dimension FK | Queue retry entity (max 3) else mark unresolved + metric |

Dead-letter: ingestion_dlq(topic=game_etl) storing payload + error_code + attempt_count.

## 13. Metrics
| Metric | Target | Notes |
|--------|--------|------|
| Game Incremental Lag p95 | < 20m | IGDB update -> internal persistence |
| Steam Link Coverage | > 85% of games with known Steam counterpart (eligible) |
| Ambiguous Link Rate | < 1% of link attempts |
| Popularity Composite Freshness p95 | < 2h | Time since recompute |
| Enrichment Failure Rate | < 2% per batch |

## 14. Data Quality Issue Codes
| Code | Description | Severity |
|------|------------|----------|
| ST-LINK-MISSING | No Steam candidate found | WARN |
| ST-LINK-AMBIGUOUS | Multiple Steam candidates | WARN |
| ST-ACH-MISSING | Achievements indicated but no global stats | INFO |
| IGDB-FK-MISS | Missing dimension FK after retries | ERROR |
| POP-CALC-OUTLIER | Popularity outlier capped | INFO |

## 15. Retry & Backoff Policy
Exponential: base 2s, factor 2, max 64s, max_attempts=5 (network / 5xx). Deterministic jitter: random(0, base*factor).

## 16. Idempotency & Concurrency
- Upsert keyed by (game.source_ref, provider=IGDB)
- Enrichment updates limited to columns steam_* and popularity_composite under separate UPDATE with optimistic check (updated_at_source unchanged) to reduce write conflicts.
- Distributed lock: advisory lock key hash('IGDB_GAME_INCREMENTAL') for watermark advance.

## 17. Event Emission
Emit after successful upsert/enrichment:
- GameUpdated (fields_changed[]) for canonical changes
- GameEnriched (steam_fields_changed[]) when enrichment columns change
Avoid duplicate emission via checksum of last emitted field set per game (cache TTL 1h).

## 18. Security & Compliance
- API keys stored in secrets manager; never logged.
- PII from Steam (real_name) excluded or hashed only upon consent.
- Rate limit tokens centrally pooled to avoid burst bans.

## 19. Operational Runbook (Summary)
Symptoms / Steps:
- Lag Spike: check watermark table; compare last_processed vs now; inspect ingestion_dlq.
- High Ambiguous Links: review fuzzy thresholds; tune token normalization list.
- Popularity Drift: verify playtime aggregation job; recompute backfill 24h.

## 20. Open Questions
| ID | Question | Status |
|----|----------|--------|
| Q1 | Introduce a separate steam_popularity_raw table? | Pending volume assessment |
| Q2 | Persist multi-language Steam names? | Defer |
| Q3 | Add fallback to Steam store API for release date? | Evaluate reliability |

## 21. Future Extensions
- Integrate PSN / XBL presence counts into popularity composite (tri-source scoring)
- Add anomaly detection on sudden playtime spikes
- Implement incremental fuzzy cache to prevent repeated ambiguous evaluations.

## 22. Revision History
- v0.1.0 (2025-08-15): Initial ETL design document.
