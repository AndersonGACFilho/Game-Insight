# Xbox Live Source Mapping

Title: Xbox Live (XBL) -> Game Service Data Mapping <br>
Version: 0.1.1 <br>
Last Updated: 2025-08-15 <br>
Owner: Anderson (Sole Maintainer) <andersonfilho09@gmail.com> <br>
Status: Draft <br>
Decision: Initial Xbox Live coverage (standardized formatting) <br>

> Compliance & Legal Disclaimer (EN): This document is a design-only specification. No Xbox Live (XBL) user, presence, social, entitlement, achievement, media or telemetry data is ingested, stored, processed, or exposed by the project at this time. XBL access requires explicit, written platform agreements (e.g., Microsoft/Xbox partner programs). Until such agreements exist, all code paths referring to XBL must remain disabled behind feature flags and rely solely on synthetic or mocked data. Users must never be asked to provide console credentials or tokens outside officially sanctioned OAuth/device flows. Any unauthorized scraping, reverse engineering, or credential collection is explicitly out of scope and prohibited.

## 1. Purpose
Define canonical mapping of Xbox Live (via xbl.io and official XBL endpoints) entities to an internal domain model for enrichment: user profile, presence, social graph, ownership/history, achievements, media, telemetry for recommendations.

## 2. Scope
In-Scope (Phase 1–2):
- User Profile (gamertag, gamer pic, tenure, reputation)
- Friends / Following (social graph)
- Presence (activity + rich presence strings)
- Title History / Owned Titles (recent & owned games)
- Achievements (title + achievement definitions)
- User Achievement Progress
- Game Clips (user-generated clips metadata) [optional Phase 2]
- Screenshots (user-generated) [optional Phase 2]

Deferred: Clubs, messages, store catalog pricing, multiplayer session details, inventories.

## 3. Source References
- XBL.io Developer Console / REST endpoints (factual field names)
- Official Xbox Live documentation (partner restricted) – field names considered factual data.

## 4. High-Level Internal Entities (Xbox Augmentation)
| Internal Entity | Purpose | XBL Endpoint (Conceptual) | Cardinality |
|-----------------|---------|---------------------------|-------------|
| ExternalReference (XBL TitleId) | Link Game <-> Xbox Title | Title metadata | Game:0..N |
| XBLUserProfile | Core user public profile snapshot | /profile | User:1 |
| XBLFriendLink | Social relation (friend/follow) | /friends | User:0..N |
| XBLUserPresence | Presence & activity snapshot | /presence | User: time-series |
| XBLTitleHistory | Recently played / owned titles | /titles | User:0..N |
| XBLTitleAchievementSet | Achievement set per (user,title) base defs | /achievements/summary | Title:1 |
| XBLAchievementDefinition | Achievement metadata | /achievements/title | Title:0..N |
| XBLUserAchievementProgress | User progress per achievement | /achievements/player | User-Title:0..N |
| XBLGameClip | User game clip metadata | /gameclips | User-Title:0..N |
| XBLScreenshot | User screenshot metadata | /screenshots | User-Title:0..N |

## 5. Identifier Strategy
| Entity | PK | Natural / Source Keys |
|--------|----|----------------------|
| XBLUserProfile | xbl_user_profile_id (UUID v7) | xuid (numeric 64-bit) |
| XBLFriendLink | xbl_friend_link_id | (xuid, friend_xuid) |
| XBLUserPresence | xbl_presence_id | (xuid, captured_at) |
| XBLTitleHistory | xbl_title_history_id | (xuid, title_id) |
| XBLTitleAchievementSet | xbl_title_ach_set_id | (title_id) |
| XBLAchievementDefinition | xbl_achievement_def_id | (title_id, achievement_id) |
| XBLUserAchievementProgress | xbl_user_ach_progress_id | (xuid, title_id, achievement_id) |
| XBLGameClip | xbl_game_clip_id | (xuid, clip_id) |
| XBLScreenshot | xbl_screenshot_id | (xuid, screenshot_id) |

