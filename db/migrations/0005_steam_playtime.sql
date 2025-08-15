-- 0005_steam_playtime.sql
-- Purpose: Create steam_playtime_30d aggregation table + helper upsert function & trigger.
-- Phase: 2 (enrichment) - added early to unblock composite script.
-- Idempotent constructs (IF NOT EXISTS) to allow re-run.

BEGIN;

CREATE TABLE IF NOT EXISTS steam_playtime_30d (
    game_id UUID PRIMARY KEY REFERENCES game(game_id) ON DELETE CASCADE,
    recent_minutes_30d BIGINT NOT NULL CHECK (recent_minutes_30d >= 0),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Simple trigger to auto-touch updated_at on updates
CREATE OR REPLACE FUNCTION touch_updated_at_steam_playtime() RETURNS trigger AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END; $$ LANGUAGE plpgsql;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_trigger WHERE tgname = 'trg_steam_playtime_touch'
    ) THEN
        CREATE TRIGGER trg_steam_playtime_touch BEFORE UPDATE ON steam_playtime_30d
            FOR EACH ROW EXECUTE FUNCTION touch_updated_at_steam_playtime();
    END IF;
END; $$;

-- Helper function for atomic upsert
CREATE OR REPLACE FUNCTION upsert_steam_playtime_30d(p_game_id UUID, p_minutes BIGINT)
RETURNS VOID AS $$
BEGIN
    INSERT INTO steam_playtime_30d(game_id, recent_minutes_30d)
    VALUES(p_game_id, p_minutes)
    ON CONFLICT (game_id)
    DO UPDATE SET recent_minutes_30d = EXCLUDED.recent_minutes_30d, updated_at = NOW();
END; $$ LANGUAGE plpgsql;

COMMIT;

