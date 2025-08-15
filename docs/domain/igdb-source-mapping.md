# IGDB Source Mapping

Title: IGDB -> Game Service Data Mapping <br>
Version: 0.3.1 <br>
Last Updated: 2025-08-15 <br>
Owner: Anderson (Sole Maintainer) <andersonfilho09@gmail.com> <br>
Status: Draft <br>
Decision: Added Achievements & fully extended entities (standardized formatting) <br>

> Compliance Notice (EN): IGDB data usage in this project is limited to fields retrieved via the official IGDB API under Twitch/IGDB terms for non-production / portfolio demonstration. No HTML scraping, credential circumvention, or redistribution of raw proprietary datasets is performed. Console platform user data (PSN / XBL) and any non‑authorized store data remain out of scope; only IGDB + Steam (public Web API) mappings are presently active. Any future production/commercial use would require a renewed license review, data minimization assessment, and explicit partner agreements for additional platforms.

## 1. Purpose
Establish a clear, versioned mapping between IGDB API entities/fields and the internal Game Service domain model. This guides ingestion, transformation, validation, and future evolution of the game catalog.

## 2. Scope
In-scope IGDB endpoints/entities (current needs + requested full coverage list):
- games (incl. categories: dlcs, expansions, ports, remakes, remasters, bundles)
- achievements
- platforms
- genres
- themes
- game_modes
- player_perspectives
- keywords
- collections
- franchises
- companies / involved_companies
- release_dates
- age_ratings / age_rating_content_descriptions
- covers / screenshots / artworks / logos
- alternative_names
- websites (game/company/platform)
- external_reviews
- game_videos
- game_engines / engine logos
- language_supports / language_support_types
- multiplayer_modes
- external_games (external references)
- platform_families / versions / version_companies / version_release_dates
- game_versions / game_version_features / game_version_feature_values

Out of scope (for now): characters (no product use yet), search (utility only).

## 3. Source References
- IGDB API Docs: https://api-docs.igdb.com/#endpoints
- Data usage licensed under Twitch/IGDB terms. Field names reproduced as factual references.

## 4. High-Level Internal Entities
| Internal Entity | Description | Primary Identifier | Lifecycle Source | Refresh Strategy |
|-----------------|-------------|--------------------|------------------|------------------|
| Game            | Core playable title | game_id (UUID) | IGDB.games       | Incremental by updated_at & backfill |
| Platform        | Hardware/OS platform | platform_id (UUID) | IGDB.platforms | Slowly changing (track version) |
| Genre           | Thematic classification | genre_id (UUID) | IGDB.genres | Rare changes |
| Theme           | Narrative/setting theme | theme_id (UUID) | IGDB.themes | Rare changes |
| GameMode        | Mode of play | mode_id (UUID) | IGDB.game_modes | Rare changes |
| Perspective     | Player viewpoint | perspective_id (UUID) | IGDB.player_perspectives | Rare changes |
| Keyword         | Free-form tag | keyword_id (UUID) | IGDB.keywords | Moderate |
| Collection      | Group of related games (series) | collection_id (UUID) | IGDB.collections | Moderate |
| Franchise       | Franchise brand umbrella | franchise_id (UUID) | IGDB.franchises | Rare |
| Company         | Company (dev/publisher/support) | company_id (UUID) | IGDB.companies | Moderate |
| InvolvedCompany | Role link between game & company | involved_company_id (UUID) | IGDB.involved_companies | Frequent |
| ReleaseDate     | Platform-specific release | release_date_id (UUID) | IGDB.release_dates | Frequent |
| AgeRating       | Age rating classification | age_rating_id (UUID) | IGDB.age_ratings | Rare |
| MediaAsset      | Visual assets (cover, screenshot, artwork) | asset_id (UUID) | IGDB.covers / screenshots / artworks | Frequent additions |

## 5. Identifier Strategy
| Aspect | Approach |
|--------|----------|
| Internal Keys | Use UUID v7 (time-ordered) generated at ingestion time. |
| Foreign Source Key | Store original IGDB numeric id as source_ref (unique per entity type). |
| Upserts | Match by source_ref + entity_type. |
| Deletion Handling | IGDB rarely hard-deletes; if missing in delta >30d, mark as deprecated. |

