create table public.platform
(
    platform_id       uuid      default gen_random_uuid() not null
        primary key,
    source_ref        bigint                              not null
        unique,
    name              text                                not null,
    abbreviation      text,
    generation        smallint,
    category_code     smallint,
    created_at_source timestamp,
    updated_at_source timestamp,
    created_at        timestamp default now()             not null,
    updated_at        timestamp default now()             not null
);

alter table public.platform
    owner to "user";

create index idx_platform_source_ref
    on public.platform (source_ref);

create trigger trg_platform_touch
    before update
    on public.platform
    for each row
execute procedure public.touch_updated_at();

create table public.genre
(
    genre_id          uuid      default gen_random_uuid() not null
        primary key,
    source_ref        bigint                              not null
        unique,
    name              text                                not null,
    slug              text                                not null,
    created_at_source timestamp,
    updated_at_source timestamp,
    created_at        timestamp default now()             not null,
    updated_at        timestamp default now()             not null
);

alter table public.genre
    owner to "user";

create table public.theme
(
    theme_id          uuid      default gen_random_uuid() not null
        primary key,
    source_ref        bigint                              not null
        unique,
    name              text                                not null,
    slug              text                                not null,
    created_at_source timestamp,
    updated_at_source timestamp,
    created_at        timestamp default now()             not null,
    updated_at        timestamp default now()             not null
);

alter table public.theme
    owner to "user";

create table public.game_mode
(
    game_mode_id      uuid      default gen_random_uuid() not null
        primary key,
    source_ref        bigint                              not null
        unique,
    name              text                                not null,
    slug              text                                not null,
    created_at_source timestamp,
    updated_at_source timestamp,
    created_at        timestamp default now()             not null,
    updated_at        timestamp default now()             not null
);

alter table public.game_mode
    owner to "user";

create table public.player_perspective
(
    player_perspective_id uuid      default gen_random_uuid() not null
        primary key,
    source_ref            bigint                              not null
        unique,
    name                  text                                not null,
    slug                  text                                not null,
    created_at_source     timestamp,
    updated_at_source     timestamp,
    created_at            timestamp default now()             not null,
    updated_at            timestamp default now()             not null
);

alter table public.player_perspective
    owner to "user";

create table public.keyword
(
    keyword_id        uuid      default gen_random_uuid() not null
        primary key,
    source_ref        bigint                              not null
        unique,
    name              text                                not null,
    slug              text                                not null,
    created_at_source timestamp,
    updated_at_source timestamp,
    created_at        timestamp default now()             not null,
    updated_at        timestamp default now()             not null
);

alter table public.keyword
    owner to "user";

create table public.collection
(
    collection_id     uuid      default gen_random_uuid() not null
        primary key,
    source_ref        bigint                              not null
        unique,
    name              text                                not null,
    slug              text                                not null,
    created_at_source timestamp,
    updated_at_source timestamp,
    created_at        timestamp default now()             not null,
    updated_at        timestamp default now()             not null
);

alter table public.collection
    owner to "user";

create table public.franchise
(
    franchise_id      uuid      default gen_random_uuid() not null
        primary key,
    source_ref        bigint                              not null
        unique,
    name              text                                not null,
    slug              text                                not null,
    created_at_source timestamp,
    updated_at_source timestamp,
    created_at        timestamp default now()             not null,
    updated_at        timestamp default now()             not null
);

alter table public.franchise
    owner to "user";

create table public.company
(
    company_id        uuid      default gen_random_uuid() not null
        primary key,
    source_ref        bigint                              not null
        unique,
    name              text                                not null,
    country           smallint,
    description       text,
    logo_asset_id     uuid,
    created_at_source timestamp,
    updated_at_source timestamp,
    created_at        timestamp default now()             not null,
    updated_at        timestamp default now()             not null
);

alter table public.company
    owner to "user";

