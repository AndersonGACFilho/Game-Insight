# Game Ingestion ETL Runbook

Title: Game Ingestion ETL Runbook (IGDB + Steam) <br>
Version: 0.1.0 <br>
Last Updated: 2025-08-15 <br>
Owner: Anderson (Sole Maintainer) <andersonfilho09@gmail.com> <br>
Status: Draft <br>
Decision: Provide operational guidance for unified game pipeline <br>

## 1. Purpose
Operational procedures (monitor, troubleshoot, rollback) for the unified IGDB + Steam Game ETL (see design: docs/domain/game-etl-igdb-steam.md).

## 2. Scope
Covers Phase 1 (IGDB canonical) + Steam enrichment (playtime, achievements presence, composite popularity). Excludes PSN/XBL (future integration).

## 3. Pipeline Components
| Component | Responsibility | Schedule |
|-----------|----------------|----------|
| igdb_game_incremental | Fetch & upsert changed IGDB games | 15m |
| steam_app_catalog_scan | Discover new Steam AppIDs | 24h |
| steam_enrichment_playtime | Aggregate recent playtime signals | 1h |
| steam_achievement_refresh | Refresh global achievement stats | 24h |
| popularity_recompute | Update popularity_composite | 1h |

## 4. Key Tables
| Table | Description |
|-------|-------------|
| game | Canonical game + enrichment columns (steam_*) |
| external_reference | Provider-specific IDs (STEAM) |
| game_genre / etc. | Many-to-many attribute links |
| game_achievement (Phase2) | Achievements metadata (if enabled) |
| enumeration_lookup | Enum code -> symbol reference |
| ingestion_watermark (planned) | Source watermark persistence |
| steam_link_candidate (planned) | Ambiguous / unresolved matches |

## 5. Monitoring & Metrics
| Metric | Threshold | Action |
|--------|-----------|--------|
| Game Incremental Lag p95 | > 20m | Check igdb API latency & queue backlog |
| Steam Link Coverage | < 85% | Inspect unresolved candidates |
| Ambiguous Link Rate | > 1% | Tune fuzzy threshold / normalization list |
| Popularity Freshness p95 | > 2h | Verify popularity_recompute job ran |
| Enrichment Failure Rate | > 2% | Review DLQ entries error codes |

## 6. Dashboards (Proposed Panels)
- Watermarks: IGDB max updated_at vs now
- Job Durations: per run histogram
- DQ Issues: counts by code (ST-LINK-MISSING, ST-LINK-AMBIGUOUS)
- Popularity Distribution: composite vs raw IGDB vs steam_playtime_score

## 7. Alerting Rules (Examples)
| Alert | Condition | Severity | Runbook Step |
|-------|-----------|----------|--------------|
| igdb_incremental_stall | No success in 45m | HIGH | Step 8.1 |
| steam_enrichment_fail_spike | Failure rate >5% 3 runs | MED | Step 8.2 |
| popularity_recompute_stale | Freshness >3h | MED | Step 8.3 |

## 8. Troubleshooting
### 8.1 IGDB Incremental Stall
1. Check network errors vs API quota (429?).
2. Inspect DLQ for IGDB-FK-MISS spikes.
3. If watermark stuck: SELECT * FROM ingestion_watermark WHERE entity='game'.
4. Manually re-run job with override param last_updated_at=prev-5m.
5. If still failing, disable incremental & trigger full backfill (after hours only).

### 8.2 Steam Enrichment Failure Spike
1. Sample DLQ rows (limit 20) -> categorize (network / schema / link).
2. If link issues >60%, run ad-hoc fuzzy recalculation for last 2h games.
3. Network: escalate if consistent 5xx over 15m.
4. Patch fix & redeploy enrichment worker, replay DLQ (bounded batch 500). 

### 8.3 Popularity Stale
1. Confirm popularity_recompute last run timestamp.
2. Manually trigger recompute function (see Section 12).
3. Validate composite sample: SELECT game_id, popularity, popularity_composite FROM game ORDER BY updated_at DESC LIMIT 10.

### 8.4 High Ambiguous Links
1. Query candidates: SELECT * FROM steam_link_candidate WHERE reason='AMBIGUOUS' AND attempt_count<5.
2. Adjust fuzzy threshold from 0.92 -> 0.94 temporarily.
3. Re-run link resolution for affected subset.
4. Monitor impact for 24h; revert threshold if coverage drops >2%.

## 9. Data Quality Recovery
| Scenario | Action |
|----------|--------|
| Wrong Steam App linked | Delete external_reference row; enqueue link recompute |
| Missing genres on bulk | Re-fetch game ids via IGDB filter (fields genres != null) |
| Rating precision drift | Re-run normalization script (TBD) |

## 10. Backfill Procedure (High-Level)
1. Pause incremental (feature flag). 
2. Run partitioned fetch (by id ranges) storing to staging tables.
3. Validate counts vs IGDB baseline.
4. Swap staging -> prod (MERGE by source_ref) in batches (5k).
5. Resume incremental; set watermark = max(updated_at) from prod.

## 11. Rollback Strategy
| Failure Type | Rollback |
|--------------|----------|
| DDL migration | Use transaction rollback; if applied, deploy inverse migration (DROP new columns) |
| Corrupt popularity_composite | UPDATE game SET popularity_composite=NULL WHERE recompute_batch_id=? |
| External link mis-association | Remove external_reference + clear steam_* columns |

## 12. Manual Commands
### 12.1 Recompute Popularity for Subset
```
UPDATE game g SET popularity_composite = pc.new_val, steam_last_enriched_at = NOW()
FROM (
  SELECT game_id,
         ROUND(
           CASE WHEN popularity IS NULL THEN steam_score
                ELSE 0.6 * norm_pop + 0.4 * steam_score END
         ,4) AS new_val
  FROM temp_popularity_stage
) pc
WHERE g.game_id = pc.game_id;
```

### 12.2 Inspect Enrichment Drift
```
SELECT game_id, popularity, popularity_composite, steam_last_enriched_at
FROM game
WHERE steam_last_enriched_at < NOW() - INTERVAL '3 hours'
ORDER BY steam_last_enriched_at ASC LIMIT 50;
```

## 13. Logs & Tracing
| Component | Log Key Fields | Trace Attributes |
|-----------|----------------|------------------|
| igdb-fetch | source_ref batch_size duration_ms | entity=game provider=IGDB |
| steam-link | game_id app_id match_score | provider=STEAM action=link |
| enrichment | game_id steam_app_id changed_fields | enrichment=steam |

## 14. Capacity Considerations
- Achievements volume (Phase 2) can add >2.5GB raw; enable compression (TOAST / pglz) & partition by hash(game_id) if > 10M rows.
- Index bloat watch: idx_game_popularity_composite reindex if bloat >30%.

## 15. Security
- Never log API keys.
- Redact user-specific identifiers in enrichment debug logs (hash steamid).

## 16. SLA & RTO/RPO
| Aspect | Value |
|--------|-------|
| Freshness (IGDB p95) | < 20m |
| Freshness (Steam composite p95) | < 2h |
| RPO (data loss tolerance) | 15m |
| RTO (pipeline restore) | 1h |

## 17. Open Improvements
- Automated fuzzy threshold tuning.
- Real-time WebSocket ingestion for Steam presence (future).
- Backfill orchestration via Temporal + saga rollback semantics.

## Revision History
- v0.1.0 (2025-08-15): Initial runbook.

