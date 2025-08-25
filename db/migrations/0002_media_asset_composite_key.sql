-- Migration: adjust media_asset uniqueness to allow same IGDB image id across different asset types
-- Rationale: IGDB image IDs (source_ref) can appear in multiple categories (e.g., COVER and ARTWORK)
-- Previous schema enforced UNIQUE(source_ref) causing conflicts when inserting both in one batch.
-- Change: drop old constraint and add composite UNIQUE(source_ref, type).

BEGIN;
ALTER TABLE public.media_asset DROP CONSTRAINT IF EXISTS media_asset_source_ref_key;
ALTER TABLE public.media_asset ADD CONSTRAINT media_asset_source_ref_type_key UNIQUE (source_ref, type);
COMMIT;

