BEGIN;

-- Enumerations generic lookup (optional, can seed later)
CREATE TABLE IF NOT EXISTS enumeration_lookup (
                                                  enum_name        VARCHAR(64) NOT NULL,
                                                  code             INTEGER     NOT NULL,
                                                  symbol           VARCHAR(64) NOT NULL,
                                                  description      TEXT        NULL,
                                                  deprecated       BOOLEAN     NOT NULL DEFAULT FALSE,
                                                  PRIMARY KEY(enum_name, code)
);

-- Core dimension tables
CREATE TABLE IF NOT EXISTS platform (
                                        platform_id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                        source_ref             BIGINT NOT NULL UNIQUE,
                                        name                   TEXT   NOT NULL,
                                        abbreviation           TEXT   NULL,
                                        generation             SMALLINT NULL,
                                        category_code          SMALLINT NULL,
                                        created_at_source      TIMESTAMP NULL,
                                        updated_at_source      TIMESTAMP NULL,
                                        created_at             TIMESTAMP NOT NULL DEFAULT NOW(),
                                        updated_at             TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS idx_platform_source_ref ON platform(source_ref);

CREATE TABLE IF NOT EXISTS genre (
                                     genre_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                     source_ref BIGINT NOT NULL UNIQUE,
                                     name TEXT NOT NULL,
                                     slug TEXT NOT NULL,
                                     created_at_source TIMESTAMP NULL,
                                     updated_at_source TIMESTAMP NULL,
                                     created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                                     updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS theme (
                                     theme_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                     source_ref BIGINT NOT NULL UNIQUE,
                                     name TEXT NOT NULL,
                                     slug TEXT NOT NULL,
                                     created_at_source TIMESTAMP NULL,
                                     updated_at_source TIMESTAMP NULL,
                                     created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                                     updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS game_mode (
                                         game_mode_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                         source_ref BIGINT NOT NULL UNIQUE,
                                         name TEXT NOT NULL,
                                         slug TEXT NOT NULL,
                                         created_at_source TIMESTAMP NULL,
                                         updated_at_source TIMESTAMP NULL,
                                         created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                                         updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS player_perspective (
                                                  player_perspective_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                                  source_ref BIGINT NOT NULL UNIQUE,
                                                  name TEXT NOT NULL,
                                                  slug TEXT NOT NULL,
                                                  created_at_source TIMESTAMP NULL,
                                                  updated_at_source TIMESTAMP NULL,
                                                  created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                                                  updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS keyword (
                                       keyword_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                       source_ref BIGINT NOT NULL UNIQUE,
                                       name TEXT NOT NULL,
                                       slug TEXT NOT NULL,
                                       created_at_source TIMESTAMP NULL,
                                       updated_at_source TIMESTAMP NULL,
                                       created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                                       updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS collection (
                                          collection_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                          source_ref BIGINT NOT NULL UNIQUE,
                                          name TEXT NOT NULL,
                                          slug TEXT NOT NULL,
                                          created_at_source TIMESTAMP NULL,
                                          updated_at_source TIMESTAMP NULL,
                                          created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                                          updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS franchise (
                                         franchise_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                         source_ref BIGINT NOT NULL UNIQUE,
                                         name TEXT NOT NULL,
                                         slug TEXT NOT NULL,
                                         created_at_source TIMESTAMP NULL,
                                         updated_at_source TIMESTAMP NULL,
                                         created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                                         updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS company (
                                       company_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                       source_ref BIGINT NOT NULL UNIQUE,
                                       name TEXT NOT NULL,
                                       country SMALLINT NULL,
                                       description TEXT NULL,
                                       logo_asset_id UUID NULL,
                                       created_at_source TIMESTAMP NULL,
                                       updated_at_source TIMESTAMP NULL,
                                       created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                                       updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS game (
                                    game_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                    source_ref BIGINT NOT NULL UNIQUE,
                                    slug TEXT NOT NULL UNIQUE,
                                    title TEXT NOT NULL,
                                    summary TEXT NULL,
                                    storyline TEXT NULL,
                                    category_code SMALLINT NULL,
                                    status_code SMALLINT NULL,
                                    first_release_date DATE NULL,
                                    total_rating NUMERIC(5,2) NULL,
                                    total_rating_count INT NULL,
                                    aggregated_rating NUMERIC(5,2) NULL,
                                    aggregated_rating_count INT NULL,
                                    popularity NUMERIC(9,2) NULL,
                                    collection_id UUID NULL REFERENCES collection(collection_id) ON DELETE SET NULL,
                                    parent_game_id UUID NULL REFERENCES game(game_id) ON DELETE SET NULL,
                                    created_at_source TIMESTAMP NULL,
                                    updated_at_source TIMESTAMP NULL,
                                    ingestion_timestamp TIMESTAMP NOT NULL DEFAULT NOW(),
                                    active BOOLEAN NOT NULL DEFAULT TRUE,
    -- Steam enrichment
                                    steam_app_id INT NULL,
                                    steam_supports_achievements BOOLEAN NULL,
                                    steam_has_stats BOOLEAN NULL,
                                    steam_last_enriched_at TIMESTAMP NULL,
                                    popularity_composite NUMERIC(5,4) NULL,
                                    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS idx_game_source_ref ON game(source_ref);
CREATE INDEX IF NOT EXISTS idx_game_first_release_date ON game(first_release_date);
CREATE INDEX IF NOT EXISTS idx_game_popularity ON game(popularity DESC);
CREATE INDEX IF NOT EXISTS idx_game_popularity_composite ON game(popularity_composite DESC);

-- Link tables for many-to-many relationships
CREATE TABLE IF NOT EXISTS game_genre (
                                          game_id UUID NOT NULL REFERENCES game(game_id) ON DELETE CASCADE,
                                          genre_id UUID NOT NULL REFERENCES genre(genre_id) ON DELETE CASCADE,
                                          PRIMARY KEY(game_id, genre_id)
);
CREATE TABLE IF NOT EXISTS game_theme (
                                          game_id UUID NOT NULL REFERENCES game(game_id) ON DELETE CASCADE,
                                          theme_id UUID NOT NULL REFERENCES theme(theme_id) ON DELETE CASCADE,
                                          PRIMARY KEY(game_id, theme_id)
);
CREATE TABLE IF NOT EXISTS game_keyword (
                                            game_id UUID NOT NULL REFERENCES game(game_id) ON DELETE CASCADE,
                                            keyword_id UUID NOT NULL REFERENCES keyword(keyword_id) ON DELETE CASCADE,
                                            PRIMARY KEY(game_id, keyword_id)
);
CREATE TABLE IF NOT EXISTS game_mode_link (
                                              game_id UUID NOT NULL REFERENCES game(game_id) ON DELETE CASCADE,
                                              game_mode_id UUID NOT NULL REFERENCES game_mode(game_mode_id) ON DELETE CASCADE,
                                              PRIMARY KEY(game_id, game_mode_id)
);
CREATE TABLE IF NOT EXISTS game_player_perspective (
                                                       game_id UUID NOT NULL REFERENCES game(game_id) ON DELETE CASCADE,
                                                       player_perspective_id UUID NOT NULL REFERENCES player_perspective(player_perspective_id) ON DELETE CASCADE,
                                                       PRIMARY KEY(game_id, player_perspective_id)
);
CREATE TABLE IF NOT EXISTS game_franchise (
                                              game_id UUID NOT NULL REFERENCES game(game_id) ON DELETE CASCADE,
                                              franchise_id UUID NOT NULL REFERENCES franchise(franchise_id) ON DELETE CASCADE,
                                              PRIMARY KEY(game_id, franchise_id)
);

CREATE TABLE IF NOT EXISTS involved_company (
                                                involved_company_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                                source_ref BIGINT NOT NULL UNIQUE,
                                                game_id UUID NOT NULL REFERENCES game(game_id) ON DELETE CASCADE,
                                                company_id UUID NOT NULL REFERENCES company(company_id) ON DELETE CASCADE,
                                                developer BOOLEAN NOT NULL DEFAULT FALSE,
                                                publisher BOOLEAN NOT NULL DEFAULT FALSE,
                                                porting BOOLEAN NOT NULL DEFAULT FALSE,
                                                supporting BOOLEAN NOT NULL DEFAULT FALSE,
                                                created_at_source TIMESTAMP NULL,
                                                updated_at_source TIMESTAMP NULL,
                                                created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                                                updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS idx_involved_company_game ON involved_company(game_id);
CREATE INDEX IF NOT EXISTS idx_involved_company_company ON involved_company(company_id);

CREATE TABLE IF NOT EXISTS release_date (
                                            release_date_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                            source_ref BIGINT NOT NULL UNIQUE,
                                            game_id UUID NOT NULL REFERENCES game(game_id) ON DELETE CASCADE,
                                            platform_id UUID NOT NULL REFERENCES platform(platform_id) ON DELETE CASCADE,
                                            date DATE NULL,
                                            region_code SMALLINT NULL,
                                            category_code SMALLINT NULL,
                                            status_code SMALLINT NULL,
                                            created_at_source TIMESTAMP NULL,
                                            updated_at_source TIMESTAMP NULL,
                                            created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                                            updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
                                            UNIQUE(game_id, platform_id, date, region_code)
);
CREATE INDEX IF NOT EXISTS idx_release_date_game ON release_date(game_id);

CREATE TABLE IF NOT EXISTS age_rating (
                                          age_rating_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                          source_ref BIGINT NOT NULL UNIQUE,
                                          organization_code SMALLINT NULL,
                                          rating_code SMALLINT NULL,
                                          synopsis TEXT NULL,
                                          created_at_source TIMESTAMP NULL,
                                          updated_at_source TIMESTAMP NULL,
                                          created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                                          updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Media assets (cover, screenshot, artwork, etc.)
CREATE TABLE IF NOT EXISTS media_asset (
                                           asset_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                           source_ref BIGINT NOT NULL UNIQUE,
                                           game_id UUID NULL REFERENCES game(game_id) ON DELETE CASCADE,
                                           type TEXT NOT NULL,
                                           width INT NULL,
                                           height INT NULL,
                                           url_original TEXT NOT NULL,
                                           checksum TEXT NULL,
                                           created_at_source TIMESTAMP NULL,
                                           updated_at_source TIMESTAMP NULL,
                                           created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                                           updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS idx_media_asset_game ON media_asset(game_id);
CREATE INDEX IF NOT EXISTS idx_media_asset_type ON media_asset(type);

-- External references (Steam, PSN, XBL, etc.)
CREATE TABLE IF NOT EXISTS external_reference (
                                                  external_reference_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                                  game_id UUID NOT NULL REFERENCES game(game_id) ON DELETE CASCADE,
                                                  provider VARCHAR(16) NOT NULL,
                                                  category VARCHAR(32) NOT NULL,
                                                  external_id VARCHAR(64) NOT NULL,
                                                  url TEXT NULL,
                                                  created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                                                  UNIQUE(provider, category, external_id),
                                                  UNIQUE(game_id, provider, category)
);
CREATE INDEX IF NOT EXISTS idx_external_reference_game ON external_reference(game_id);
-- Achievement table
CREATE TABLE IF NOT EXISTS game_achievement (
                                                game_achievement_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                                source_ref BIGINT NOT NULL UNIQUE,
                                                game_id UUID NOT NULL REFERENCES game(game_id) ON DELETE CASCADE,
                                                name TEXT NOT NULL,
                                                slug TEXT NOT NULL,
                                                description TEXT NULL,
                                                category_code SMALLINT NULL,
                                                order_index INT NULL,
                                                points INT NULL DEFAULT 0,
                                                secret BOOLEAN NOT NULL DEFAULT FALSE,
                                                unlocked_icon_asset_id UUID NULL REFERENCES media_asset(asset_id) ON DELETE SET NULL,
                                                locked_icon_asset_id UUID NULL REFERENCES media_asset(asset_id) ON DELETE SET NULL,
                                                created_at_source TIMESTAMP NULL,
                                                updated_at_source TIMESTAMP NULL,
                                                checksum TEXT NULL,
                                                created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                                                updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS idx_game_achievement_game ON game_achievement(game_id);
CREATE INDEX IF NOT EXISTS idx_game_achievement_order ON game_achievement(game_id, order_index);

-- Alternative Names
CREATE TABLE IF NOT EXISTS game_alt_name (
                                             game_alt_name_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                             source_ref BIGINT NOT NULL UNIQUE,
                                             game_id UUID NOT NULL REFERENCES game(game_id) ON DELETE CASCADE,
                                             name TEXT NOT NULL,
                                             comment TEXT NULL,
                                             locale TEXT NULL,
                                             created_at_source TIMESTAMP NULL,
                                             updated_at_source TIMESTAMP NULL,
                                             created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                                             updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS idx_game_alt_name_game ON game_alt_name(game_id);

-- Game Websites
CREATE TABLE IF NOT EXISTS game_website (
                                            game_website_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                            source_ref BIGINT NOT NULL UNIQUE,
                                            game_id UUID NOT NULL REFERENCES game(game_id) ON DELETE CASCADE,
                                            category_code INT NULL,
                                            url TEXT NOT NULL,
                                            trusted BOOLEAN NULL,
                                            checksum TEXT NULL,
                                            created_at_source TIMESTAMP NULL,
                                            updated_at_source TIMESTAMP NULL,
                                            created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                                            updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS idx_game_website_game ON game_website(game_id);

-- Game Videos
CREATE TABLE IF NOT EXISTS game_video (
                                          game_video_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                          source_ref BIGINT NOT NULL UNIQUE,
                                          game_id UUID NOT NULL REFERENCES game(game_id) ON DELETE CASCADE,
                                          name TEXT NOT NULL,
                                          video_id TEXT NOT NULL,
                                          platform TEXT NULL,
                                          checksum TEXT NULL,
                                          created_at_source TIMESTAMP NULL,
                                          updated_at_source TIMESTAMP NULL,
                                          created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                                          updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS idx_game_video_game ON game_video(game_id);

-- Multiplayer Modes
CREATE TABLE IF NOT EXISTS multiplayer_mode (
                                                multiplayer_mode_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                                source_ref BIGINT NOT NULL UNIQUE,
                                                game_id UUID NOT NULL REFERENCES game(game_id) ON DELETE CASCADE,
                                                campaign_coop BOOLEAN NULL,
                                                drop_in BOOLEAN NULL,
                                                lan_coop BOOLEAN NULL,
                                                offline_coop BOOLEAN NULL,
                                                offline_coop_max SMALLINT NULL,
                                                offline_max SMALLINT NULL,
                                                online_coop BOOLEAN NULL,
                                                online_coop_max SMALLINT NULL,
                                                online_max SMALLINT NULL,
                                                splitscreen BOOLEAN NULL,
                                                splitscreen_online BOOLEAN NULL,
                                                created_at_source TIMESTAMP NULL,
                                                updated_at_source TIMESTAMP NULL,
                                                created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                                                updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS idx_multiplayer_mode_game ON multiplayer_mode(game_id);

-- Language Support (link table per language/type)
CREATE TABLE IF NOT EXISTS game_language_support (
                                                     game_language_support_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                                     source_ref BIGINT NOT NULL UNIQUE,
                                                     game_id UUID NOT NULL REFERENCES game(game_id) ON DELETE CASCADE,
                                                     language_code INT NOT NULL,
                                                     support_type_code INT NOT NULL,
                                                     created_at_source TIMESTAMP NULL,
                                                     updated_at_source TIMESTAMP NULL,
                                                     created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                                                     updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
                                                     UNIQUE(game_id, language_code, support_type_code)
);
CREATE INDEX IF NOT EXISTS idx_game_language_support_game ON game_language_support(game_id);

-- Basic trigger function (must exist before creating triggers)
CREATE OR REPLACE FUNCTION touch_updated_at() RETURNS trigger AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END; $$ LANGUAGE plpgsql;

-- Touch triggers (auto-update updated_at on content tables)
CREATE TRIGGER trg_game_achievement_touch BEFORE UPDATE ON game_achievement FOR EACH ROW EXECUTE FUNCTION touch_updated_at();
CREATE TRIGGER trg_game_alt_name_touch BEFORE UPDATE ON game_alt_name FOR EACH ROW EXECUTE FUNCTION touch_updated_at();
CREATE TRIGGER trg_game_website_touch BEFORE UPDATE ON game_website FOR EACH ROW EXECUTE FUNCTION touch_updated_at();
CREATE TRIGGER trg_game_video_touch BEFORE UPDATE ON game_video FOR EACH ROW EXECUTE FUNCTION touch_updated_at();
CREATE TRIGGER trg_multiplayer_mode_touch BEFORE UPDATE ON multiplayer_mode FOR EACH ROW EXECUTE FUNCTION touch_updated_at();
CREATE TRIGGER trg_game_language_support_touch BEFORE UPDATE ON game_language_support FOR EACH ROW EXECUTE FUNCTION touch_updated_at();

-- Entity triggers
CREATE TRIGGER trg_game_touch BEFORE UPDATE ON game FOR EACH ROW EXECUTE FUNCTION touch_updated_at();
CREATE TRIGGER trg_platform_touch BEFORE UPDATE ON platform FOR EACH ROW EXECUTE FUNCTION touch_updated_at();

COMMIT;