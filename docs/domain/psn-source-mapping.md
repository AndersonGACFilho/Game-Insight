# PSN Source Mapping

Title: PlayStation Network (PSN) -> Game Service Data Mapping <br>
Version: 0.1.1 <br>
Last Updated: 2025-08-15 <br>
Owner: Anderson (Sole Maintainer) <andersonfilho09@gmail.com> <br>
Status: Draft <br>
Decision: Initial PSN coverage (standardized formatting) <br>

> Compliance & Legal Disclaimer (EN): Design-only specification. The project does NOT ingest, store, process, or expose PlayStation Network (PSN) user, entitlement, presence, or trophy data. PSN integration requires explicit, written approval via official Sony / PlayStation partner programs. Until such approval exists, any PSN-related code must remain disabled behind feature flags and utilize synthetic or mocked datasets exclusively. Users must never be asked for PSN credentials, device authentication cookies, or to execute manual token extraction steps. Scraping, reverse engineering, or automating non-public PSN endpoints is explicitly prohibited and out of scope.

## 1. Purpose
Define canonical mapping of PSN (as accessed via psnawp / official PSN Web API) entities to internal domain model to enrich user profiles, ownership, and achievements (trophies) alongside IGDB & Steam sources.

## 2. Scope
In-Scope (Phase 1-2):
- User Profile (basic + avatar + plus status)
- Friends / Follow Relationships
- Presence (online, in-game title id)
- Game Entitlements (owned titles / library)
- Trophies (Title trophy sets, groups, definitions)
- User Trophy Progress (earned trophies, timestamps)
- Title Metadata (name, platform, concept/title IDs)

Deferred: Store catalog pricing, DLC entitlement details, media assets from store, parental controls, messaging, leaderboard stats.

## 3. Source References
- psnawp docs: https://psnawp.readthedocs.io/en/stable
- Official PSN Web API (proprietary endpoints; field names treated as factual).

## 4. High-Level Internal Entities (PSN Augmentation)
| Internal Entity | Purpose | PSN Source | Cardinality |
|-----------------|---------|-----------|-------------|
| ExternalReference (PSN) | Link Game <-> PSN title/concept id | Title metadata | Game:0..N |
| PSNUserProfile | Core PSN user info snapshot | User profile endpoint | User:1 |
| PSNFriendLink | Friendship / follow relation | Friends endpoint | User:0..N |
| PSNUserPresence | Current presence snapshot | Presence endpoint | User: time-series |
| PSNGameEntitlement | Ownership of a title | Entitlements endpoint | User-Game:0..N |
| PSNTrophyTitle | Trophy set root per game | Trophy titles list | Game:0..1 (per platform variant) |
| PSNTrophyGroup | Group within trophy set | Trophy groups endpoint | TrophyTitle:1..N |
| PSNTrophyDefinition | Individual trophy metadata | Trophy list (per group) | TrophyGroup:0..N |
| PSNUserTrophyProgress | User-specific trophy unlock | User trophy progress | User-Trophy:0..N |

## 5. Identifier Strategy
| Entity | PK | Natural / Source Keys |
|--------|----|----------------------|
| PSNUserProfile | psn_user_profile_id (UUID v7) | online_id (string), account_id (numeric) |
| PSNFriendLink | psn_friend_link_id | (user_account_id, friend_account_id) |
| PSNUserPresence | psn_user_presence_id | (user_account_id, captured_at) |
| PSNGameEntitlement | psn_entitlement_id | (user_account_id, entitlement_id) |
| PSNTrophyTitle | psn_trophy_title_id | (np_communication_id) |
| PSNTrophyGroup | psn_trophy_group_id | (np_communication_id, group_id) |
| PSNTrophyDefinition | psn_trophy_definition_id | (np_communication_id, group_id, trophy_id) |
| PSNUserTrophyProgress | psn_user_trophy_progress_id | (user_account_id, np_communication_id, trophy_id) |

Note: np_communication_id uniquely identifies a title’s trophy set (e.g., NPWRxxxxx_00). Map to Game via ExternalReference(provider=PSN, category=NP_COMM_ID).

## 6. Field-Level Mapping
### 6.1 PSNUserProfile
| Field | Source | Notes |
|-------|--------|-------|
| psn_user_profile_id | (generated) | UUID |
| account_id | profile.accountId | BIGINT |
| online_id | profile.onlineId | Display ID; case-sensitive preserved |
| about_me | profile.aboutMe | Truncate 500 chars, sanitize |
| avatar_url | profile.avatars[].url (largest) | Select highest resolution |
| is_plus | profile.isPlus | BOOLEAN |
| is_verified | profile.isOfficiallyVerified | BOOLEAN |
| language | profile.languagesUsed[0] | Primary language |
| country | profile.country | ISO 3166-1 alpha-2 |
| timezone | profile.timezone | Raw Olson ID |
| privacy_level | profile.presenceRestricted | Derive enum (PUBLIC/LIMITED/PRIVATE) |
| created_at_source | (n/a) | Null unless provided |
| updated_at_source | (n/a) | Null (PSN lacks direct timestamp) |
| last_profile_refresh | (system) | Snapshot time |