All numeric IDs stored as BIGINT. Title IDs mapped to Game via ExternalReference(provider=XBL, category=TITLE_ID).

## 6. Field-Level Mapping
### 6.1 XBLUserProfile (/profile)
| Field | Source | Notes |
|-------|--------|-------|
| xbl_user_profile_id | (generated) | UUID |
| xuid | profile.xuid | BIGINT |
| gamertag | profile.gamertag | Case preserved |
| gamer_score | profile.gamerscore | INT |
| gamer_pic_url | profile.gameDisplayPicRaw | HTTPS normalized |
| account_tier | profile.accountTier | ENUM (Silver/Gold/Ultimate) |
| reputation | profile.reputation | Raw string (e.g., "GoodPlayer") |
| tenure_level | profile.tenureLevel | INT |
| preferred_color_primary | profile.preferredColor.primaryColor | HEX |
| locale | profile.locale | e.g., en-US |
| bio | profile.bio | Truncate 500, sanitize |
| location | profile.location | Optional free text |
| follower_count | profile.followersCount | INT |
| following_count | profile.followingCount | INT |
| has_game_pass | (derived) | If accountTier implies subscription |
| created_at_source | (n/a) | Null (not exposed) |
| updated_at_source | (n/a) | Null |
| last_profile_refresh | (system) | Timestamp |

### 6.2 XBLFriendLink (/friends)
| Field | Source | Notes |
|-------|--------|-------|
| xbl_friend_link_id | (generated) | |
| xuid | context.xuid | Origin user |
| friend_xuid | friend.xuid | Target |
| friend_gamertag | friend.gamertag | Denormalized |
| is_favorite | friend.isFavorite | BOOLEAN |
| is_following | friend.isFollowingCaller | BOOLEAN |
| is_followed_by | friend.isFollowedByCaller | BOOLEAN |
| added_at | friend.addedDateTimeUtc | ISO -> ts |
| last_refresh | (system) | |

### 6.3 XBLUserPresence (/presence)
| Field | Source | Notes |
|-------|--------|-------|
| xbl_presence_id | (generated) | |
| xuid | presence.xuid | |
| state | presence.state | ONLINE, OFFLINE, IDLE, AWAY |
| last_seen_title_id | presence.lastSeen.titleId | Nullable |
| last_seen_title_name | presence.lastSeen.titleName | Denormalized |
| last_seen_at | presence.lastSeen.timestamp | Timestamp |
| active_titles | presence.titles[].titleId | Array of active title IDs |
| rich_presence_strings | presence.titles[].activity | Array of strings |
| captured_at | (system) | Snapshot time |

### 6.4 XBLTitleHistory (/titles)
| Field | Source | Notes |
|-------|--------|-------|
| xbl_title_history_id | (generated) | |
| xuid | titles.xuid | |
| title_id | titles[].titleId | BIGINT |
| service_config_id | titles[].serviceConfigId | Optional |
| name | titles[].name | Trim |
| display_image_url | titles[].displayImage | |
| media_type | titles[].mediaItemType | GAME / APP |
| total_achievements | titles[].achievement.totalPossible | INT |
| current_achievements_unlocked | titles[].achievement.currentAchievements | INT |
| last_played_at | titles[].activityDetails.lastTimePlayed | Timestamp |
| minutes_played | titles[].detail.minutesPlayed | INT (nullable) |
| platform_family | titles[].detail.platform | e.g., XboxOne, SeriesX, PC |
| last_refresh | (system) | |

### 6.5 XBLTitleAchievementSet (/achievements/title)
| Field | Source | Notes |
|-------|--------|-------|
| xbl_title_ach_set_id | (generated) | |
| title_id | achievementSet.titleId | |
| total_achievements | achievementSet.totalAchievements | INT |
| total_gamerscore | achievementSet.totalGamerscore | INT |
| platform_family | achievementSet.platform | |
| last_refresh | (system) | |