create table public.game
(
    game_id                     uuid      default gen_random_uuid() not null
        primary key,
    source_ref                  bigint                              not null
        unique,
    slug                        text                                not null
        unique,
    title                       text                                not null,
    summary                     text,
    storyline                   text,
    category_code               smallint,
    status_code                 smallint,
    first_release_date          date,
    total_rating                numeric(5, 2),
    total_rating_count          integer,
    aggregated_rating           numeric(5, 2),
    aggregated_rating_count     integer,
    popularity                  numeric(9, 2),
    parent_game_id              uuid
                                                                    references public.game
                                                                        on delete set null,
    created_at_source           timestamp,
    updated_at_source           timestamp,
    ingestion_timestamp         timestamp default now()             not null,
    active                      boolean   default true              not null,
    steam_app_id                integer,
    steam_supports_achievements boolean,
    steam_has_stats             boolean,
    steam_last_enriched_at      timestamp,
    popularity_composite        numeric(5, 4),
    updated_at                  timestamp default now()             not null,
    created_at                  timestamp default now()             not null
);

alter table public.game
    owner to "user";

create index idx_game_source_ref
    on public.game (source_ref);

create index idx_game_first_release_date
    on public.game (first_release_date);

create index idx_game_popularity
    on public.game (popularity desc);

create index idx_game_popularity_composite
    on public.game (popularity_composite desc);

create index idx_game_parent_game_id
    on public.game (parent_game_id);

create index idx_game_category_code
    on public.game (category_code);

create index idx_game_status_code
    on public.game (status_code);

create trigger trg_game_touch
    before update
    on public.game
    for each row
execute procedure public.touch_updated_at();

create table public.game_genre
(
    game_id  uuid not null
        references public.game
            on delete cascade,
    genre_id uuid not null
        references public.genre
            on delete cascade,
    primary key (game_id, genre_id)
);

alter table public.game_genre
    owner to "user";

create table public.game_theme
(
    game_id  uuid not null
        references public.game
            on delete cascade,
    theme_id uuid not null
        references public.theme
            on delete cascade,
    primary key (game_id, theme_id)
);

alter table public.game_theme
    owner to "user";

create table public.game_keyword
(
    game_id    uuid not null
        references public.game
            on delete cascade,
    keyword_id uuid not null
        references public.keyword
            on delete cascade,
    primary key (game_id, keyword_id)
);

alter table public.game_keyword
    owner to "user";

create table public.game_mode_link
(
    game_id      uuid not null
        references public.game
            on delete cascade,
    game_mode_id uuid not null
        references public.game_mode
            on delete cascade,
    primary key (game_id, game_mode_id)
);

alter table public.game_mode_link
    owner to "user";

create table public.game_player_perspective
(
    game_id               uuid not null
        references public.game
            on delete cascade,
    player_perspective_id uuid not null
        references public.player_perspective
            on delete cascade,
    primary key (game_id, player_perspective_id)
);

alter table public.game_player_perspective
    owner to "user";

create table public.game_franchise
(
    game_id      uuid not null
        references public.game
            on delete cascade,
    franchise_id uuid not null
        references public.franchise
            on delete cascade,
    primary key (game_id, franchise_id)
);

alter table public.game_franchise
    owner to "user";

create table public.involved_company
(
    involved_company_id uuid      default gen_random_uuid() not null
        primary key,
    source_ref          bigint                              not null
        unique,
    game_id             uuid                                not null
        references public.game
            on delete cascade,
    company_id          uuid                                not null
        references public.company
            on delete cascade,
    developer           boolean   default false             not null,
    publisher           boolean   default false             not null,
    porting             boolean   default false             not null,
    supporting          boolean   default false             not null,
    created_at_source   timestamp,
    updated_at_source   timestamp,
    created_at          timestamp default now()             not null,
    updated_at          timestamp default now()             not null
);

alter table public.involved_company
    owner to "user";

create index idx_involved_company_game
    on public.involved_company (game_id);

create index idx_involved_company_company
    on public.involved_company (company_id);

create table public.release_date
(
    release_date_id   uuid      default gen_random_uuid() not null
        primary key,
    source_ref        bigint                              not null
        unique,
    game_id           uuid                                not null
        references public.game
            on delete cascade,
    platform_id       uuid                                not null
        references public.platform
            on delete cascade,
    date              date,
    region_code       smallint,
    category_code     smallint,
    status_code       smallint,
    created_at_source timestamp,
    updated_at_source timestamp,
    created_at        timestamp default now()             not null,
    updated_at        timestamp default now()             not null,
    unique (game_id, platform_id, date, region_code)
);