## 6. Field-Level Mapping (Core)
### 6.1 Game
| Internal Field | Type | Required | IGDB Field(s) | Transformation / Notes |
|----------------|------|----------|---------------|------------------------|
| game_id | UUID | Yes | (generated) | Deterministic creation if not exists. |
| source_ref | BIGINT | Yes | id | Direct copy. Unique per source entity. |
| slug | TEXT | Yes | slug | Lowercase slug. Validate charset. |
| title | TEXT | Yes | name | Trim; fallback to slug if null. |
| summary | TEXT | No | summary | Strip HTML; length limit 4000 chars. |
| storyline | TEXT | No | storyline | Strip HTML. |
| category | SMALLINT | No | category | Map to internal enum GameCategory. |
| status | SMALLINT | No | status | Map to internal enum ReleaseStatus. |
| first_release_date | DATE | No | first_release_date | Convert epoch seconds -> UTC date. |
| total_rating | DECIMAL(5,2) | No | total_rating | Round to 2 decimals. |
| total_rating_count | INT | No | total_rating_count | >=0. |
| aggregated_rating | DECIMAL(5,2) | No | aggregated_rating | Round to 2 decimals. |
| aggregated_rating_count | INT | No | aggregated_rating_count | >=0. |
| popularity | DECIMAL(9,2) | No | popularity | Preserve scale 2. |
| keywords | ARRAY<UUID> | No | keywords | Resolve via keyword ids -> keyword_id. |
| genres | ARRAY<UUID> | No | genres | Resolve. |
| themes | ARRAY<UUID> | No | themes | Resolve. |
| game_modes | ARRAY<UUID> | No | game_modes | Resolve. |
| player_perspectives | ARRAY<UUID> | No | player_perspectives | Resolve. |
| collection_id | UUID | No | collection | Lookup collection. |
| franchise_ids | ARRAY<UUID> | No | franchises | Resolve list. |
| parent_game_id | UUID | No | parent_game | Link if exists. |
| dlc_ids | ARRAY<UUID> | No | (derived) | All Games where parent_game == this AND category in (DLC_ADDON) |
| expansion_ids | ARRAY<UUID> | No | (derived) | Games where parent_game == this AND category in (EXPANSION, STANDALONE_EXPANSION, EXPANDED_GAME) |
| remake_ids | ARRAY<UUID> | No | (derived) | Games category=REMAKE with parent_game=this |
| remaster_ids | ARRAY<UUID> | No | (derived) | Games category=REMASTER with parent_game=this |
| port_ids | ARRAY<UUID> | No | (derived) | Games category=PORT with parent_game=this |
| created_at_source | TIMESTAMP | Yes | created_at | Convert epoch -> UTC. |
| updated_at_source | TIMESTAMP | Yes | updated_at | Convert epoch -> UTC. |
| ingestion_timestamp | TIMESTAMP | Yes | (system) | Now() at ingestion. |
| active | BOOLEAN | Yes | (derived) | False if deprecated. |

### 6.2 Platform
| Internal Field | IGDB Field | Notes |
|----------------|-----------|-------|
| platform_id | (generated) | UUID v7 |
| source_ref | id | BIGINT |
| name | name | Trim |
| abbreviation | abbreviation | Nullable |
| generation | generation | SMALLINT |
| category | category | Map to internal enum PlatformCategory |
| created_at_source | created_at | Epoch -> timestamp |
| updated_at_source | updated_at | Epoch -> timestamp |

### 6.3 Genre / Theme / GameMode / PlayerPerspective / Keyword
These share a lightweight pattern.
| Field | Notes |
|-------|-------|
| <entity>_id | UUID v7 |
| source_ref | IGDB id |
| name | name (trim) |
| slug | slug (validate) |
| created_at_source | created_at |
| updated_at_source | updated_at |

### 6.4 Company & InvolvedCompany
| Internal Field | IGDB Field | Notes |
|----------------|-----------|-------|
| company_id | (generated) | |
| source_ref | id | |
| name | name | |
| country | country | Map numeric -> ISO code (lookup table). |
| description | description | Strip HTML. |
| logo_asset_id | logo | Later enhancement (MediaAsset). |
| created_at_source | created_at | |
| updated_at_source | updated_at | |

InvolvedCompany (link):
| Field | IGDB Field | Notes |
|-------|-----------|-------|
| involved_company_id | (generated) | |
| source_ref | id | |
| game_id | game | Resolve game first |
| company_id | company | Resolve company |
| developer | developer | Boolean |
| publisher | publisher | Boolean |
| porting | porting | Boolean |
| supporting | supporting | Boolean |
| created_at_source | created_at | |
| updated_at_source | updated_at | |

