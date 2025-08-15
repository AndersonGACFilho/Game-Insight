# ADR 0001: Backend Language Selection (Go, Java, Python)

Title: Backend Language Selection <br>
Version: 0.1.0 <br>
Last Updated: 2025-08-15 <br>
Owner: Anderson (Sole Maintainer) <andersonfilho09@gmail.com> <br>
Status: Accepted <br>
Decision: Final <br>

## Context
We need a scalable foundation for the MVP (Gateway + ETLs + Core Services + Recommendation) with:
- Low latency for the gateway.  
- Reliability and maturity for central business rules.  
- Strong machine learning library ecosystem for recommendations.  
- Clear path for observability, testing, and multi-stack CI/CD.

## Non-Goals
- Selecting a feature store technology (future ADR).  
- Defining deployment strategy (blue/green, canary) details.  
- Finalizing data warehouse / lake choice.

## Decision
Adopt a polyglot architecture:
1. Go for API Gateway and ETLs (simple concurrency, reduced footprint, static binaries).
2. Java (Spring Boot) for User Service and Game Service (robust ecosystem, validation, security, transactions).
3. Python for Recommendation Service (ML ecosystem and rapid prototyping).
4. RabbitMQ for asynchronous inter-service commands.
5. Kafka for ingestion and normalization of external data (Steam + future official sources).
6. PostgreSQL as the initial unified transactional store.
7. Separate a future feature store (later phase) if an ML load grows.

## Rationale
- Reduces risk of technological lock-in in a single stack that does not cover all needs well.  
- Minimizes MVP delivery time by leveraging domain strengths.  
- Enables independent evolution of each service (deploy, domain-specific horizontal scaling).

## Alternatives Considered
1. Single Java monolith: Lower initial complexity but heavy refactor risk at scale and less agility for ML experimentation.
2. Full Go: Excellent performance but higher ML cost (less mature libraries).
3. Full Python: Fast ML iteration, but worse cold start for high concurrency services and higher maintenance cost for large domain services.
4. Node.js + Python: Similar tradeoffs, but Java provides superior maturity for business rules and enterprise tooling.

## Consequences (Positive)
- Clear responsibility alignment per language.  
- Independent scalability (scale gateway separately from recommendation engine).  
- Team can iterate on models without blocking core deploys.

## Consequences (Negative / Costs)
- Toolchain overhead (different linters, builds, Docker images).  
- Steeper observability curve (consistent tracing across runtimes).  
- Multiplied security and dependency management.

## Mitigations
- Define cross-cutting standards (structured JSON logging, trace-id correlation).  
- Shared repository of reusable CI pipelines.  
- Hardened base images per language.  
- Future ADR for Feature Store and deployment model (blue/green or canary).

## Observability & Governance Links
- Versioning rules: ADR 0003.  
- Messaging & envelope propagation: ADR 0002 + docs/messaging/envelope.md.  
- Telemetry fields (service_version, api_version): docs/operations/observability.md.

## Validation Metrics
- Gateway p95 latency < 200ms.  
- Core services availability >= 99%.  
- Mean model cycle time (prototype -> production) < 5 days.  
- Inter-service serialization errors < 0.5% over 30 days.

## Risks Not Addressed
- Internal skill gaps may slow parallel multi-language development.
- Potential JNI / interop overhead if future cross-language ML components emerge.
- Increased cognitive load for new contributors ramping across 3 runtimes.
- Risk of uneven maturity in shared tooling (lint, security scanning) early on.
- Single maintainer bus factor (no redundancy) increases operational risk.

## Follow-Up
Scheduled review at end of Phase 1 (before integrating new platforms or adding social layer).

## Open Questions
- Should Go also host lightweight aggregation logic for ingestion to reduce Java service fan-out?
- Criteria/threshold for introducing a separate feature store (events per second? model retrain frequency?).
- Need for a shared protobuf / gRPC layer between Java and Python when latency-sensitive rec inference appears?

## Supersedes / Superseded By
- Supersedes: N/A (initial).  
- Superseded by: (placeholder for future revisions).

## References
- docs/architecture/strategic-analysis.md (Sections: Architecture, Recs, Roadmap)  
- Phase 1 Roadmap (infrastructure and initial recommendation)
- ADR 0002 (Messaging)  
- ADR 0003 (Versioning)

## Revision History
- v0.1.0 (2025-08-15): Initial version.
