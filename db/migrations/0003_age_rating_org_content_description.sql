-- Migration: add age rating organization & content description v2 tables + link table
-- Rationale: support enrichment of age rating organizations and v2 content descriptions (multi-valued) from IGDB.

BEGIN;

create table if not exists public.age_rating_organization (
    age_rating_organization_id uuid default gen_random_uuid() primary key,
    source_ref        bigint not null unique,
    name              text   not null,
    checksum          text,
    created_at_source timestamp,
    updated_at_source timestamp,
    created_at        timestamp default now() not null,
    updated_at        timestamp default now() not null
);

create table if not exists public.age_rating_content_description (
    age_rating_content_description_id uuid default gen_random_uuid() primary key,
    source_ref        bigint not null unique,
    organization_id   uuid references public.age_rating_organization on delete set null,
    description       text,
    description_type  bigint,
    checksum          text,
    created_at_source timestamp,
    updated_at_source timestamp,
    created_at        timestamp default now() not null,
    updated_at        timestamp default now() not null
);

create table if not exists public.age_rating_content_description_link (
    age_rating_id uuid not null references public.age_rating on delete cascade,
    age_rating_content_description_id uuid not null references public.age_rating_content_description on delete cascade,
    created_at    timestamp default now() not null,
    primary key (age_rating_id, age_rating_content_description_id)
);

COMMIT;

