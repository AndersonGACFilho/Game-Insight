# Observability

Title: Observability Standards <br>
Version: 0.1.0 <br>
Last Updated: 2025-08-15 <br>
Owner: Anderson (Sole Maintainer) <andersonfilho09@gmail.com> <br>
Status: Accepted <br>
Decision: Final <br>

## Objectives
Correlate version, request, message, and model inference.

## Stack (Phase 1)
- Structured JSON logs
- OpenTelemetry (traces + metrics)
- Prometheus + Grafana
- Kafka lag / RabbitMQ queue exporters

## Log Standard
Minimum fields:
- timestamp, level, service, service_version
- trace_id, span_id, correlation_id (if saga)
- msg
- http_method, http_status (when applicable)
- latency_ms
- schema_version / model_version (when relevant)
- retry_count (for message processing logs)
- user_id (hashed) optional

### Example (JSON line)
```
{"timestamp":"2025-08-14T12:00:00Z","level":"INFO","service":"user-svc","service_version":"user-svc:1.4.2","trace_id":"...","correlation_id":"...","api_version":"v1","schema_version":"1.2.0","msg":"profile rebuilt","latency_ms":45,"user_hash":"ab91..."}
```

## Tracing
- W3C `traceparent` propagation
- Inject `trace_id` into message envelope (see docs/messaging/envelope.md)
- Use `correlation_id` to group multi-trace sagas (distinct from trace_id when multiple flows)

## Version Mapping (See ADR 0003)
| Dimension | Source | Example | Propagation |
|-----------|--------|---------|-------------|
| service_version | Image tag | user-svc:1.4.2 | Logs/Traces/Metrics |
| api_version | HTTP path | /v1/users | Logs/Traces |
| schema_version | Envelope/Avro | 1.2.0 | Logs/Envelope |
| model_version | Model registry | rec-model:2.3.1 | Logs/Metrics |
| infra_module_version | Terraform module tag | network-v1.5.0 | Infra dashboards |

## Key Metrics
- HTTP: p50, p95, error_rate
- Kafka: consumer_lag, dlq_count
- RabbitMQ: queue_depth, ack_latency
- Model: inference_latency_ms, rec_ctr (online), model_version

## Initial Alerts
- API p95 > 200ms (5 min window)
- Error rate > 2%
- Kafka lag > 60s
- DLQ > 1% last 24h

## Dashboards
1. Latency per service
2. Errors by version
3. Ingestion lag
4. Recommendation quality (CTR, coverage)
5. DLQ trending & retry_count distribution

## Retention
- Logs: 7 days
- Metrics: 30 days
- Traces: 3 days

## Governance Links
- Versioning policy: docs/adr/0003-versioning-policy.md
- Messaging envelope: docs/messaging/envelope.md
- Branching policy: docs/governance/branching-policy.md

## Future
- Feature store metrics
- Front-end RUM
- Version-aware alert suppression (ignore old phased-out versions)
- Automatic cardinality guardrails

## Revision History
- v0.1.0 (2025-08-15): Initial version.