# Steam Source Mapping

Title: Steam Web API -> Game Service Data Mapping <br>
Version: 0.1.1 <br>
Last Updated: 2025-08-15 <br>
Owner: Anderson (Sole Maintainer) <andersonfilho09@gmail.com> <br>
Status: Draft <br>
Decision: Initial Steam coverage (standardized formatting) <br>

> Compliance Notice (EN): This mapping relies solely on publicly documented Steam Web API endpoints respecting Valve's terms (non‑commercial / key-specific usage) for portfolio/demo purposes. No password harvesting, session cookie reuse, storefront HTML scraping, or circumvention of visibility / privacy settings is performed or supported. Private profile fields are excluded unless a user explicitly consents and the Steam visibility state permits them. Any future commercial use would require re‑evaluation of Valve’s applicable terms and possibly an alternative licensed data arrangement.

## 1. Purpose
Define canonical, versioned mapping of Steam Web API entities (public + authenticated) to internal domain entities, complementing IGDB mapping (see igdb-source-mapping.md). Focus on gameplay stats, achievements, ownership, player profile and news for enrichment & personalization.

## 2. Scope
In-Scope (Phase 1–2):
- Player Profile (GetPlayerSummaries)
- Friends (GetFriendList)
- Owned Games & Playtime (GetOwnedGames)
- Recently Played (GetRecentlyPlayedGames)
- Global Achievement Percentages (GetGlobalAchievementPercentagesForApp)
- Player Achievements (GetPlayerAchievements)
- User Stats (GetUserStatsForGame)
- Game News (GetNewsForApp)

Out of Scope (Now): Trading, inventory, workshop, economy, VAC/game bans, server lists.

## 3. Source References
- Steam Web API Docs: https://developer.valvesoftware.com/wiki/Steam_Web_API
- Service discovery: ISteamWebAPIUtil/GetSupportedAPIList
- Data privacy: Steam profile visibility rules.

## 4. High-Level Internal Entities (Steam Augmentation)
| Internal Entity | Purpose | Source Endpoint | Cardinality |
|-----------------|---------|-----------------|-------------|
| ExternalReference (steam app) | Link Game <-> Steam AppID | (Derived via appids) | Game:1..N refs |
| SteamPlayerProfile | Cached subset of Steam user profile | GetPlayerSummaries | Player:1 |
| SteamFriendLink | Friendship edge | GetFriendList | Player:0..N |
| UserGameOwnership | Ownership + lifetime playtime | GetOwnedGames | User-Game:1..N |
| UserRecentPlay | Playtime last 2 weeks snapshot | GetRecentlyPlayedGames | User-Game:0..M (recent) |
| GlobalAchievementStat | Global unlock % per achievement | GetGlobalAchievementPercentagesForApp | Game:0..N |
| UserAchievement | Per-user achievement state | GetPlayerAchievements | User-Game:0..N |
| UserGameStat | Arbitrary numeric stat | GetUserStatsForGame | User-Game:0..N |
| GameNewsItem | News article metadata | GetNewsForApp | Game:0..N |
| PersonaStateEvent (optional) | State changes (online/ingame) | GetPlayerSummaries polling | User: time-series |

## 5. Identifier Strategy
| Entity | Internal PK | Natural Key(s) |
|--------|-------------|-----------------|
| SteamPlayerProfile | steam_player_id (UUID v7) | steamid (64-bit) |
| SteamFriendLink | steam_friend_link_id | (steamid, friend_steamid) |
| UserGameOwnership | user_game_ownership_id | (user_id, game_id, provider=STEAM) |
| UserRecentPlay | user_recent_play_id | (user_id, game_id, window_start_ts) |
| GlobalAchievementStat | global_achievement_stat_id | (app_id, api_name) |
| UserAchievement | user_achievement_id | (user_id, app_id, api_name) |
| UserGameStat | user_game_stat_id | (user_id, app_id, stat_name, snapshot_ts) |
| GameNewsItem | game_news_item_id | (app_id, news_id) |

Steam 64-bit IDs stored as BIGINT (unsigned semantics). AppIDs as INT (positive). Use provider enum STEAM in ExternalReference linking to internal Game.

