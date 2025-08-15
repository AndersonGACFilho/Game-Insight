# ADR 0002: Messaging Strategy (RabbitMQ x Kafka)

Title: Messaging Strategy (RabbitMQ x Kafka) <br>
Version: 0.1.0 <br>
Last Updated: 2025-08-15 <br>
Owner: Anderson (Sole Maintainer) <andersonfilho09@gmail.com> <br>
Status: Accepted <br>
Decision: Final <br>

## Context
We must clearly separate communication patterns:
- Directed asynchronous commands (e.g., recalculate profile, trigger onboarding)
- Ingestion and normalization of large volumes of external events/data (Steam and future sources)
- Avoid coupling between core services and the ingestion pipeline
- Ensure reprocessing, horizontal scalability, and traceability (trace-id)

## Decision
- Use RabbitMQ for point-to-point or fanout asynchronous commands with low/medium rate (workflow, orchestration, retriable tasks)
- Use Apache Kafka for external data ingestion, normalization streams, and future analytical / recommendation feature events
- Standardize message envelope (fields: trace_id, occurred_at, schema_version)
- Define Avro contracts (Kafka) and schema-validated JSON (RabbitMQ)
- Introduce domain-specific dead letter topic/queue
- Enforce size guidance: commands < 256 KB (otherwise store external object + reference)

## Rationale
- RabbitMQ simplifies routing, fine-grained retries, and per-message acknowledgments for command/task scenarios
- Kafka optimizes throughput, retention, replay, and partition ordering for data pipelines
- Avoids overloading a single broker with conflicting patterns
- Eases future evolution (partial event sourcing or incremental feature store)

## Alternatives Considered
1. RabbitMQ only: simpler initially, poor replay scalability and high-throughput ingestion limits
2. Kafka only: complicates command patterns (ack latency, no simple native routing)
3. NATS JetStream: lower maturity for planned analytical ecosystem
4. Redis Streams: good for simple cases, insufficient for long retention and evolving contracts

## Consequences (Positive)
- Tool aligned to use case
- Ingestion reprocessing isolated from command queues
- Separate observability (Kafka lag vs command throughput)

## Consequences (Negative / Costs)
- Dual operational surface (monitor two systems)
- Schema management in two formats
- Larger initial team learning curve

## Mitigations
- Standardized infrastructure-as-code scripts (topic / queue provisioning)
- Automated registry (Avro) with CI validation
- Unified dashboards (lag, error rate, DLQ size)
- Versioned incident playbooks (runbooks)

## Retry & Idempotency Policy (Summary)
| Scenario | Mechanism | Limits |
|----------|-----------|--------|
| RabbitMQ transient error | exponential backoff requeue | 3 attempts then DLQ |
| Kafka consumer processing error (transient) | retry within consumer (in-memory) | configurable (default 5) |
| Permanent validation failure | direct DLQ | immediate |
| Duplicate command | idempotency_key check | drop second |

## DLQ Triage Workflow
1. Inspect batch (sample 10 messages) classify root cause (schema, transient infra, poisoned data).  
2. If schema mismatch -> create fix PR + patch version bump (see ADR 0003).  
3. Requeue only if deterministic fix applied; otherwise document and purge with justification.

## Size & Payload Guidance
- Commands: < 256 KB (target); prefer references (object storage) for large blobs.  
- Events: Partition key chosen to avoid hotspot (user_id or game_id).  
- Compression: Enable Kafka compression (lz4 or zstd) at producer.

## Compatibility & Versioning
- Follow ADR 0003 rules (SemVer, backward additions).  
- Envelope version fields: see docs/messaging/envelope.md.

## Validation Metrics
- Command processing p95 (RabbitMQ) < 2s (steady-state)
- Average Kafka ingestion lag < 60s under target load
- Messages sent to DLQ < 1% / 24h
- Incompatible schemas 100% blocked in CI (zero prod breaks)
- Full reprocessing (backfill) of 10GB data < 1.5h

## Implementation Plan (Phases)
1. Phase 1: Kafka topics for Steam ingestion (raw, normalized), RabbitMQ queues for user_profile.rebuild
2. Phase 2: Expand catalog events (game.updated) and derived commands (recommendation.refresh)
3. Phase 3: Introduce aggregated analytical events and compacted topics for dimension tables

## Risks
- Undersized partition configuration → bottleneck (Mitigation: baseline load tests)
- Divergent schemas across environments → runtime failures (Mitigation: promotion workflow)
- Rising operational cost (Mitigation: cost alerts and right sizing)

## Follow-Up
Review at end of Phase 1 to assess: need for compacted topics and adoption of additional consumer groups in the recommendation engine.

## Open Questions
- Partition key strategy for high-cardinality events: need documented hashing guidance.
- Threshold for introducing compacted topics for dimension data.
- Criteria for migrating some command patterns to gRPC if latency targets tighten.

## References
- `docs/architecture/strategic-analysis.md` (Sections: Architecture, Data Pipeline)
- ADR 0001 (Polyglot base and services)
- ADR 0003 (Versioning Policy)
- docs/messaging/envelope.md

## Revision History
- v0.1.0 (2025-08-15): Initial version.