### 6.6 XBLAchievementDefinition (/achievements/title)
| Field | Source | Notes |
|-------|--------|-------|
| xbl_achievement_def_id | (generated) | |
| title_id | achievement.titleId | FK mapping to Game |
| achievement_id | achievement.id | STRING (GUID-like) |
| name | achievement.name | Localized default |
| description | achievement.description | |
| locked_description | achievement.lockedDescription | Optional alt text |
| is_secret | achievement.isSecret | BOOLEAN |
| progression_type | achievement.progression.type | STATIC / INCREMENTAL |
| progression_target | achievement.progression.target | INT if incremental |
| media_asset_url | achievement.mediaAssets[].url | Choose first of type Art/Tile |
| position | achievement.ordering | INT ordering |
| rarity_current_percentage | achievement.rarity.currentPercentage | DECIMAL(6,3) |
| rarity_category | achievement.rarity.category | COMMON/RARE/etc. |
| gamerscore | rewards[].value (type=Gamerscore) | INT |
| available_at | achievement.progressState.timeUnlocked (if available) | For scheduled unlock |
| created_at_source | (n/a) | Null |
| updated_at_source | (n/a) | Null |
| last_refresh | (system) | |

### 6.7 XBLUserAchievementProgress (/achievements/player)
| Field | Source | Notes |
|-------|--------|-------|
| xbl_user_ach_progress_id | (generated) | |
| xuid | progress.xuid | |
| title_id | progress.titleId | |
| achievement_id | progress.id | FK def |
| progress_state | progress.progressState | ACHIEVED / INPROGRESS / NOT_STARTED |
| percent_complete | progress.progression.timeUnlocked? else computed | For incremental |
| current_progress | progress.progression.current | INT |
| target_progress | progress.progression.target | INT |
| unlocked_at | progress.progression.timeUnlocked | Timestamp |
| last_refresh | (system) | |

### 6.8 XBLGameClip (/gameclips)
| Field | Source | Notes |
|-------|--------|-------|
| xbl_game_clip_id | (generated) | |
| xuid | clip.xuid | |
| clip_id | clip.gameClipId | |
| title_id | clip.titleId | Map to Game |
| caption | clip.userCaption | Nullable |
| duration_seconds | clip.durationInSeconds | INT |
| resolution_width | clip.thumbnails[0].width | Representative |
| resolution_height | clip.thumbnails[0].height | |
| thumbnail_url | clip.thumbnails[0].uri | |
| video_url | clip.gameClipUris[0].uri | Select highest quality |
| recording_started_at | clip.dateRecorded | Timestamp |
| published_at | clip.datePublished | Timestamp |
| views | clip.views | INT |
| rating | clip.rating | Optional |
| last_refresh | (system) | |

### 6.9 XBLScreenshot (/screenshots)
| Field | Source | Notes |
|-------|--------|-------|
| xbl_screenshot_id | (generated) | |
| xuid | shot.xuid | |
| screenshot_id | shot.screenshotId | |
| title_id | shot.titleId | |
| resolution_width | shot.thumbnails[0].width | |
| resolution_height | shot.thumbnails[0].height | |
| thumbnail_url | shot.thumbnails[0].uri | |
| image_url | shot.screenshotUris[0].uri | Highest quality first |
| captured_at | shot.dateTaken | Timestamp |
| published_at | shot.datePublished | Timestamp |
| views | shot.views | INT |
| last_refresh | (system) | |

## 7. Enumerations
### 7.1 PresenceState
| Symbol | Meaning |
|--------|---------|
| ONLINE | User online |
| OFFLINE | User offline |
| IDLE | Idle (inactivity) |
| AWAY | Away |

### 7.2 MediaItemType
| Symbol | Meaning |
|--------|---------|
| GAME | Playable title |
| APP | Non-game application |

### 7.3 AchievementProgressState
| Symbol | Meaning |
|--------|---------|
| NOT_STARTED | No progress |
| INPROGRESS | Partial progress |
| ACHIEVED | Completed |

