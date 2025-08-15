# Threat Model (Initial)

Title: Security Threat Model (Initial STRIDE) <br>
Version: 0.1.0 <br>
Last Updated: 2025-08-15 <br>
Owner: Anderson (Sole Maintainer) <andersonfilho09@gmail.com> <br>
Status: Accepted <br>
Decision: Final <br>

## Scope
API Gateway, Core Services (User, Game), Recommendation Service, Messaging (RabbitMQ/Kafka), PostgreSQL, Ingestion ETL.

## Methodology
High-level STRIDE enumeration and baseline mitigations. Detailed per-service models to follow in later phases.

## Assets
- User PII & pseudonymous identifiers
- API tokens / refresh tokens
- Recommendation model artifacts & feature vectors
- Message integrity (commands and ingestion events)
- Access & audit logs

## Trust Boundaries
1. External clients ↔ API Gateway
2. API Gateway ↔ Internal services
3. Services ↔ Datastore (Postgres)
4. Services ↔ Messaging (Kafka/RabbitMQ)
5. ETL ↔ External platform APIs (Steam)
6. CI/CD ↔ Runtime cluster

## STRIDE Summary
| Category | Example Threat | Impact | Likelihood (Initial) | Mitigations (Current/Planned) |
|----------|----------------|--------|----------------------|-------------------------------|
| Spoofing | Stolen API key used by attacker | Account takeover | Medium | API key rotation, future OAuth, rate limits |
| Tampering | Message payload altered in transit | Corrupted state / model drift | Low | TLS in transit, envelope schema validation, checksum (future) |
| Repudiation | User denies action (profile rebuild) | Disputed support cases | Medium | Structured logs with trace_id, signed audit (future) |
| Information Disclosure | PII leakage via logs or DLQ | Privacy breach / compliance | Medium | Log redaction, DLQ access RBAC, classification policy |
| Denial of Service | Flood of recommendation requests | Latency SLO breach | Medium | Rate limiting vars (planned), autoscaling, circuit breakers (future) |
| Elevation of Privilege | Service account misuse for admin actions | Lateral movement | Low | Least privilege, unique service creds, secret rotation |
| Model Extraction | Repeated inference queries to clone model behavior | IP loss | Low | Request pattern anomaly detection (future), throttling |
| Data Poisoning | Malformed ingestion events alter model training | Degraded recommendations | Medium | Schema + value validation, anomaly detection (future) |
| Replay | Re-sent old rebuild command causing inconsistency | Stale overwrite | Low | Idempotency keys, occurred_at freshness checks |
| Supply Chain | Malicious dependency update | Compromise at build | Medium | SCA scanning, pin versions, SBOM, provenance attestation (future) |

## Detailed Mitigation Notes
- Idempotency: envelope.idempotency_key required for commands prevents duplicate side-effects.
- Version Tags: service_version/model_version enable targeted rollback if anomaly detected.
- DLQ Workflow: triage process stops poisoned messages from silent retries.
- Secret Handling: .env only for local dev; production via vault (policy file).

## Gaps / Future Enhancements
| Gap | Planned Action | Target Version |
|-----|----------------|----------------|
| Missing mutual TLS internally | Evaluate SPIRE / mTLS sidecar | >= 0.3 |
| Lack of anomaly detection for ingestion | Introduce statistical validators | >= 0.4 |
| No signed envelopes | Add detached signature field | >= 0.5 |
| No automated threat review cadence | Quarterly threat review meeting | >= 0.2 |
| Missing dependency provenance | Integrate SLSA build attestations | >= 0.6 |

## Metrics
- % high severity gaps with owner & deadline: 100%
- Mean time to remediate new critical CVE: < 7 days (align SECURITY.md)
- DLQ poisoning incidents reaching production: 0

## Review Cadence
Quarterly or on major architecture change (new external platform, new data store, major ML pipeline change).

## References
- SECURITY.md
- governance/data-classification.md
- messaging/envelope.md
- adr/0002-messaging-architecture.md

## Revision History
- v0.1.0 (2025-08-15): Initial STRIDE summary threat model.