### 6.5 ReleaseDate
| Internal Field | IGDB Field | Notes |
|----------------|-----------|-------|
| release_date_id | (generated) | |
| source_ref | id | |
| game_id | game | FK |
| platform_id | platform | FK |
| date | date | Epoch -> DATE (if only year precision, set mid-year heuristics flagged) |
| region | region | Map to internal Region enum |
| category | category | Internal ReleaseDateCategory |
| status | status | Align with ReleaseStatus enum |
| created_at_source | created_at | |
| updated_at_source | updated_at | |

### 6.6 AgeRating
| Internal Field | IGDB Field | Notes |
|----------------|-----------|-------|
| age_rating_id | (generated) | |
| source_ref | id | |
| organization | category | Map to AgeRatingOrg enum |
| rating_code | rating | Map to AgeRatingCode enum |
| synopsis | synopsis | Truncate 1000 chars. |
| content_descriptors | content_descriptions | Resolve descriptor codes (future) |

### 6.7 MediaAsset (Cover / Screenshot / Artwork)
| Internal Field | IGDB Field | Notes |
|----------------|-----------|-------|
| asset_id | (generated) | |
| source_ref | id | |
| game_id | game | Nullable for some artwork? Usually present |
| type | (inferred) | cover|screenshot|artwork |
| width | width | |
| height | height | |
| url_original | url | Use https, store canonical. |
| checksum | checksum | Integrity check; detect changes. |
| created_at_source | created_at | |
| updated_at_source | updated_at | |

## 7. Enumerations Mapping
Only enumerations are actually used now. Codes derive from IGDB docs (numeric) mapped to internal symbolic names.

### 7.1 GameCategory (IGDB games.category)
| IGDB Code | Internal Symbol | Meaning |
|-----------|-----------------|---------|
| 0 | MAIN_GAME | Main Game |
| 1 | DLC_ADDON | DLC/Add-on |
| 2 | EXPANSION | Expansion |
| 3 | BUNDLE | Bundle |
| 4 | STANDALONE_EXPANSION | Standalone Expansion |
| 5 | MOD | Mod |
| 8 | REMAKE | Remake |
| 9 | REMASTER | Remaster |
| 10 | EXPANDED_GAME | Expanded Game |
| 11 | PORT | Port |
| 12 | FORK | Fork |
| 13 | PACK | Pack |
| 14 | UPDATE | Update |
| 15 | EPISODE | Episode |

### 7.2 ReleaseStatus (games.status)
| IGDB Code | Internal Symbol | Meaning |
|-----------|-----------------|---------|
| 0 | RELEASED | Released |
| 2 | ALPHA | Alpha |
| 3 | BETA | Beta |
| 4 | EARLY_ACCESS | Early Access |
| 5 | OFFLINE | Offline/Shutdown |
| 6 | CANCELLED | Cancelled |
| 7 | RUMORED | Rumored |
| 8 | DELISTED | Delisted |

### 7.3 PlatformCategory (platforms.category)
| IGDB Code | Internal Symbol | Meaning |
|-----------|-----------------|---------|
| 1 | CONSOLE | Console |
| 2 | ARCADE | Arcade |
| 3 | PLATFORM | Platform (Generic) |
| 4 | OPERATING_SYSTEM | Operating System |
| 5 | PORTABLE_CONSOLE | Portable Console |
| 6 | COMPUTER | Computer |
| 7 | DEVICE | Device |
| 8 | VIRTUAL_REALITY | VR |
| 9 | EMULATOR | Emulator |
| 10 | BROWSER | Browser |
| 11 | INTERNET_OF_THINGS | IoT |
| 12 | STREAMING | Streaming Platform |
| 13 | WALKTHROUGH | Delisted (deprecated) |

### 7.4 Region (release_dates.region)
| IGDB Code | Internal Symbol | Meaning |
|-----------|-----------------|---------|
| 1 | EUROPE | Europe |
| 2 | NORTH_AMERICA | North America |
| 3 | AUSTRALIA | Australia |
| 4 | NEW_ZEALAND | New Zealand |
| 5 | JAPAN | Japan |
| 6 | CHINA | China |
| 7 | ASIA | Rest of Asia |
| 8 | WORLDWIDE | Worldwide |
| 9 | KOREA | Korea |
| 10 | BRAZIL | Brazil |
| 11 | OTHER | Other |

### 7.5 AgeRatingOrg (age_ratings.category)
| IGDB Code | Internal Symbol | Organization |
|-----------|-----------------|-------------|
| 1 | ESRB | ESRB |
| 2 | PEGI | PEGI |
| 3 | CERO | CERO |
| 4 | USK | USK |
| 5 | GRAC | GRAC |
| 6 | CLASS_IND | ClassInd (Brazil) |
| 7 | ACB | Australian Classification Board |
| 8 | OFLC_NZ | OFLC New Zealand |
| 9 | RUSSIA | Russian (Age Rating) |
| 10 | MDA_SG | Singapore MDA |
| 11 | IARC | IARC |