### 7.4 RarityCategory (Xbox)
| Symbol | Approx Boundary (%) |
|--------|----------------------|
| COMMON | > 10 |
| UNCOMMON | 5-10 |
| RARE | 1-5 |
| ULTRA_RARE | <= 1 |

(Exact categories may differ; store raw category and computed boundaries.)

## 8. Transformation & Validation Rules
| Rule | Description | Handling |
|------|-------------|----------|
| Gamertag Charset | Validate allowed symbols length <= 15 | Reject or truncate (log) |
| Image URL Normalization | Force https scheme | If not possible -> mark invalid |
| Rarity Bounds | Percentage 0-100 | Clamp & warn |
| Progress Integrity | current <= target | If > target set current=target + warn |
| Clip Video Selection | Choose highest bitrate uri | Heuristic on resolution/bitrate fields |
| Duplicate Achievement Def | (title_id, achievement_id) | Upsert diff fields |
| Unmapped Title | TitleId not linked to Game | Queue IGDB/XBL catalog enrichment |
| Large Media Arrays | Limit to first representative asset | Keep counts for metrics |

## 9. Ingestion Flow
1. Resolve user linking (OAuth tokens) out of scope here.
2. Daily: Full profile + title history & entitlement refresh for active users.
3. 4h: Achievement definitions delta for recently active titles.
4. Hourly: User achievement progress for sessions in last 48h.
5. 5m: Presence polling (active cohort).
6. 30m: Game clips & screenshots fetch for new captures (optional Phase 2 toggle).

Watermarks: (endpoint, last_run_at, since_token?). Use incremental tokens if provided by xbl.io (else time-based).

## 10. Privacy & Compliance
- Gamertag treated as pseudonymous; respect user consent for public display.
- Remove or hash bio if user opts out of sharing.
- Avoid storing full friend graph for users who revoke consent – purge on unlink.
- Media (clips/screenshots) flagged NSFW detection pipeline (future) before public surfacing.

## 11. Metrics
| Metric | Target | Notes |
|--------|--------|------|
| Title Link Coverage | > 95% | Titles mapped to Game |
| Achievement Def Freshness p95 | < 24h | |
| Presence Lag p95 | < 2m | |
| Unmapped Title Ratio | < 5% | |
| Media Fetch Failure Rate | < 1% | Retries excluded |

## 12. Open Questions
| ID | Question | Status |
|----|----------|--------|
| X1 | Need incremental token support from xbl.io? | Investigate |
| X2 | Multi-language achievement text caching? | Pending locale strategy |
| X3 | Cross-platform merging (PC vs Console title IDs)? | Mapping rules TBD |

## 13. Future Extensions
- Store store-offer/pricing for price-based recommendations.
- Integrate Game Pass catalog availability signals.
- Real-time push (if future WebSocket presence) to reduce polling cost.
- Content moderation for user media assets.

## 14. Data Quality Issue Codes (XBL)
| Code | Description | Severity |
|------|------------|----------|
| XB01 | Unmapped TitleId | WARN |
| XB02 | Rarity percent out of range | WARN |
| XB03 | Progress > target | WARN |
| XB04 | Missing media asset | INFO |
| XB05 | Duplicate achievement def | INFO |

## 15. Rate Limits & Backoff
- Respect xbl.io quota: central throttle bucket (configurable QPS limit, e.g., 10 QPS).
- 429/503 backoff: exponential (base 500 ms, factor 2, max 60s, jitter 25%).
- Presence: batch requests where supported up to API max (assume 25 users). Split remainder.

## 16. Change Management
1. Propose addition or modification; update this doc & bump version.
2. Implement ingestion & migrations.
3. Add monitoring metrics and DQ codes.
4. Deploy behind a feature flag if high-volume.

## 17. Revision History
- v0.1.1 (2025-08-15): Standardized arrow formatting (->) in title/decision.
- v0.1.0 (2025-08-15): Initial Xbox Live mapping document.