### 6.2 PSNFriendLink
| Field | Source | Notes |
|-------|--------|-------|
| psn_friend_link_id | (generated) | |
| user_account_id | context.accountId | Origin user |
| friend_account_id | friend.accountId | Target |
| online_id_friend | friend.onlineId | Denormalized |
| relation_type | friend.relationship | Enum (FRIEND, FOLLOWER, FOLLOWING) |
| since | friend.addedDate | Parse ISO8601 -> ts |
| last_refresh | (system) | |

### 6.3 PSNUserPresence
| Field | Source | Notes |
|-------|--------|-------|
| psn_user_presence_id | (generated) | |
| account_id | presence.accountId | |
| primary_status | presence.primaryOnlineStatus | ONLINE/OFFLINE/AWAY |
| last_online_ts | presence.lastOnlineDate | Timestamp |
| platform | presence.platform | PS5/PS4/PS3/PSVITA/MOBILE/WEB |
| active_title_np_comm_id | presence.gameTitleInfoList[].npTitleId | First active title |
| active_title_name | presence.gameTitleInfoList[].titleName | Denormalized |
| captured_at | (system) | Snapshot time |

### 6.4 PSNGameEntitlement
| Field | Source | Notes |
|-------|--------|-------|
| psn_entitlement_id | (generated) | |
| user_account_id | entitlement.accountId | |
| entitlement_id | entitlement.id | Unique per user |
| product_id | entitlement.productId | Store product (CUSA / concept) |
| game_np_title_id | entitlement.gameMeta.npTitleId | Map to Game via ExternalReference |
| name | entitlement.gameMeta.name | Snapshot title |
| platform | entitlement.platform | Enum |
| active | entitlement.active | BOOLEAN |
| last_played_ts | entitlement.lastPlayedDateTime | Optional |
| created_at_source | entitlement.entitlementCreatedDate | Timestamp |
| updated_at_source | entitlement.lastUpdatedDate | Timestamp |
| last_refresh | (system) | |

### 6.5 PSNTrophyTitle
| Field | Source | Notes |
|-------|--------|-------|
| psn_trophy_title_id | (generated) | |
| np_communication_id | trophyTitle.npCommunicationId | Unique trophy set id |
| game_np_title_id | trophyTitle.npTitleId | Link to game title id |
| name | trophyTitle.trophyTitleName | Trim |
| icon_url | trophyTitle.trophyTitleIconUrl | Media asset |
| trophy_group_count | trophyTitle.trophyGroupCount | INT |
| bronze_total | trophyTitle.definedTrophies.bronze | INT |
| silver_total | trophyTitle.definedTrophies.silver | INT |
| gold_total | trophyTitle.definedTrophies.gold | INT |
| platinum_total | trophyTitle.definedTrophies.platinum | INT |
| platform | trophyTitle.platform | ENUM |
| last_refresh | (system) | |

### 6.6 PSNTrophyGroup
| Field | Source | Notes |
|-------|--------|-------|
| psn_trophy_group_id | (generated) | |
| np_communication_id | group.npCommunicationId | FK to title |
| group_id | group.trophyGroupId | "default" or numeric |
| name | group.trophyGroupName | |
| icon_url | group.trophyGroupIconUrl | |
| bronze_total | group.definedTrophies.bronze | |
| silver_total | group.definedTrophies.silver | |
| gold_total | group.definedTrophies.gold | |
| platinum_total | group.definedTrophies.platinum | |
| last_refresh | (system) | |

### 6.7 PSNTrophyDefinition
| Field | Source | Notes |
|-------|--------|-------|
| psn_trophy_definition_id | (generated) | |
| np_communication_id | trophy.npCommunicationId | |
| group_id | trophy.trophyGroupId | FK group |
| trophy_id | trophy.trophyId | INT code |
| type | trophy.trophyType | Enum (PLATINUM,GOLD,SILVER,BRONZE) |
| rarity | trophy.trophyRare | Raw percent or mapped bucket |
| rarity_category | trophy.trophyRareCategory | (e.g., COMMON, RARE, VERY_RARE, ULTRA_RARE) |
| hidden | trophy.hidden | BOOLEAN |
| name | trophy.trophyName | Localized default |
| detail | trophy.trophyDetail | Description |
| icon_url | trophy.trophyIconUrl | Locked/unlocked variant? PSN usually one icon |
| icon_url_gray | trophy.trophyIconUrlGray | Optional locked version |
| last_refresh | (system) | |