(Additional rating codes mapping to AgeRatingCode will be defined when ingestion implemented.)

## 8. Transformation & Validation Rules
| Rule | Description | Failure Handling |
|------|-------------|------------------|
| HTML Sanitize | Remove tags from text fields | Log warn, continue |
| Length Limits | Enforce max lengths (e.g., summary 4000) | Truncate, add note flag |
| Slug Validation | Regex ^[a-z0-9\-]+$ | If invalid, regenerate from title |
| Enum Mapping | Unknown numeric -> UNKNOWN enum | Capture metric; review weekly |
| Epoch Conversion | Reject timestamps before 1970 or > now+1y | Set null + warn |
| Duplicate Source ID | If existing with different data | Perform diff; update changed fields |
| Referential Integrity | Missing referenced entity | Queue retry; if >3 attempts mark unresolved |
| Rating Precision | Round to 2 decimals | Deterministic rounding |

## 9. Ingestion Flow (Simplified)
1. Pull updated ids using IGDB search with updated_at > last_marker
2. Fetch full records in batches (respect rate limits)
3. Normalize & validate
4. Upsert dimension entities (Genre, Theme, etc.)
5. Upsert Company then InvolvedCompany links
6. Upsert Game (after dimensions resolved)
7. Upsert ReleaseDates, AgeRatings, MediaAssets
8. Emit domain events (GameUpdated, PlatformUpdated) for downstream caches
9. Persist new watermark (max updated_at processed)

## 10. Incremental Strategy
- Watermark Table: source_name, entity, last_updated_at
- Backfill: nightly job revalidates random 1% of catalog for drift detection
- Drift Metrics: mismatch rate per entity per week < 0.5% target

## 11. Data Quality Metrics
| Metric | Target | Notes |
|--------|--------|------|
| Missing Genre Ratio | < 5% | Games without any genre |
| Unknown Enum Ratio | < 1% | Per entity type |
| Orphan Link Ratio | 0% | Link referencing missing FK |
| Retry Resolution Rate | > 95% in 24h | For deferred references |

## 12. Security & Compliance
- Store only necessary IGDB fields; exclude personally identifying info (none expected).
- Respect rate limiting; credential stored in secrets manager (out of scope here).

## 13. Open Questions
| ID | Question | Status |
|----|----------|--------|
| Q1 | Include achievements in near term? | Pending product priority |
| Q2 | Enrich with external popularity (Steam)? | Future integration |
| Q3 | Need multi-language localization fields? | TBD (current = English only) |

## 14. Future Extensions
- Add Achievements ingestion pipeline.
- Localized text (name/summary) expansions.
- Media CDN caching & derivative sizes.
- Age rating content descriptors full taxonomy.

## 15. Revision History
- v0.3.1 (2025-08-15): Standardized arrow formatting (->), wording cleanup.
- v0.3.0 (2025-08-15): Added Achievements (GameAchievement) mapping, detailed relationship derived arrays (dlc/expansion/remake/remaster/port), extended scope to include all requested entities.
- v0.2.0 (2025-08-15): Added full IGDB entity coverage appendix (Section 16), extended mapping tables & enums.
- v0.1.0 (2025-08-15): Initial draft mapping.

## 16. Extended IGDB Entities Coverage & Mapping (Appendix)
This appendix enumerates ALL primary IGDB endpoints and defines ingestion strategy and (where applicable) internal mapping extensions.

