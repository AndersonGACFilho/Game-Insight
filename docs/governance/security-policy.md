# Security Policy

Title: Internal Security Operations Policy <br>
Version: 0.1.0 <br>
Last Updated: 2025-08-15 <br>
Owner: Anderson (Sole Maintainer) <andersonfilho09@gmail.com> <br>
Status: Accepted <br>
Decision: Final <br>

## Objectives
Reduce the risk of data leakage (PII) and integrity compromise.

## Scope
Code, infrastructure, data at rest, internal traffic, supply chain.

## Principles
- Least privilege
- Ephemeral secrets
- Automated verification

## Secrets
- Local: `.env` dev only, never committed
- Prod: Vault / secrets manager
- Rotation: every 90 days or on incident

## Dependency Management
- Renovate (daily PR)
- SCA: Trivy / Grype
- Block builds with high/critical CVEs (CI gate)

## Scanning
- SAST: Semgrep
- SCA: Trivy (containers + libraries)
- IaC: Tfsec / Checkov
- Images: Hardened base, weekly rebuild

## Hardening
- Minimal images (no unnecessary tools)
- Non-root container user
- Internal TLS (mTLS future)

## Secure Logging
- Do not log tokens, emails; mask PII
- Sensitive fields: `[REDACTED]`

## Incidents
See `../operations/runbooks/incident-response.md`

## Metrics
- % builds with completed scans: 100%
- the mean time to remediate critical CVE: < 7 days
- Exposed secrets detected: 0

## Future
- Service-level threat modeling
- Pre-public-release pentest
- Automated secret rotation hooks

## Revision History
- v0.1.0 (2025-08-15): Initial version.
