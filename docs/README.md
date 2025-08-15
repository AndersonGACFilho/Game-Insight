# Documentation Index

Title: Documentation Index <br>
Version: 0.1.2 <br>
Last Updated: 2025-08-15 <br>
Owner: Anderson (Sole Maintainer) <andersonfilho09@gmail.com> <br>
Status: Accepted <br>
Decision: Added domain mapping & ETL references <br>

## 1. Architecture & Strategy
- Strategic Analysis: architecture/strategic-analysis.md
- Diagrams: architecture/diagrams/
  - Game Catalog ER: architecture/diagrams/game-catalog-er.md
- ADR Index: adr/
  - 0001 Backend Language Selection
  - 0002 Messaging Strategy
  - 0003 Versioning Policy

## 2. Governance & Process
- Branching Policy: governance/branching-policy.md
- Data Classification: governance/data-classification.md
- Security Policy: security-policy.md
- Threats & Mitigations: governance/security-threats.md
- Code of Conduct:./CODE_OF_CONDUCT.md
- Contributing Guide:./CONTRIBUTING.md

## 3. Messaging & Contracts
- Envelope Standard: messaging/envelope.md
- Versioning Policy (ADR 0003): adr/0003-versioning-policy.md

## 4. Operations & Reliability
- Observability: operations/observability.md
- SLOs: operations/slo.md
- Incident Response Runbook: operations/runbooks/incident-response.md
- Game Ingestion ETL Runbook: operations/runbooks/game-ingestion-etl.md
- Load Testing Plan: operations/testing/load-testing-plan.md

## 5. Security
- Security Policy (root):./SECURITY.md
- Threat Model: governance/security-threats.md
- Data Classification: governance/data-classification.md

## 6. Domain & Reference
- Game Service Data Model: domain/game-data-model.md (placeholder)
- IGDB Source Mapping: domain/igdb-source-mapping.md
- Steam Source Mapping: domain/steam-source-mapping.md
- PSN Source Mapping: domain/psn-source-mapping.md
- Xbox Source Mapping: domain/xbox-source-mapping.md
- Unified Game ETL (IGDB + Steam): domain/game-etl-igdb-steam.md
- Game Catalog ER Diagram: architecture/diagrams/game-catalog-er.md
- Popularity Composite SQL: ../scripts/popularity_composite.sql
- Service & ETL Implementation Plan: roadmap/service-etl-implementation-plan.md
- Service Implementation Backlog (Phase 1): roadmap/service-implementation-backlog.md
- Glossary: glossary.md
- Changelog: ../CHANGELOG.md

## 7. Future Placeholders
- API Reference: api/ (to add)
- Model Cards: ml/models/
- Feature Store Docs: data/feature-store/

## 8. Conventions
- All governance & domain docs include metadata header (Title, Version, Last Updated, Owner, Status).
- SemVer for docs where material to process (major breaking changes flagged in CHANGELOG Added/Changed sections).

## 9. Compliance & Data Scope
- Active Data Sources: IGDB (official API) + Steam Web API public endpoints only.
- Design-Only Sources: PSN and Xbox mappings are architectural references; no ingestion, storage, or processing of console user/presence/trophy/achievement data occurs.
- Prohibited Activities: No scraping of authenticated pages, credential harvesting, reverse engineering non-public endpoints, or use of user passwords/session cookies.
- Synthetic Data: Any console feature demonstrations must use synthetic or mocked datasets behind feature flags.
- Future Expansion: Requires explicit written partner agreements and renewed legal/privacy review prior to enabling console or additional store integrations.

## 10. Updating This Index
Run (future) script: `scripts/update-doc-index.sh` (TBD) to auto-refresh ADR & domain lists.

## Revision History
- v0.1.2 (2025-08-15): Added Compliance & Data Scope section.
- v0.1.1 (2025-08-15): Added source mappings (IGDB/Steam/PSN/Xbox), ETL design, ER diagram, runbook & popularity script references.
- v0.1.0 (2025-08-15): Initial version.