## 6. Field-Level Mapping
### 6.1 SteamPlayerProfile (GetPlayerSummaries)
| Internal Field | Source Field | Type | Notes |
|----------------|-------------|------|-------|
| steam_player_id | (generated) | UUID | New row per steamid first seen |
| steamid | steamid | BIGINT | 64-bit ID |
| persona_name | personaname | TEXT | Trim |
| profile_url | profileurl | TEXT | Validate https |
| avatar_small_url | avatar | TEXT | 32x32 |
| avatar_medium_url | avatarmedium | TEXT | 64x64 |
| avatar_full_url | avatarfull | TEXT | 184x184 |
| persona_state | personastate | SMALLINT | Enum (0-6) |
| visibility_state | communityvisibilitystate | SMALLINT | 1=Hidden,3=Public |
| profile_configured | profilestate | BOOLEAN | profilestate==1 |
| last_logoff | lastlogoff | TIMESTAMP | Epoch -> ts |
| real_name | realname | TEXT | Nullable (PII: treat as sensitive) |
| primary_clan_id | primaryclanid | BIGINT | Nullable |
| account_created_at | timecreated | TIMESTAMP | Nullable |
| current_game_app_id | gameid | INT | Nullable; link to Game via ExternalReference if present |
| current_game_extra | gameextrainfo | TEXT | Short name |
| country_code | loccountrycode | CHAR(2) | Uppercase |
| state_code | locstatecode | TEXT | Raw; optional |
| city_id | loccityid | INT | Optional; low priority |
| last_profile_refresh | (system) | TIMESTAMP | Ingestion/poll time |

### 6.2 SteamFriendLink (GetFriendList)
| Internal Field | Source Field | Notes |
|----------------|-------------|-------|
| steam_friend_link_id | (generated) | UUID |
| user_steamid | steamid (request) | Owner user |
| friend_steamid | friends[].steamid | Target |
| relationship | friends[].relationship | Expect 'friend' |
| friend_since | friends[].friend_since | Epoch -> ts |
| created_at | (system) | Insertion time |

### 6.3 UserGameOwnership (GetOwnedGames)
| Internal Field | Source Field | Notes |
|----------------|-------------|-------|
| user_game_ownership_id | (generated) | |
| user_id | (resolved) | Internal user mapping to steamid |
| steamid | steamid | Redundant for trace |
| app_id | games[].appid | INT |
| game_id | (resolved) | FK Game (via ExternalReference) |
| name | games[].name | Snapshot (optional if include_appinfo=true) |
| playtime_forever_min | games[].playtime_forever | INT minutes |
| playtime_2weeks_min | games[].playtime_2weeks | INT minutes; nullable |
| has_visible_stats | games[].has_community_visible_stats | BOOLEAN |
| icon_image_hash | games[].img_icon_url | TEXT |
| logo_image_hash | games[].img_logo_url | TEXT |
| last_ownership_refresh | (system) | TIMESTAMP |
| free_license | (derived) | If missing price & flagged free elsewhere |

### 6.4 UserRecentPlay (GetRecentlyPlayedGames)
| Field | Source | Notes |
|-------|--------|-------|
| user_recent_play_id | (generated) | |
| user_id | steamid | |
| app_id | games[].appid | |
| game_id | (resolved) | |
| playtime_2weeks_min | games[].playtime_2weeks | |
| playtime_forever_min | games[].playtime_forever | Snapshot (denormalized) |
| snapshot_ts | (system) | Window anchor |

### 6.5 GlobalAchievementStat (GetGlobalAchievementPercentagesForApp)
| Field | Source | Notes |
|-------|--------|-------|
| global_achievement_stat_id | (generated) | |
| app_id | gameid | |
| game_id | (resolved) | |
| api_name | achievements[].name | Key (lowercase) |
| percent | achievements[].percent | DECIMAL(6,3) |
| snapshot_ts | (system) | Timestamp |

### 6.6 UserAchievement (GetPlayerAchievements)
| Field | Source | Notes |
|-------|--------|-------|
| user_achievement_id | (generated) | |
| user_id | steamid | |
| app_id | appid | |
| game_id | (resolved) | |
| api_name | achievements[].apiname | |
| achieved | achievements[].achieved | BOOLEAN |
| unlock_time | achievements[].unlocktime | TIMESTAMP nullable (0 => NULL) |
| localized_name | achievements[].name | Optional if language requested |
| localized_description | achievements[].description | Optional |
| last_refresh | (system) | |

### 6.7 UserGameStat (GetUserStatsForGame)
| Field | Source | Notes |
|-------|--------|-------|
| user_game_stat_id | (generated) | |
| user_id | steamid | |
| app_id | appid | |
| game_id | (resolved) | |
| stat_name | stats[].name | |
| stat_value | stats[].value | BIGINT |
| snapshot_ts | (system) | |

### 6.8 GameNewsItem (GetNewsForApp)
| Field | Source | Notes |
|-------|--------|-------|
| game_news_item_id | (generated) | |
| app_id | appid | |
| game_id | (resolved) | |
| news_id | newsitems[].gid | Unique external id |
| title | newsitems[].title | Trim |
| url | newsitems[].url | Normalize https |
| author | newsitems[].author | Nullable |
| contents_excerpt | newsitems[].contents | Truncated to maxlength param |
| tags_raw | newsitems[].tags | Comma-split -> array |
| publish_time | newsitems[].date | Epoch -> ts |
| feed_type | (derived) | Source feed classification |
| language | newsitems[].feedlabel / heuristics | Optional |
| checksum | (derived) | Hash(title+publish_time) |
| fetched_at | (system) | Timestamp |