alter table public.release_date
    owner to "user";

create index idx_release_date_game
    on public.release_date (game_id);

create table public.age_rating
(
    age_rating_id     uuid      default gen_random_uuid() not null
        primary key,
    source_ref        bigint                              not null
        unique,
    organization_code smallint,
    rating_code       smallint,
    synopsis          text,
    created_at_source timestamp,
    updated_at_source timestamp,
    created_at        timestamp default now()             not null,
    updated_at        timestamp default now()             not null
);

alter table public.age_rating
    owner to "user";

create table public.media_asset
(
    asset_id          uuid      default gen_random_uuid() not null
        primary key,
    source_ref        bigint                              not null
        unique,
    game_id           uuid
        references public.game
            on delete cascade,
    type              text                                not null,
    width             integer,
    height            integer,
    url_original      text                                not null,
    checksum          text,
    created_at_source timestamp,
    updated_at_source timestamp,
    created_at        timestamp default now()             not null,
    updated_at        timestamp default now()             not null
);

alter table public.media_asset
    owner to "user";

create index idx_media_asset_game
    on public.media_asset (game_id);

create index idx_media_asset_type
    on public.media_asset (type);

create index idx_media_asset_game_id
    on public.media_asset (game_id);

create table public.game_achievement
(
    game_achievement_id    uuid      default gen_random_uuid() not null
        primary key,
    source_ref             bigint                              not null
        unique,
    game_id                uuid                                not null
        references public.game
            on delete cascade,
    name                   text                                not null,
    slug                   text                                not null,
    description            text,
    category_code          smallint,
    order_index            integer,
    points                 integer   default 0,
    secret                 boolean   default false             not null,
    unlocked_icon_asset_id uuid
                                                               references public.media_asset
                                                                   on delete set null,
    locked_icon_asset_id   uuid
                                                               references public.media_asset
                                                                   on delete set null,
    created_at_source      timestamp,
    updated_at_source      timestamp,
    checksum               text,
    created_at             timestamp default now()             not null,
    updated_at             timestamp default now()             not null
);

alter table public.game_achievement
    owner to "user";

create index idx_game_achievement_game
    on public.game_achievement (game_id);

create index idx_game_achievement_order
    on public.game_achievement (game_id, order_index);

create trigger trg_game_achievement_touch
    before update
    on public.game_achievement
    for each row
execute procedure public.touch_updated_at();

create table public.game_alt_name
(
    game_alt_name_id  uuid      default gen_random_uuid() not null
        primary key,
    source_ref        bigint                              not null
        unique,
    game_id           uuid                                not null
        references public.game
            on delete cascade,
    name              text                                not null,
    comment           text,
    locale            text,
    created_at_source timestamp,
    updated_at_source timestamp,
    created_at        timestamp default now()             not null,
    updated_at        timestamp default now()             not null
);

alter table public.game_alt_name
    owner to "user";

create index idx_game_alt_name_game
    on public.game_alt_name (game_id);

create trigger trg_game_alt_name_touch
    before update
    on public.game_alt_name
    for each row
execute procedure public.touch_updated_at();

create table public.game_website
(
    game_website_id   uuid      default gen_random_uuid() not null
        primary key,
    source_ref        bigint                              not null
        unique,
    game_id           uuid                                not null
        references public.game
            on delete cascade,
    category_code     integer,
    url               text                                not null,
    trusted           boolean,
    checksum          text,
    created_at_source timestamp,
    updated_at_source timestamp,
    created_at        timestamp default now()             not null,
    updated_at        timestamp default now()             not null
);

alter table public.game_website
    owner to "user";

create index idx_game_website_game
    on public.game_website (game_id);

create trigger trg_game_website_touch
    before update
    on public.game_website
    for each row
execute procedure public.touch_updated_at();

create table public.game_video
(
    game_video_id     uuid      default gen_random_uuid() not null
        primary key,
    source_ref        bigint                              not null
        unique,
    game_id           uuid                                not null
        references public.game
            on delete cascade,
    name              text                                not null,
    video_id          text                                not null,
    platform          text,
    checksum          text,
    created_at_source timestamp,
    updated_at_source timestamp,
    created_at        timestamp default now()             not null,
    updated_at        timestamp default now()             not null
);