### 6.8 PSNUserTrophyProgress
| Field | Source | Notes |
|-------|--------|-------|
| psn_user_trophy_progress_id | (generated) | |
| account_id | progress.accountId | |
| np_communication_id | progress.npCommunicationId | |
| trophy_id | progress.trophyId | |
| unlocked | progress.earned | BOOLEAN |
| earned_at | progress.earnedDateTime | Timestamp if unlocked |
| rarity_snapshot | progress.trophyRare | Capture at unlock |
| last_refresh | (system) | |

## 7. Enumerations
### 7.1 TrophyType
| Symbol | Weight (points suggestion) |
|--------|----------------------------|
| PLATINUM | 300 |
| GOLD | 90 |
| SILVER | 30 |
| BRONZE | 15 |

### 7.2 TrophyRarityCategory
| Symbol | Percent Upper Bound (exclusive) |
|--------|-------------------------------|
| ULTRA_RARE | 5 |
| VERY_RARE | 15 |
| RARE | 50 |
| COMMON | 101 |

### 7.3 PresenceStatus
| Symbol | Meaning |
|--------|---------|
| ONLINE | User actively online |
| OFFLINE | Not connected |
| AWAY | Idle/away |

### 7.4 PrivacyLevel
| Symbol | Meaning |
|--------|---------|
| PUBLIC | All visible |
| LIMITED | Partial (friends) |
| PRIVATE | Hidden |

## 8. Transformation & Validation Rules
| Rule | Description | Handling |
|------|-------------|----------|
| OnlineId Charset | Validate allowed PSN chars | Reject or normalize lower? keep original |
| Avatar Selection | Pick largest resolution | Fallback to default |
| Trophy Rarity Bucket | Map percent -> category | If percent null -> UNKNOWN |
| Trophy Hidden | hidden true delays name/detail | Store placeholder until visible |
| Duplicate Trophy Key | (np_comm_id,trophy_id) conflict | Upsert diff fields |
| Entitlement Missing Game | No matching Game | Queue IGDB lookup by external ids |
| Presence Title Resolution | Unknown npTitleId | Create pending ExternalReference row |
| Timezone Validation | Must match Olson | If invalid set null |

## 9. Ingestion Flow
1. User authentication obtains refresh tokens (out of scope) mapped to account_id.
2. Daily: Full entitlement sync for active users.
3. 4h: Trophy titles & definitions delta (for titles where user earned progress or recently released).
4. Hourly: User trophy progress update (recently played titles subset).
5. 5m: Presence polling for online cohort (recent activity < 7d).
6. On-demand: Backfill trophy definitions when encountering unseen np_communication_id.

Watermarks: (entity, last_run_at, last_seen_cursor).

## 10. Privacy & Compliance
- Treat online_id as pseudonymous identifier; do not expose without user consent in public endpoints.
- About_me sanitized (strip HTML/script).
- Real geographic data limited to country; do not attempt exact geolocation.
- Allow user deletion: cascade soft-delete PSN augment tables by account_id.

## 11. Metrics
| Metric | Target | Notes |
|--------|--------|------|
| Entitlement Link Coverage | > 97% | Resolved to Game |
| Trophy Definition Freshness p95 | < 24h | Time since definition fetch |
| Presence Lag p95 | < 2m | Delay between status change and capture |
| Hidden Trophy Reveal Lag p95 | < 12h | After unlock or reveal |
| Missing ExternalRef Ratio | < 3% | Pending PSN title links |

## 12. Open Questions
| ID | Question | Status |
|----|----------|--------|
| P1 | Support multi-language trophy names? | Pending locale strategy |
| P2 | Store friend relationship history? | Maybe; audit need |
| P3 | Include store pricing integration? | Defer |

## 13. Future Extensions
- Store metadata ingestion (price, discount) for recommendation economics.
- Cross-platform merge where same Game has multiple np_communication_id sets (PS4 vs PS5) -> unified achievement surface.
- Real-time webhook (if PSN offers) to reduce polling.

## 14. Data Quality Issue Codes (PSN)
| Code | Description | Severity |
|------|------------|----------|
| PN01 | Missing Game mapping for npTitleId | WARN |
| PN02 | Trophy rarity null | WARN |
| PN03 | Duplicate trophy definition conflict | WARN |
| PN04 | Entitlement inactive but progress present | INFO |
| PN05 | Presence unknown platform | INFO |

## 15. Rate Limits & Backoff
- Respect psnawp internal pacing; additionally cap to 10 req/s per user context.
- Backoff: 429 -> exponential (base 1s, factor 2, max 64s, jitter).
- Parallelization: limit trophy definition fetch to five titles concurrently.

## 17. Revision History
- v0.1.1 (2025-08-15): Standardized arrow formatting (->) and fixed typo in rate limits.
- v0.1.0 (2025-08-15): Initial PSN mapping document.
