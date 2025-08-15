# Service Level Objectives (SLOs)

Title: Service Level Objectives <br>
Version: 0.1.0 <br>
Last Updated: 2025-08-15 <br>
Owner: Anderson (Sole Maintainer) <andersonfilho09@gmail.com> <br>
Status: Accepted <br>
Decision: Final <br>

## Purpose
Define reliability targets and error budgets for core-user-facing and data ingestion capabilities.

## Core Services & Indicators
| Service | SLI Type | SLI Definition | SLO Target | Budget Period | Notes |
|---------|----------|----------------|-----------|---------------|-------|
| API Gateway | Availability | Successful requests / total (HTTP 2xx,3xx) | 99.0% | 30 days | Excludes planned maintenance (<0.5%) |
| API Gateway | Latency (p95) | p95 latency for key endpoints | < 200ms | 30 days | Auth, recommendations, library fetch |
| Recommendation API | Latency (p95) | p95 model inference + assembly | < 600ms | 30 days | After warm cache |
| Recommendation API | Correctness Proxy | CTR relative to baseline model | ≥ baseline -5% | 30 days | Alerts on model drift |
| Ingestion (Steam) | Freshness | % of ingestion cycles < 2h lag | 95% | 30 days | Lag = now - last complete ingest per user |
| Kafka Pipeline | Processing Lag | p95 consumer lag (seconds) | < 60s | 30 days | Normalized topic |
| RabbitMQ Commands | Queue Delay | p95 time in queue before ack | < 5s | 30 days | user_profile.rebuild |
| Data Quality | Parse Success | Successful normalized records / total | ≥ 99% | 30 days | Steam ingest parsing |

## Error Budget Calculation
Error budget = (1 - SLO Target). Example: 99.0% availability ⇒ 1% budget over 30 days.

## Policies
- If error budget burn > 50% mid-period: freeze non-critical launches.
- > 80% burn: initiate reliability review & corrective action plan.
- >= 100% burn: mandatory freeze (features), focus on remediation.

## Alert Thresholds (Early Warning)
| SLO | Early Warning | Critical |
|-----|---------------|----------|
| Availability | 2h rolling < 99.2% | 2h rolling < 98.5% |
| Gateway p95 | > 180ms 15m | > 200ms 15m |
| Recommendation p95 | > 550ms 30m | > 600ms 30m |
| Ingestion Freshness | < 97% daily | < 95% daily |
| Kafka Lag | > 45s 10m | > 60s 10m |
| Command Queue Delay | > 4s 10m | > 5s 10m |
| Parse Success | < 99.5% daily | < 99% daily |

## Governance
- Monthly self-review: adjust targets if consistently exceeded.
- Track SLO trends in Observability dashboard (link TBD).
- Changes recorded via PR with justification (self-approval by sole maintainer).

## Future Additions
- Social layer latency & availability once launched.
- B2B data export timeliness.
- Per-model fairness / bias metrics.

## Revision History
- v0.1.0 (2025-08-15): Initial version.