# Documentation Index

Title: Documentation Index <br>
Version: 0.1.0 <br>
Last Updated: 2025-08-15 <br>
Owner: Anderson (Sole Maintainer) <andersonfilho09@gmail.com> <br>
Status: Accepted <br>
Decision: Final <br>

## 1. Architecture & Strategy
- Strategic Analysis: architecture/strategic-analysis.md
- Diagrams: architecture/diagrams/
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
- Load Testing Plan: operations/testing/load-testing-plan.md

## 5. Security
- Security Policy (root):./SECURITY.md
- Threat Model: governance/security-threats.md
- Data Classification: governance/data-classification.md

## 6. Glossary & Reference
- Glossary: glossary.md
- Changelog: ../CHANGELOG.md

## 7. Future Placeholders
- API Reference: api/ (to add)
- Model Cards: ml/models/
- Feature Store Docs: data/feature-store/

## 8. Conventions
- All governance docs include metadata header (Title, Version, Last Updated, Owner, Status).
- SemVer for docs where material to process (major breaking changes flagged in CHANGELOG Security or Added section).

## 9. Updating This Index
Run (future) script: `scripts/update-doc-index.sh` to an auto-refresh ADR list.

## Revision History
- v0.1.0 (2025-08-15): Initial version.