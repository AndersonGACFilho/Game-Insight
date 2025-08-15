# ADR 0003: Versioning Policy (APIs, Schemas, Services, Models)

Title: Versioning Policy (APIs, Schemas, Services, Models) <br>
Version: 0.1.0 <br>
Last Updated: 2025-08-15 <br>
Owner: Anderson (Sole Maintainer) <andersonfilho09@gmail.com> <br>
Status: Accepted <br>
Decision: Final <br>

## Context
A unified versioning and release policy is required to:
- Reduce risk of breaking changes across APIs, messaging schemas, ML models, infrastructure templates.
- Standardize traceability between code, deployed artifacts, telemetry, and incident response.
- Enable CI gates (fail fast on incompatible schema or contract changes).
- Support the polyglot stack (ADR 0001) and dual messaging backbone (ADR 0002).

## Problem
Without explicit rules:
- Silent client breakages (removed or renamed fields).
- Avro / JSON schemas drift across environments.
- Slow or unsafe rollback (unclear artifact identity).
- ML model updates alter feature contracts unnoticed by services.

## Decision
Adopt Semantic Versioning (SemVer) `MAJOR.MINOR.PATCH` across artifact types with tailored rules:
1. HTTP APIs: Major version in path (`/v1/...`). Breaking change => new major; old major maintained for a deprecation window.
2. Kafka (Avro) Schemas: Backward (and ideally forward) compatible evolution; optional fields with defaults => MINOR.
3. RabbitMQ (JSON) Schemas: `schema_version` in envelope; producers add only backward compatible changes in MINOR.
4. Service Images: Tag `<service>:<apiMajor>.<serviceMinor>.<build>` (never use `latest` in production).
5. Database Migrations: Immutable files `YYYYMMDDHHMM__desc.sql`; rollback via forward fix migrations.
6. ML Models: `model_version` (SemVer) + metadata (`feature_store_snapshot_id`, `data_version` digest, hyperparams).
7. Infra / Pipelines (Terraform modules, reusable CI templates): SemVer; breaking includes destructive reprovision or mandatory interface change.
8. Feature Flags: Code paths guarded by a disabled-by-default flag DO NOT count as a breaking change until the flag is enabled for >95% of traffic OR the old path is removed. Removal of the old behavior behind a flag = MAJOR if externally observable.

## Artifact Identity Mapping
| Dimension | Source of Truth | Example | Propagated Field |
|-----------|-----------------|---------|------------------|
| service_version | Image tag / deployment manifest | user-svc:1.4.2 | logs/traces metrics |
| api_version | HTTP path segment | /v1/users | logs/traces |
| schema_version | Envelope / Avro registry | 1.2.0 | envelope/logs |
| model_version | Model registry metadata | rec-model:2.3.1 | inference metrics |
| infra_module_version | Terraform module tag | network-v1.5.0 | infra drift reports |

## Scope
- Public and internal HTTP APIs.
- Asynchronous command payloads (RabbitMQ) and ingestion / event stream schemas (Kafka).
- Container images, database migration sets.
- ML model artifacts and feature contracts.
- Shared IaC modules and CI pipeline templates.

## Evolution Rules (Summary Table)
| Action | Version Type |
|--------|--------------|
| Add optional field (with default if Avro) | MINOR |
| Add new endpoint / command | MINOR |
| Performance optimization (no contract change) | PATCH |
| Bug fix (no external contract change) | PATCH |
| Remove field / endpoint | MAJOR |
| Rename field | MAJOR |
| Change field type / semantics | MAJOR |
| Add required field | MAJOR |
| New compatible ML model (same input schema) | MINOR |
| Change required event field format | MAJOR |
| Remove legacy flag-protected path | MAJOR (if external) |

## Compatibility Guarantees
| Artifact | Within Same MAJOR |
|----------|-------------------|
| HTTP API | Backward compatible (existing clients continue working) |
| Avro Schema | Backward; forward if only MINOR additions with defaults |
| JSON Command | Backward; consumers ignore unknown fields |
| DB Schema | Additive migrations; no destructive change without MAJOR |
| ML Model | Does not change input feature contract; otherwise MAJOR for depending API |
| Infra Module | No forced destructive apply |

