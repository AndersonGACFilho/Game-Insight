# Load Testing Plan (Phase 1)

Title: Load Testing Plan (Phase 1) <br>
Version: 0.1.0 <br>
Last Updated: 2025-08-15 <br>
Owner: Anderson (Sole Maintainer) <andersonfilho09@gmail.com> <br>
Status: Accepted <br>
Decision: Final <br>

## Objectives
- Validate gateway p95 < 200ms
- Confirm ingestion throughput without a backlog > 60s

## Scope
- API Gateway (critical endpoints)
- Recommendation Service (inference latency)
- Kafka pipeline (Steam ingestion)

## Metrics
- Latency p50/p95/p99
- Error rate
- CPU / memory
- Kafka lag
- RabbitMQ queue size

## Scenarios
1. Ramp: 0 -> 100 req/s in 10 min
2. Sustained peak: 150 req/s for 30 min
3. Burst: 5 spikes of 300 req/s (1 min each)
4. Ingestion: 10GB backfill (measure duration)

## Tools
- k6 (HTTP)
- Kafka performance scripts
- Prometheus + Grafana

## Success Criteria
- p95 <= 200ms (gateway)
- Error rate < 1%
- Average lag < 60s
- No OOM / throttling

## Automation
Nightly pipeline (cron) + stored report

## Future
Chaos tests (artificial latency, partition loss)

## Revision History
- v0.1.0 (2025-08-15): Initial version.