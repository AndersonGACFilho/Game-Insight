-- popularity_composite.sql
-- Recompute composite popularity using IGDB popularity and Steam recent playtime (30d).
-- Assumptions:
--   - steam_playtime_30d(game_id UUID PRIMARY KEY, recent_minutes_30d BIGINT NOT NULL)
--   - Composite formula: 0.6 * norm_igdb_pop + 0.4 * steam_playtime_score
--   - steam_playtime_score = LOG10(1 + recent_minutes_30d) / LOG10(1 + max_recent_minutes)
-- Run inside a transaction; adjust weights if needed.

BEGIN;

WITH igdb_bounds AS (
    SELECT MIN(popularity) AS min_pop, MAX(popularity) AS max_pop
    FROM game WHERE popularity IS NOT NULL
), play_bounds AS (
    SELECT MAX(recent_minutes_30d) AS max_minutes FROM steam_playtime_30d
), scores AS (
    SELECT g.game_id,
           CASE WHEN g.popularity IS NULL OR ib.max_pop = ib.min_pop THEN NULL
                ELSE (g.popularity - ib.min_pop) / NULLIF(ib.max_pop - ib.min_pop,0) END AS norm_igdb_pop,
           CASE WHEN sp.recent_minutes_30d IS NULL THEN 0
                ELSE LOG(10, 1 + sp.recent_minutes_30d) / NULLIF(LOG(10, 1 + pb.max_minutes),0) END AS steam_playtime_score
    FROM game g
    CROSS JOIN igdb_bounds ib
    LEFT JOIN steam_playtime_30d sp ON sp.game_id = g.game_id
    CROSS JOIN play_bounds pb
), composite AS (
    SELECT game_id,
           ROUND(
             CASE
               WHEN norm_igdb_pop IS NULL THEN steam_playtime_score
               ELSE 0.6 * norm_igdb_pop + 0.4 * steam_playtime_score
             END
           ,4) AS popularity_composite
    FROM scores
)
UPDATE game g
SET popularity_composite = c.popularity_composite,
    steam_last_enriched_at = NOW()
FROM composite c
WHERE g.game_id = c.game_id;

COMMIT;