### 16.1 Coverage Summary
| IGDB Endpoint | Purpose | Ingestion Strategy | Internal Entity / Notes |
|---------------|---------|--------------------|-------------------------|
| games | Core game objects | Core Incremental | Game (incl. category-derived relationships) |
| achievements | Per-game achievements | Planned (Phase 2) | GameAchievement + MediaAsset icons |
| age_ratings | Age classification | Core Incremental | AgeRating |
| age_rating_content_descriptions | Rating descriptors | Deferred (Phase 3) | AgeRatingDescriptor (new) |
| alternative_names | Alternate titles | Planned (Phase 2) | GameAltName |
| artworks | High-res artwork | Core | MediaAsset(type=ARTWORK) |
| bundles (via games.category) | Bundle grouping | Derived | Game (category=BUNDLE) |
| characters | Playable/featured characters | Deferred | (Not modeled) |
| collections | Series grouping | Core | Collection |
| companies | Company info | Core | Company |
| company_logos | Company logos | Deferred | MediaAsset(type=COMPANY_LOGO) |
| company_websites | Company websites | Deferred | CompanyWebsite |
| covers | Game cover images | Core | MediaAsset(type=COVER) |
| dlcs (games.category) | DLC entries | Derived | Game (category=DLC_ADDON) + parent link |
| external_games | External IDs | Planned (Phase 2) | ExternalReference |
| external_reviews | External review summaries | Planned (Phase 3) | ExternalReview |
| franchises | Franchise umbrella | Core | Franchise |
| game_engines | Engine info | Planned (Phase 2) | GameEngine |
| game_engine_logos | Engine logos | Deferred | MediaAsset(type=ENGINE_LOGO) |
| game_modes | Modes | Core | GameMode |
| game_versions | Version variants | Deferred | GameVersion |
| game_version_features | Feature taxonomy | Deferred | GameVersionFeature |
| game_version_feature_values | Feature value set | Deferred | GameVersionFeatureValue |
| game_videos | Official trailers | Planned (Phase 2) | GameVideo |
| genres | Genres | Core | Genre |
| involved_companies | Roles linking | Core | InvolvedCompany |
| keywords | Keywords tags | Core | Keyword |
| language_supports | Game language availability | Planned (Phase 2) | GameLanguageSupport (link) |
| language_support_types | Support type lookup | Planned (Phase 2) | LanguageSupportType (enum table) |
| multiplayer_modes | Multiplayer capabilities | Planned (Phase 2) | MultiplayerMode |
| platforms | Platforms | Core | Platform |
| platform_families | Families grouping | Deferred | PlatformFamily |
| platform_logos | Logos | Deferred | MediaAsset(type=PLATFORM_LOGO) |
| platform_versions | Versioned HW/SW revisions | Deferred | PlatformVersion |
| platform_version_companies | Support companies | Deferred | PlatformVersionCompany |
| platform_version_release_dates | Version release dates | Deferred | PlatformVersionReleaseDate |
| platform_websites | Platform websites | Deferred | PlatformWebsite |
| player_perspectives | Perspectives | Core | PlayerPerspective |
| release_dates | Release timeline | Core | ReleaseDate |
| remakes (games.category) | Remake entries | Derived | Game (category=REMAKE) |
| remasters (games.category) | Remaster entries | Derived | Game (category=REMASTER) |
| ports (games.category) | Port entries | Derived | Game (category=PORT) |
| screenshots | Gameplay screenshots | Core | MediaAsset(type=SCREENSHOT) |
| search | Search utility | N/A (API Helper) | — |
| themes | Narrative themes | Core | Theme |
| websites | Game websites | Planned (Phase 2) | GameWebsite |

Legend: Core = ingest now; Planned = near-term; Deferred = backlog; Derived = inferred via existing games fields.

### 16.2 New Internal Entities (Definitions)
Below are entities not detailed in earlier sections but now part of the extended model plan.

#### 16.2.1 GameAltName (AlternativeName)
| Field | Source | Notes |
|-------|--------|-------|
| alt_name_id | (generated) | UUID v7 |
| game_id | alternative_names.game | FK Game |
| source_ref | alternative_names.id | BIGINT |
| name | alternative_names.name | Trim |
| comment | alternative_names.comment | Nullable |
| locale | (derived) | Heuristic / future enrichment |
| created_at_source | alternative_names.created_at | |
| updated_at_source | alternative_names.updated_at | |

#### 16.2.2 GameWebsite (websites)
| Field | Source | Notes |
|-------|--------|-------|
| website_id | (generated) | |
| game_id | websites.game | FK |
| source_ref | websites.id | |
| category | websites.category | Map to WebsiteCategory enum |
| url | websites.url | Normalize https |
| trusted | (derived) | Domain whitelist flag |
| checksum | websites.checksum | Detect change |
| created_at_source | websites.created_at | |
| updated_at_source | websites.updated_at | |

#### 16.2.3 ExternalReview (external_reviews)
| Field | Source | Notes |
|-------|--------|-------|
| external_review_id | (generated) | |
| game_id | external_reviews.game | FK |
| source_ref | external_reviews.id | |
| source | external_reviews.source | Normalize to enum ReviewSource |
| url | external_reviews.url | |
| score | external_reviews.score | DECIMAL(5,2) |
| score_format | external_reviews.score_format | Enum (PERCENT, VALUE_10, VALUE_5, STAR) |
| locale | external_reviews.locale | Optional |
| published_at | external_reviews.published_at | Epoch->ts |
| created_at_source | external_reviews.created_at | |
| updated_at_source | external_reviews.updated_at | |