alter table public.game_video
    owner to "user";

create index idx_game_video_game
    on public.game_video (game_id);

create trigger trg_game_video_touch
    before update
    on public.game_video
    for each row
execute procedure public.touch_updated_at();

create table public.multiplayer_mode
(
    multiplayer_mode_id uuid      default gen_random_uuid() not null
        primary key,
    source_ref          bigint                              not null
        unique,
    game_id             uuid                                not null
        references public.game
            on delete cascade,
    campaign_coop       boolean,
    drop_in             boolean,
    lan_coop            boolean,
    offline_coop        boolean,
    offline_coop_max    smallint,
    offline_max         smallint,
    online_coop         boolean,
    online_coop_max     smallint,
    online_max          smallint,
    splitscreen         boolean,
    splitscreen_online  boolean,
    created_at_source   timestamp,
    updated_at_source   timestamp,
    created_at          timestamp default now()             not null,
    updated_at          timestamp default now()             not null
);

alter table public.multiplayer_mode
    owner to "user";

create index idx_multiplayer_mode_game
    on public.multiplayer_mode (game_id);

create trigger trg_multiplayer_mode_touch
    before update
    on public.multiplayer_mode
    for each row
execute procedure public.touch_updated_at();

create table public.game_language_support
(
    game_language_support_id uuid      default gen_random_uuid() not null
        primary key,
    source_ref               bigint                              not null
        unique,
    game_id                  uuid                                not null
        references public.game
            on delete cascade,
    language_code            integer                             not null,
    support_type_code        integer                             not null,
    created_at_source        timestamp,
    updated_at_source        timestamp,
    created_at               timestamp default now()             not null,
    updated_at               timestamp default now()             not null,
    constraint game_language_support_game_id_language_code_support_type_co_key
        unique (game_id, language_code, support_type_code)
);

alter table public.game_language_support
    owner to "user";

create index idx_game_language_support_game
    on public.game_language_support (game_id);

create trigger trg_game_language_support_touch
    before update
    on public.game_language_support
    for each row
execute procedure public.touch_updated_at();

create table public.ingestion_watermark
(
    entity     varchar(32)             not null,
    source     varchar(32)             not null,
    last_value bigint                  not null,
    updated_at timestamp default now() not null,
    primary key (entity, source)
);

alter table public.ingestion_watermark
    owner to "user";

create table public.steam_playtime_30d
(
    game_id            uuid                    not null
        primary key
        references public.game
            on delete cascade,
    recent_minutes_30d bigint                  not null
        constraint steam_playtime_30d_recent_minutes_30d_check
            check (recent_minutes_30d >= 0),
    updated_at         timestamp default now() not null
);

alter table public.steam_playtime_30d
    owner to "user";

create trigger trg_steam_playtime_touch
    before update
    on public.steam_playtime_30d
    for each row
execute procedure public.touch_updated_at_steam_playtime();

create table public.game_platform
(
    game_id     uuid not null
        constraint game_platforms_game_id_fkey
            references public.game
            on delete cascade,
    platform_id uuid not null
        constraint game_platforms_platform_id_fkey
            references public.platform
            on delete cascade,
    constraint game_platforms_pkey
        primary key (game_id, platform_id)
);

alter table public.game_platform
    owner to "user";

create table public.game_age_rating
(
    game_id       uuid                    not null
        references public.game
            on delete cascade,
    age_rating_id uuid                    not null
        references public.age_rating
            on delete cascade,
    created_at    timestamp default now() not null,
    primary key (game_id, age_rating_id)
);

alter table public.game_age_rating
    owner to "user";

create index idx_game_age_rating_age
    on public.game_age_rating (age_rating_id);

create table public.game_collection
(
    game_id       uuid                    not null
        references public.game
            on delete cascade,
    collection_id uuid                    not null
        references public.collection
            on delete cascade,
    created_at    timestamp default now() not null,
    primary key (game_id, collection_id)
);

alter table public.game_collection
    owner to "user";

create index idx_game_collection_collection
    on public.game_collection (collection_id);

