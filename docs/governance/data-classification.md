# Data Classification

Title: Data Classification Policy <br>
Version: 0.1.0 <br>
Last Updated: 2025-08-15 <br>
Owner: Anderson (Sole Maintainer) <andersonfilho09@gmail.com> <br>
Status: Accepted <br>
Decision: Final <br>

## Classes
| Class | Description | Examples | Controls | Legal Basis / Justification |
|-------|-------------|----------|----------|-----------------------------|
| PII | Directly identifies a user | email, external id | Encryption (at rest + in transit), restricted RBAC, access logging | Legitimate interest / consent (TBD) |
| Security Sensitive | Credentials or tokens enabling access | API tokens, refresh tokens, OAuth secrets | HSM / vault storage, strict RBAC, rotation ≤ 90d, no logs | Contractual necessity / security obligation |
| Pseudonymous | Needs separate key | internal user_id, hashes | Segregated mapping table, rotate mapping key, limited access | Minimization principle |
| Behavioral | Interaction, playtime | hours played, achievements | Aggregation, minimization, retention limits | Analytics (service improvement) |
| Aggregated / Anonymous | Irreversible statistics | global metrics | Public sharing allowed, extended retention | Not personal data |

## Retention
- Raw ETL: 30 days (then normalized or deleted)
- Normalized: rolling 24 months (review annually)
- Aggregated: indefinite (non-reversible)
- Security Sensitive: rotate ≤ 90 days; revoke immediately on compromise

## Controls (Overview)
- Encryption at rest: all PII, Pseudonymous, Security Sensitive in Postgres/object storage (pgcrypto / TDE / SSE).
- Encryption in transit: TLS ≥1.2, SASL for Kafka (future phase).
- Access: role-based; the least privilege; quarterly review (Security-Sensitive monthly review).
- Logging: deny PII & Security Sensitive in logs (lint rule planned) — only hashed identifiers.
- Audits: quarterly automated sample and annual manual review; Security-Sensitive access monthly.

## Rules
- Do not place PII- or Security-Sensitive data in logs.
- Anonymize before external analytics.
- Annual classification review + on adding a new source.
- New data element requires classification before merge (PR checklist).

## Deletion Requests
1. Locate PII records.
2. Remove identifiers (or pseudonymize irreversibly).
3. Invalidate refresh tokens / sessions (Security-Sensitive cleanup).
4. Mark aggregates as non-traceable (no action if already irreversible).
5. Record fulfillment timestamp (audit log).

## Metrics
- Deletion requests fulfilled < 30 days.
- % events without improper PII in audit: 100%.
- Access reviews executed on schedule: 100%.
- Credential rotation on time: ≥ 98%.

## Review Cadence
- Quarterly: access lists, pseudonymous mapping rotation, audit sampling.
- Annual: classification scheme relevance, retention justification.

## Open Questions
- Introduce differential privacy for aggregated export?
- Need for per-field lineage tracking in Phase 2?

## Revision History
- v0.1.0 (2025-08-15): Initial version.