#### 16.2.4 GameVideo (game_videos)
| Field | Source | Notes |
|-------|--------|-------|
| game_video_id | (generated) | |
| game_id | game_videos.game | FK |
| source_ref | game_videos.id | |
| name | game_videos.name | Title |
| video_id | game_videos.video_id | Platform specific id (YouTube) |
| platform | (derived) | Parse (YouTube default) |
| checksum | game_videos.checksum | |
| created_at_source | game_videos.created_at | |
| updated_at_source | game_videos.updated_at | |

#### 16.2.5 GameEngine (game_engines)
| Field | Source | Notes |
|-------|--------|-------|
| game_engine_id | (generated) | |
| source_ref | game_engines.id | |
| name | game_engines.name | |
| description | game_engines.description | Sanitize |
| logo_asset_id | game_engines.logo | MediaAsset link deferred |
| created_at_source | game_engines.created_at | |
| updated_at_source | game_engines.updated_at | |

Link (Game -> Engine) is many-to-many through games.game_engines array (if enabled later) stored as game.engine_ids.

#### 16.2.6 GameLanguageSupport (language_supports)
| Field | Source | Notes |
|-------|--------|-------|
| game_language_support_id | (generated) | |
| source_ref | language_supports.id | |
| game_id | language_supports.game | FK |
| language | language_supports.language | Map numeric -> ISO 639-1/2 via lookup |
| type_id | language_supports.language_support_type | FK LanguageSupportType |
| created_at_source | language_supports.created_at | |
| updated_at_source | language_supports.updated_at | |

LanguageSupportType lookup: id, name (e.g., Audio, Subtitles, Interface).

#### 16.2.7 MultiplayerMode (multiplayer_modes)
| Field | Source | Notes |
|-------|--------|-------|
| multiplayer_mode_id | (generated) | |
| source_ref | multiplayer_modes.id | |
| game_id | multiplayer_modes.game | FK |
| campaign_coop | multiplayer_modes.campaigncoop | Boolean |
| drop_in | multiplayer_modes.dropin | Boolean |
| lan_coop | multiplayer_modes.lancoop | Boolean |
| offline_coop | multiplayer_modes.offlinecoop | Boolean |
| offline_coop_max | multiplayer_modes.offlinecoopmax | Smallint |
| offline_max | multiplayer_modes.offlinemax | Smallint |
| online_coop | multiplayer_modes.onlinecoop | Boolean |
| online_coop_max | multiplayer_modes.onlinecoopmax | Smallint |
| online_max | multiplayer_modes.onlinemax | Smallint |
| splitscreen | multiplayer_modes.splitscreen | Boolean |
| splitscreen_online | multiplayer_modes.splitscreenonline | Boolean |
| created_at_source | multiplayer_modes.created_at | |
| updated_at_source | multiplayer_modes.updated_at | |

#### 16.2.8 GameVersion / GameVersionFeature / GameVersionFeatureValue
(Deferred until a need arises for version-specific deltas.)
| Entity | Key Fields | Purpose |
|--------|------------|---------|
| GameVersion | id, game_id, source_ref, features[] | Alternate SKU/edition metadata |
| GameVersionFeature | id, category, position | Taxonomy grouping |
| GameVersionFeatureValue | id, feature_id, game_version_id | Value assignment |

#### 16.2.9 AgeRatingDescriptor (age_rating_content_descriptions)
| Field | Source | Notes |
|-------|--------|-------|
| descriptor_id | (generated) | |
| source_ref | age_rating_content_descriptions.id | |
| description | age_rating_content_descriptions.description | |
| category | (derived) | From parent rating org if needed |

Join table AgeRatingDescriptorLink(age_rating_id, descriptor_id).

#### 16.2.10 ExternalReference (external_games)
| Field | Source | Notes |
|-------|--------|-------|
| external_reference_id | (generated) | |
| game_id | external_games.game | FK |
| source_ref | external_games.id | |
| category | external_games.category | Map to ExternalRefCategory enum (e.g., STEAM, GOG) |
| uid | external_games.uid | Raw external id/string |
| url | external_games.url | Optional direct link |
| checksum | external_games.checksum | |
| created_at_source | external_games.created_at | |
| updated_at_source | external_games.updated_at | |

