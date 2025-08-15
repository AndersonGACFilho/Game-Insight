-- 0004_ingestion_foundations.sql
-- Purpose: Create ingestion foundational tables needed in Phase 1 (watermark) and optional role helpers.
-- Idempotent: Uses IF NOT EXISTS. Run inside transaction.
BEGIN;

-- Watermark table (generic for any entity/source incremental ingestion)
CREATE TABLE IF NOT EXISTS ingestion_watermark (
    entity      VARCHAR(32) NOT NULL,
    source      VARCHAR(32) NOT NULL,
    last_value  BIGINT      NOT NULL,
    updated_at  TIMESTAMP   NOT NULL DEFAULT NOW(),
    PRIMARY KEY(entity, source)
);

-- Helper function to upsert a watermark atomically
CREATE OR REPLACE FUNCTION upsert_ingestion_watermark(p_entity VARCHAR, p_source VARCHAR, p_last BIGINT)
RETURNS VOID AS $$
BEGIN
    INSERT INTO ingestion_watermark(entity, source, last_value)
    VALUES(p_entity, p_source, p_last)
    ON CONFLICT(entity, source)
    DO UPDATE SET last_value = EXCLUDED.last_value, updated_at = NOW();
END; $$ LANGUAGE plpgsql;

-- Optional “app” vs “migrator” roles (only create if not exist). Safe to ignore errors if insufficient privilege.
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'app_user') THEN
        CREATE ROLE app_user NOINHERIT LOGIN PASSWORD 'app_pass_placeholder'; -- Replace via secret management.
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'migrator') THEN
        CREATE ROLE migrator NOINHERIT LOGIN PASSWORD 'migrator_pass_placeholder';
    END IF;
EXCEPTION WHEN insufficient_privilege THEN
    RAISE NOTICE 'Skipping role creation due to insufficient privileges';
END; $$;

COMMIT;

