# Game Catalog ER Diagram

Title: Game Catalog ER Diagram (IGDB + Phase 2) <br>
Version: 0.1.0 <br>
Last Updated: 2025-08-15 <br>
Owner: Anderson (Sole Maintainer) <andersonfilho09@gmail.com> <br>
Status: Draft <br>
Decision: Provide high-level ER overview for core & phase 2 entities <br>

```mermaid
erDiagram
    GAME ||--o{ RELEASE_DATE : has
    GAME ||--o{ INVOLVED_COMPANY : has
    GAME ||--o{ GAME_GENRE : tags
    GAME ||--o{ GAME_THEME : tags
    GAME ||--o{ GAME_KEYWORD : tags
    GAME ||--o{ GAME_MODE_LINK : modes
    GAME ||--o{ GAME_PLAYER_PERSPECTIVE : perspectives
    GAME ||--o{ GAME_FRANCHISE : franchise_link
    GAME ||--o{ MEDIA_ASSET : assets
    GAME ||--|{ GAME_ACHIEVEMENT : achievements
    GAME ||--o{ GAME_ALT_NAME : alt_names
    GAME ||--o{ GAME_WEBSITE : websites
    GAME ||--o{ GAME_VIDEO : videos
    GAME ||--o{ MULTIPLAYER_MODE : multiplayer
    GAME ||--o{ GAME_LANGUAGE_SUPPORT : language_support
    GAME ||--o{ EXTERNAL_REFERENCE : external_ids
    GAME ||--o| GAME : parent

    PLATFORM ||--o{ RELEASE_DATE : context
    COMPANY ||--o{ INVOLVED_COMPANY : roles

    GENRE ||--o{ GAME_GENRE : rev
    THEME ||--o{ GAME_THEME : rev
    KEYWORD ||--o{ GAME_KEYWORD : rev
    GAME_MODE ||--o{ GAME_MODE_LINK : rev
    PLAYER_PERSPECTIVE ||--o{ GAME_PLAYER_PERSPECTIVE : rev
    FRANCHISE ||--o{ GAME_FRANCHISE : rev
    AGE_RATING ||--o| GAME : classify

    GAME {
        UUID game_id PK
        BIGINT source_ref
        TEXT slug
        TEXT title
        DATE first_release_date
        NUMERIC popularity
        INT steam_app_id
        NUMERIC popularity_composite
    }
    GAME_ACHIEVEMENT {
        UUID game_achievement_id PK
        BIGINT source_ref
        UUID game_id FK
        TEXT name
        TEXT slug
        INT points
        SMALLINT category_code
    }
    EXTERNAL_REFERENCE {
        UUID external_reference_id PK
        UUID game_id FK
        VARCHAR provider
        VARCHAR category
        VARCHAR external_id
    }
```

## Notes
- Relationship tables (GAME_GENRE etc.) omitted detailed columns for clarity.
- Achievement icons reused via MEDIA_ASSET (not separately drawn).
- Future entities (Version, Engine, Descriptor) excluded until Phase 3 promotion.

## Revision History
- v0.1.0 (2025-08-15): Initial ER diagram.