#### 16.2.11 CompanyWebsite / PlatformWebsite
Same structure reused for company_websites & platform_websites.
| Field | Source | Notes |
|-------|--------|-------|
| website_id | (generated) | |
| owner_type | (derived) | COMPANY or PLATFORM |
| owner_id | company_websites.company / platform_websites.platform | Polymorphic FK |
| source_ref | *.id | |
| category | *.category | Enum WebsiteCategory |
| url | *.url | Normalized |
| created_at_source | *.created_at | |
| updated_at_source | *.updated_at | |

#### 16.2.12 PlatformFamily / PlatformVersion / PlatformVersionCompany / PlatformVersionReleaseDate
Deferred structural platform granularity for later hardware analytics.
| Entity | Purpose | Key Fields |
|--------|---------|-----------|
| PlatformFamily | Group related platforms | id, name |
| PlatformVersion | Version detail | id, platform_id, name, source_ref |
| PlatformVersionCompany | Support link | id, platform_version_id, company_id, support_type |
| PlatformVersionReleaseDate | Version launch | id, platform_version_id, date, region |

#### 16.2.13 GameAchievement (achievements)
Provides per-game achievement metadata. (Field names validated against IGDB Achievements endpoint; verify final list during implementation.)
| Field | Source Field | Notes |
|-------|--------------|-------|
| game_achievement_id | (generated) | UUID v7 |
| source_ref | achievements.id | BIGINT |
| game_id | achievements.game | FK Game |
| name | achievements.name | Trim |
| slug | achievements.slug | Validate; regenerate if invalid |
| description | achievements.description | Sanitize HTML |
| category | achievements.category | Map -> AchievementCategory enum (placeholder) |
| order_index | achievements.order | Display ordering (if provided) |
| points | achievements.points | INT, default 0 |
| secret | achievements.hidden / secret | Boolean if field exists (fallback false) |
| unlocked_icon_asset_id | achievements.unlocked_icon | MediaAsset ref (type=ACHIEVEMENT_ICON_UNLOCKED) |
| locked_icon_asset_id | achievements.locked_icon | MediaAsset ref (type=ACHIEVEMENT_ICON_LOCKED) |
| created_at_source | achievements.created_at | Epoch -> ts |
| updated_at_source | achievements.updated_at | Epoch -> ts |
| checksum | achievements.checksum | Drift detection |

Achievement MediaAsset entries follow standard MediaAsset schema with type differentiating locked/unlocked icons.

#### 16.2.14 Achievement MediaAsset Types
Add two MediaAsset.type enumerations:
- ACHIEVEMENT_ICON_LOCKED
- ACHIEVEMENT_ICON_UNLOCKED

#### 16.2.15 Derived Relationship Refresh Logic
For arrays (dlc_ids, expansion_ids, remake_ids, remaster_ids, port_ids): recompute after each Game upsert impacting categories or parent_game linkage. Maintain auxiliary table GameRelationshipDelta(game_id, rel_type, changed_at) to trigger cache invalidations.

### 16.3 Additional Enumerations (Appendix)

#### 16.3.1 WebsiteCategory (websites.category)
Examples (subset; extend when ingesting):
| Code | Symbol | Description |
|------|--------|-------------|
| 1 | OFFICIAL | Official site |
| 2 | WIKIA | Wikia |
| 3 | WIKIPEDIA | Wikipedia |
| 4 | FACEBOOK | Facebook |
| 5 | TWITTER | Twitter/X |
| 6 | TWITCH | Twitch |
| 8 | INSTAGRAM | Instagram |
| 9 | YOUTUBE | YouTube |
| 10 | IPHONE | iOS Store |
| 11 | IPAD | iPad Store |
| 12 | ANDROID | Android Store |
| 13 | STEAM | Steam Store |
| 14 | REDDIT | Reddit |
| 15 | ITCH | itch.io |
| 16 | EPIC_GAMES | Epic Games Store |
| 17 | GOG | GOG Store |
| 18 | DISCORD | Discord |

#### 16.3.2 ExternalRefCategory (external_games.category)
| Code | Symbol | Example |
|------|--------|---------|
| 1 | STEAM | AppID |
| 5 | GOG | GOG ID |
| 10 | EPIC | Epic Namespace/App |
| 13 | DISCORD | Discord SKU |
| 14 | GEARBOX | Gearbox ID |
| 15 | GOOGLE_PLAY | Package name |
| 20 | HUMBLE | Humble ID |

#### 16.3.3 LanguageSupportType
| Example ID | Symbol | Description |
|------------|--------|-------------|
| 1 | AUDIO | Full Audio |
| 2 | SUBTITLES | Subtitles |
| 3 | INTERFACE | Interface/Text |
| 4 | AUDIO_PARTIAL | Partial Audio |