## Release Process
1. Merge to `main` triggers pipeline: build → lint → tests → contract tests (OpenAPI / schema diff) → security scan.
2. On approval create git tag `vX.Y.Z`.
3. Generate and publish changelog entry (root `CHANGELOG.md` + service section).
4. Canary deploy (5–10% traffic) with comparative SLO metrics (latency p95, error rate, resource usage).
5. Automatic or manual promotion once guard metrics pass thresholds.
6. Record deployment (version + environment) in release ledger (e.g., artifact registry annotation).

## CI Gates (Fail Conditions)
- Avro breaking change without MAJOR bump.
- Removed or renamed HTTP field / endpoint without prior deprecation metadata.
- JSON schema required field added without MAJOR bump.
- Model registered without model card and metadata.
- OpenAPI changed but docs / version not updated.
- Missing changelog entry for new tag.
- Flag removal altering response shape without MAJOR bump.

## Deprecation Policy
1. Mark in changelog under Deprecated.
2. Emit `Deprecation` and `Sunset` headers (HTTP) where applicable.
3. Track usage metrics (requests per endpoint / field, event consumer lag).
4. Removal only after:
    - Deprecation window: ≥ 2 MINOR cycles or 90 days (whichever longer), and
    - Usage below threshold (e.g., < 5% of calls over rolling 14-day mean).
5. Document final removal in Major release notes.

## Observability
Include version dimensions:
- Logs: `service_version`, `api_version`, `schema_version`, `model_version`.
- Traces: attributes with same keys.
- Metrics: `deployment_info{service,version}` plus per-model inference metrics.
- Alert routing: version-aware to accelerate rollback.

## Rollback Strategy
- Keep at least 2 prior deployable image versions warm.
- DB: prefer additive forward fixes; avoid editing historical migrations.
- Model rollback: atomic pointer update in model registry / feature store.
- Canary anomalies trigger automated rollback if error budget burn > defined threshold or latency regression > X%.

## Metrics (Policy Effectiveness)
| Metric | Target |
|--------|--------|
| Releases with complete changelog | 100% |
| Production contract break incidents | 0 |
| Mean service rollback time | < 10 min |
| Mean model rollback time | < 2 min |
| CI gate false negative rate | < 5% |
| CI gate false positive rate | < 10% |
| % telemetry spans with version tags | ≥ 99% |

## Adoption Plan
1. Phase 1: Implement Avro & OpenAPI diff tooling; enforce image tag format; add version fields to structured logging.
2. Phase 2: Add model registry integration + automated model card check; telemetry dashboards with version filters.
3. Phase 3: Quarterly automated compliance audit (sample endpoints, schemas, models) + report.

## Risks & Mitigations
| Risk | Mitigation |
|------|------------|
| Overuse of MAJOR bumps | Review checklist + PR template guidance |
| Skipped changelog updates | Mandatory CI gate |
| Hidden schema drift (env mismatch) | Promotion pipeline enforces registry promotion sequence |
| Model incompatibility discovered late | Pre-deploy contract test (feature presence / dtype) |
| Canary noise leading to false rollback | Statistical comparison window & minimum traffic threshold |

## Alternatives Considered
1. Calendar Versioning: Simpler temporal ordering; lacks explicit signaling of breaking changes.
2. Single global repository hash: Precise but opaque externally; does not capture per-artifact compatibility nuances.

## Consequences (Positive)
- Predictable integration for internal and future external consumers.
- Reduced runtime incidents from uncoordinated changes.
- Faster incident triage (version dimensions in telemetry).
- Foundation for automated diff-based governance.

## Consequences (Negative / Costs)
- Initial tooling and pipeline overhead (schema diff, gates).
- Discipline required for consistent changelog maintenance.
- Possible friction (gate failures) during early adoption.

## Acceptance Criteria
- The First 3 releases post-adoption have valid tags, changelog entries, and versioned telemetry.
- CI fails on intentional test breaking scenarios (simulated schema removal).
- The Dashboard shows per-version latency and error rate.

## Review
Scheduled 60-day post-adoption review to adjust gating thresholds, false positive handling, and deprecation window length.

## References
- ADR 0001 (Backend Language Selection).
- ADR 0002 (Messaging Strategy).
- Semantic Versioning 2.0.0.
- Keep a Changelog.

## Revision History
- v0.1.0 (2025-08-15): Initial version.