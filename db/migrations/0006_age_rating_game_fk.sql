-- Adds game_id FK to age_rating to satisfy GORM AgeRatings association
-- Safe additive migration (nullable first to avoid failures with existing rows)
ALTER TABLE age_rating
	ADD COLUMN IF NOT EXISTS game_id UUID NULL REFERENCES game(game_id) ON DELETE CASCADE;

CREATE INDEX IF NOT EXISTS idx_age_rating_game ON age_rating(game_id);
