# Security Policy

## 1. Reporting a Vulnerability
Report privately to the sole maintainer:
- Email: andersonfilho09@gmail.com
- Optional PGP: (to be added)

Do NOT create a public issue for vulnerabilities.

## 2. Disclosure Timeline (Targets)
| Phase | Target SLA |
|-------|-----------|
| Acknowledge report | ≤ 48 hours |
| Initial triage / severity classification | ≤ 5 business days |
| Fix or mitigation for High/Critical | ≤ 30 days |
| Fix or mitigation for Medium | ≤ 60 days |
| Fix or plan for Low | ≤ 90 days |

If a deadline cannot be met, we will provide status updates.

## 3. Scope
In scope (current / planned components):
- API Gateway (Go)
- User & Game Services (Java)
- Recommendation Service (Python)
- Messaging envelope & ingestion pipeline (Kafka / RabbitMQ)

Out of scope:
- Third-party platforms (Steam, console APIs) except how we store their returned data
- User browsers / devices (client vulnerabilities outside our code)

## 4. Accepted Vulnerability Types
- Authentication / authorization flaws
- Data exposure (PII, tokens)
- Injection (SQL, command, template)
- Deserialization, RCE
- SSRF, CSRF, XSS
- Privilege escalation
- Broken access control on endpoints or messages

## 5. Non-Qualifying Issues (Examples)
- Missing security headers with low practical impact
- Rate limiting absence (unless it leads to proven exploit)
- Use of deprecated libraries without demonstrated exploit
- Best practice suggestions without tangible risk

## 6. Coordinated Disclosure
We prefer coordinated disclosure; do not publicly share details until a fix is released or 90 days have passed (whichever earlier), unless mutually agreed otherwise.

## 7. Supported Versions
| Version | Supported | Notes |
|---------|-----------|-------|
| 0.1.0 (current) | Yes | Initial public documentation baseline |

Future: Intend to support N (latest) and N-1 minor versions for 90 days post new minor release.

## 8. Vulnerability Handling Workflow
1. Receipt & acknowledgment
2. Triage: severity (CVSS preliminary)
3. Reproduce & log internally (private tracker)
4. Patch development + code review (security focus)
5. Regression + security tests
6. Release (tag & changelog Security section)
7. Credit reporter (if desired & safe)

## 9. Hardening Baseline (Planned)
- Dependency scanning (SCA)
- Static code analysis (SAST)
- Container image vulnerability scanning
- Infrastructure as Code scanning
- Secret scanning (pre-commit + CI)
- SBOM generation per release

## 10. Contact & Emergency
If you believe data is actively being exploited, use subject: **URGENT SECURITY** and describe observed indicators.

## 11. Data Protection
- Least privilege (service accounts scoped per service)
- Encryption in transit (TLS >=1.2)
- Encryption at rest (database & backups) planned
- Token revocation & audit logging roadmap

## 12. Severity & Scoring
We use CVSS v3.1 base scoring to prioritize remediation. High (7.0–8.9) and Critical (9.0–10.0) receive expedited handling. Contextual/environmental adjustments may be applied for internal-only components.

## 13. Future Improvements
- Bug bounty scope definition
- PGP key publication
- Automated vulnerability disclosure (security.txt)
