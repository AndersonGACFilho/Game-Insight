# Runbook: Incident Response

Title: Incident Response Runbook <br>
Version: 0.1.0 <br>
Last Updated: 2025-08-15 <br>
Owner: Anderson (Sole Maintainer) <andersonfilho09@gmail.com> <br>
Status: Accepted <br>
Decision: Final <br>

## Severity Classification
- SEV1: total outage / data loss or exposure
- SEV2: severe degradation (latency p95 > 3x SLO, ingestion halted)
- SEV3: partial impact (non-critical service degraded)
- SEV4: minor issues / low-impact bugs

## Roles
- Incident Commander (IC)
- Service Engineer
- Observability / Infra
- Communications / Stakeholders

### RACI (Simplified)
| Task | IC | Service Eng | Observability | Comms |
|------|----|-------------|--------------|-------|
| Declare severity | A/R | C | C | I |
| Coordinate war room | R | C | C | I |
| Apply rollback | C | R | C | I |
| Feature flag toggle | C | R | C | I |
| External comms | C | C | I | R |
| Post-mortem draft | R | C | C | I |
| Action follow-up tracking | R | C | C | I |

## Quick Checklist (First 15 Minutes)
1. Acknowledge alert (assign IC).
2. Open war room channel `#incident-<id>`.
3. Set initial severity (SEV1 – SEV4).
4. Gather key metrics (error rate, p95 latency, DLQ size, Kafka lag).
5. Decide containment action (rollback? feature a flag off? a scale?).
6. Update status message (timestamped) every 10 minutes for SEV1/2.
7. Create incident doc skeleton (template) for timeline.

## Flow
1. Detection (alert)
2. IC assigned (≤ 5 min)
3. War room (`#incident-<id>`)
4. Containment (rollback, feature flag)
5. Eradication (fix root cause)
6. Recovery (validate SLO)
7. Post-mortem (≤ 48h)

## Rollback Decision Criteria
Rollback if ANY:
- Error rate > 2x SLO for > 5 min after mitigation attempt
- P95 latency regression > 50% sustained 10 min
- DLQ rate > 5% of messages for 10 min (risk of backlog growth)
- Data corruption risk (schema mismatch) detected (ref: ADR 0003 gates)

## Evidence Preservation
- Export relevant logs (time-bounded) and attach to incident doc.
- Snapshot DLQ messages (sample up to 50) before purge or requeue.
- Save Grafana dashboard screenshots for key metrics.
- Record version identifiers (service_version, schema_version, model_version) present.

## Metrics
- MTTA: < 5 min
- MTTR (SEV1): < 60 min
- % post-mortem delivered ≤ 48h: 100%
- % incidents with preserved evidence set: ≥ 95%

## End of Incident
- Only IC can declare the end after confirming: metrics within SLO, no active error budget burn spike, remediation or rollback verified.
- IC communicates closure summary (impact, root cause status, next steps) in a war room and status channel.

## Post-Mortem Template
- Summary
- Timeline
- Impact
- Root cause (5 why's)
- Corrective actions (owner and deadline)
- Lessons learned
- Preventive follow-ups (gates, tests, automation)

## Escalation
- If no improvement after 30 min (SEV1/2), escalate to on-call architect.
- If regulatory/privacy impact is suspected, page security contact immediately.

## Tools
- Alertmanager
- Grafana dashboards
- Repository (rollback via tag)
- Feature Flags dashboard

## Links
- Versioning & rollback policy: docs/adr/0003-versioning-policy.md
- Observability standards: docs/operations/observability.md
- Data classification (for potential exposure): docs/governance/data-classification.md

## Revision History
- v0.1.0 (2025-08-15): Initial version.
