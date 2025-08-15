# Changelog
Format based on Keep a Changelog and SemVer.

## [Unreleased]
### Added
- Metadata (Status/Decision) blocks standardized in strategic analysis and load testing plan.
- IGDB, Steam, PSN, Xbox source mapping documents (docs/domain/*-source-mapping.md).
- Unified Game ETL design (docs/domain/game-etl-igdb-steam.md).
- Migration 0002_game_schema.sql (core catalog tables, enrichment columns, external_reference table,achievements, alt names, websites, videos, multiplayer modes, language support link).
- Migration 0003_seed_enums.sql (enum seeds for GameCategory, ReleaseStatus, PlatformCategory, Region, AgeRatingOrg, AchievementCategory, WebsiteCategory, ExternalRefCategory, LanguageSupportType).
- Migration 0004_ingestion_foundations.sql (ingestion_watermark + upsert function + role scaffolding).
- Migration 0005_steam_playtime.sql (steam_playtime_30d table + upsert function + trigger).
- ER Diagram game-catalog (docs/architecture/diagrams/game-catalog-er.md).
- Runbook Game Ingestion ETL (docs/operations/runbooks/game-ingestion-etl.md).
- Popularity composite SQL helper (scripts/popularity_composite.sql).
- Service & ETL Implementation Plan (docs/roadmap/service-etl-implementation-plan.md) v0.1.1 (Solo Addendum + tasks status).
- Makefile (db.migrate, db.smoke, db.setup, docs.validate) and smoke script (scripts/db_smoke.sql).
- IGDB ETL worker skeleton (services/igdb-etl) with incremental loop + watermark handling.
- example.env updated with IGDB_* variables and poll/batch configs.
- docker-compose.yml updated with igdb-etl service.
- GitHub Issue Templates (.github/ISSUE_TEMPLATE/*) for bug, feature, epic, task, tech debt, security hardening, docs.
- Service Implementation Backlog (docs/roadmap/service-implementation-backlog.md) seeding epics & tasks.
### Changed
- README: project status updated to v0.1.0 and disclaimer note on metadata standardization.
- SECURITY: supported versions table corrected to reflect 0.1.0 initial baseline.
- Strategic Analysis: added Status/Decision + revision history.
- Load Testing Plan: restored Status/Decision block.
### Deprecated
### Removed
### Fixed
- Minor formatting inconsistencies in docs (metadata spacing, consistency).
### Security
### Maintenance
- Documentation consistency pass (initial metadata standardization sweep).

## [0.1.0] - 2025-08-15
### Added
- Initial documentation set (ADRs 0001–0003, governance policies, operation docs, strategic analysis, messaging envelope, glossary, security policies, runbooks, load testing plan, SLOs).

[Unreleased]: https://github.com/AndersonGACFilho/Game-Insight/compare/v0.1.0...HEAD
[0.1.0]: https://github.com/AndersonGACFilho/Game-Insight/releases/tag/v0.1.0