#### 16.3.4 ReviewSource (external_reviews.source)
| Symbol | Notes |
|--------|-------|
| METACRITIC | Aggregator |
| OPENCRITIC | Aggregator |
| IGN | Media Outlet |
| GAMESPOT | Media Outlet |
| PCGAMER | Media Outlet |
| EUROGAMER | Media Outlet |

#### 16.3.5 AchievementCategory (Placeholder)
(Exact numeric codes to be confirmed with IGDB Achievements docs. Store raw code; map to symbolic on ingestion.)
| Code | Symbol | Meaning |
|------|--------|---------|
| 0 | CORE | Base game/default |
| 1 | DLC | DLC achievement |
| 2 | EXPANSION | Expansion achievement |
| 99 | UNKNOWN | Unclassified |

### 16.4 Extended Validation & Rules
| Rule | Description | Handling |
|------|-------------|----------|
| Website Domain Allowlist | Flag suspicious domains | Mark trusted=false |
| Video Platform Validation | Accept only known platforms (YouTube) initially | Reject row (quarantine table) |
| Language Code Mapping | Map numeric -> ISO | If unknown -> store numeric + warn |
| Multiplayer Cardinalities | Ensure max >= coop max values | Adjust or flag issue |
| External Ref Uniqueness | (category, uid) unique per game | Upsert conflict -> keep earliest |
| Descriptor Linking | Age rating descriptors must reference valid age_rating | Defer until age_rating present |
| Achievement Icon Presence | Missing locked OR unlocked icon | If both missing -> mark asset_missing=true |
| Achievement Duplicate Slug | Duplicate slug within same game | Append -{source_ref} |
| Achievement Points Range | points <0 | Clamp to 0 + warn |

### 16.5 Incremental Prioritization Roadmap
| Phase | Entities |
|-------|----------|
| Phase 1 (Current) | Game, Platform, Genre, Theme, GameMode, PlayerPerspective, Keyword, Collection, Franchise, Company, InvolvedCompany, ReleaseDate, AgeRating, MediaAsset (Cover, Screenshot, Artwork) |
| Phase 2 | GameAchievement, AlternativeName, GameWebsite, GameVideo, MultiplayerMode, LanguageSupport & LanguageSupportType, ExternalReference, GameEngine |
| Phase 3 | ExternalReview, AgeRatingDescriptor, PlatformFamily/Version structures, CompanyWebsite/PlatformWebsite |
| Phase 4 | GameVersion & Feature taxonomy, Additional media/logos |

### 16.6 Open Metrics Additions
Add new DQ metrics once Phase 2 begins:
| Metric | Target |
|--------|--------|
| Orphan Website Ratio | < 1% |
| Unknown Language Code Ratio | < 2% |
| Video Platform Rejection Rate | < 0.5% |
| External Ref Duplication Rate | 0% |
| Achievement Icon Missing Ratio | < 2% |
| Achievement Unknown Category Ratio | < 1% |

### 16.7 Storage Impact Estimate (High-Level)
Add row:
| Entity | Avg Payload Bytes | Est. Count | 12-Mo Storage (~MB) |
|--------|-------------------|-----------|---------------------|
| GameAchievement | 180 | 15M | ~2575 |

### 16.8 Risk & Mitigation
| Risk | Impact | Mitigation |
|------|--------|-----------|
| Data Volume Spike (LanguageSupport) | Storage/ingest cost | Batch window throttling + compression |
| Unstable ExternalRef categories | Integrity drift | Weekly audit vs IGDB taxonomy export |
| Website Malicious URLs | Security exposure | Domain validation + blocklist |
| Video Link Rot | Broken UX | Periodic HEAD checks & revalidation |
| Descriptor Taxonomy Changes | Enum mismatch | Versioned descriptor snapshot table |
| High Volume Achievements | Large storage & ingest time | Partition table (hash on game_id) + compression |
| Missing Category Codes | Ambiguous classification | Fallback to CORE & log metric |

### 16.9 Deferred Justification
Deferred entities add complexity without immediate product value (e.g., platform versions). They remain documented to prevent scope creep ambiguity and to support future analytic use cases (hardware lineage, edition feature diffs).

### 16.10 Update Procedure
When moving an entity from Planned/Deferred to Core:
1. Add migration (tables / indexes)
2. Implement ingestion extractor & transformer
3. Extend validation & metrics
4. Update Watermark tracking
5. Bump document version & revision history
6. Announce in CHANGELOG