## 7. Enumerations
### 7.1 PersonaState
| Code | Symbol | Meaning |
|------|--------|---------|
| 0 | OFFLINE | Offline |
| 1 | ONLINE | Online |
| 2 | BUSY | Busy |
| 3 | AWAY | Away |
| 4 | SNOOZE | Snooze |
| 5 | LOOKING_TRADE | Looking to Trade |
| 6 | LOOKING_PLAY | Looking to Play |

### 7.2 VisibilityState
| Code | Symbol | Meaning |
|------|--------|---------|
| 1 | HIDDEN | Not visible (Private/Friends) |
| 3 | PUBLIC | Public profile |

### 7.3 AchievementStatus
| Value | Meaning |
|-------|---------|
| 0 | LOCKED |
| 1 | UNLOCKED |

## 8. Transformation & Validation Rules
| Rule | Description | Handling |
|------|-------------|----------|
| Epoch Conversion | Convert all epoch ints (seconds) to UTC TIMESTAMP | Null if <2000 or > now+1d |
| Visibility Filter | If profile visibility != PUBLIC restrict to allowed fields | Store partial & flag restricted |
| Zero Unlock Time | unlocktime==0 means not unlocked | Set NULL |
| Duplicate Friend Edge | Existing (user,friend) | Upsert ignore |
| Percent Bounds | Achievement percent 0-100 | Clamp & warn if out of range |
| News Truncation | Respect caller maxlength | Store indicator truncated=true |
| Stat Overflow | Value > 2^63-1 | Set to max & flag overflow |
| Ownership Missing Game | If no internal Game linked | Queue IGDB enrichment by app_id & mark pending |

## 9. Ingestion Flow (Per Steam Domain)
1. Resolve internal user -> steamid mapping (auth handshake out of scope here).
2. Nightly: Owned Games (baseline) + Global Achievement Percentages updates for active catalog.
3. Hourly: Recently Played, Player Achievements delta for active users (activity heuristic: played in last 30 days).
4. 15-min: Player Summaries (lightweight) for online status of active cohort.
5. 6h: Game News fetch (recent 50 items per tracked app, dedupe by news_id).
6. On-demand: User stats for recommendation feature extraction (triggered by play event).

Watermarks per endpoint: (steam_endpoint, last_run_at, paging_cursor?). Most endpoints not paged.

## 10. Privacy & Compliance
- Do NOT persist real_name unless user consent flag present (user_profile_consent=true). If absent store hashed (SHA256 + salt) or drop.
- Country/state/city treated as coarse location; avoid reverse lookup or correlation.
- Honor user deletion: drop Steam augmentation rows referencing deleted user_id (soft delete mask).

## 11. Metrics
| Metric | Target | Notes |
|--------|--------|------|
| Ownership Link Coverage | > 98% | Owned rows with resolved game_id |
| Achievement Global Freshness p95 | < 24h | Time since last stat snapshot |
| Recent Play Lag p95 | < 30m | Snapshot delay |
| News Duplicate Rate | < 1% | Duplicate news_id ignored |
| Restricted Profile Ratio | Tracked | Monitoring only |

## 12. Open Questions
| ID | Question | Status |
|----|----------|--------|
| S1 | Store full avatar images or proxy/cache? | Pending CDN decision |
| S2 | Need historical retention of stats (versioning)? | Likely 30d rolling window |
| S3 | Support multi-language achievements caching? | Evaluate demand |
| S4 | Normalizing non-Steam game play (shortcuts)? | Low priority |

## 13. Future Extensions
- Economy / inventory integration for cosmetic-based recommendations.
- VAC ban signals (risk scoring) - sensitive (governance required).
- Rich presence events (real-time websockets) instead of polling.
- News sentiment analysis & topic tagging.

## 14. Data Quality Issue Codes (Steam)
| Code | Description | Severity |
|------|------------|----------|
| ST01 | Missing Game mapping for app_id | WARN |
| ST02 | Achievement percent out of bounds | WARN |
| ST03 | Stat overflow truncated | WARN |
| ST04 | Profile restricted (partial data) | INFO |
| ST05 | News duplicate suppressed | INFO |

## 15. Dependencies & Rate Limits
- Apply exponential backoff (base 500ms, factor 2, max 8s) on HTTP 429.
- Concurrency: limit parallel calls per endpoint to 5.
- Cache PlayerSummaries batching up to 100 steamids per request.

## 16. Change Management
When adding new Steam endpoint:
1. Add entity mapping (Section 6.x)
2. Extend enumerations if needed
3. Add migration & indexes
4. Update metrics & DQ codes
5. Bump version & revision history

## 17. Revision History
- v0.1.1 (2025-08-15): Standardized arrow formatting (->) and metadata wording.
- v0.1.0 (2025-08-15): Initial Steam mapping document.
