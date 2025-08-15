# Game Insight 🎮
Game Insight is a cross-platform game recommendation engine designed to answer the perennial gamer question: **"What should I play next?"**

It solves this by creating a **"True Gamer Profile"**—a single, unified view of a user's entire gaming life, aggregating library data, playtime, and achievements from multiple platforms like Steam, PlayStation, and Xbox. This rich, behavioral dataset powers a nuanced recommendation engine that goes far beyond simple genre matching.

> Project Status: v0.1.0 – Initial public documentation and early architecture phase (pre-public MVP). Steam-only MVP in progress; console integrations pending official partnerships.

<!-- Badges (activate when pipelines exist)
![Build Status](#) ![Coverage](#) ![License: MIT](#) -->

---

## Table of Contents
1. Overview
2. Key Features
3. System Architecture
4. Project Roadmap (High-Level)
5. Documentation
6. Getting Started
7. Environment Variables
8. Development Workflow
9. Core Metrics (Initial Targets)
10. Contributing
11. License
12. Further Reading & Docs

---

## Overview
Game Insight unifies fragmented gaming activity into a behavioral profile used to deliver high-quality, explainable recommendations plus real-time deal intelligence. Short-term focus: Steam-only MVP validating retention + recommendation engagement. Medium-term: secure official console data partnerships. Long-term: social/network effects and anonymized B2B insights. Canonical terminology lives in `docs/glossary.md`.

## Key Features

- **Unified Game Library**: Connect multiple platform accounts to see your entire collection in one place.
- **Hybrid Recommendation Engine**: Combines content-based and collaborative filtering for tailored suggestions.
- **Deep Data Analysis**: Leverages nuanced metrics like **playtime** and **achievements** to understand playstyle.
- **Integrated Deal Tracking**: Alerts when wishlist games go on sale across storefronts.
- **Cross-Platform Clients**: Web (React) and Mobile (React Native).
- **Privacy & Governance (Planned)**: Explicit consent flows, transparent data usage, opt-out pipelines.

> Implementation Strategy: Start lean. A simplified single-process deployment (gateway + core logic + basic rec stub) can be used initially before decomposing into full microservices as scale and complexity justify it.

---

## System Architecture

A modern, polyglot microservices architecture for scalability and separation of concerns.

**Stack Overview**
- **Frontend**: React (Web), React Native (Mobile)
- **API Gateway & ETLs**: Go
- **Core Services**: Java (Spring Boot)
- **Recommendation Service**: Python (scikit-learn, TensorFlow)
- **Database**: PostgreSQL
- **Messaging**: RabbitMQ (commands) & Apache Kafka (ingestion)

Detailed diagrams and rationale: `./docs/architecture/strategic-analysis.md`.

ASCII (fallback) high-level view:
```
[ Clients ] -> [ API Gateway (Go) ] -> [ User Svc | Game Svc (Java) ] -> [ Rec Svc (Py) ]
                               |              |                      
                               v              v                      
                         [ RabbitMQ ]   [ PostgreSQL ] <-> [ Kafka Ingestion ]
```

---

## Project Roadmap (High-Level)

| Phase | Focus | Core Outcomes |
|-------|-------|---------------|
| 1. De-Risking & Foundation | Steam-only MVP, legal/privacy readiness | Steam ingest, core services, onboarding quiz |
| 2. Expansion & Growth | Console integrations (if licensed), freemium launch | Cross-platform rec engine v1, deal alerts, subscriptions |
| 3. Moat & Scaling | Social + B2B data products | Network effects, analytics dashboards, insights offering |

Full strategic roadmap (with Gantt, KPIs): see `docs/architecture/strategic-analysis.md`.
---
## Documentation

### 1. Overview
- Strategy & roadmap: `docs/architecture/strategic-analysis.md`
- ADRs: `docs/adr/*`

### 2. Architectural Decisions
- Backend languages: `docs/adr/0001-backend-language-selection.md`
- Messaging: `docs/adr/0002-messaging-architecture.md`
- Versioning: `docs/adr/0003-versioning-policy.md`
- Branching: `docs/governance/branching-policy.md`

### 3. Operations
- Security: `docs/governance/security-policy.md`
- Data classification: `docs/governance/data-classification.md`
- Observability: `docs/operations/observability.md`
- Runbooks: `docs/operations/runbooks/incident-response.md`
- SLOs: `docs/operations/slo.md`

### 4. Engineering
- Glossary (canonical): `docs/glossary.md`
- Messaging standards: `docs/messaging/envelope.md`
- Load testing: `docs/operations/testing/load-testing-plan.md`
- Contributing: `CONTRIBUTING.md`
- Changelog: `CHANGELOG.md`

### 5. Models / ML
- (Future) Feature store
- (Future) Recommendation model v1 (model card)

### 6. Security & Compliance
- SECURITY policy: `SECURITY.md`
- Secrets policy (in security policy)
- Scans (SAST, SCA, IaC, container)
- Incident response process

### 7. Data
- Ingestion pipeline (strategic analysis)
- Governance & retention (data classification)

### 8. Appendices
- Mermaid diagrams embedded in source files

## Getting Started

To get a local copy up and running for development and testing:

### Prerequisites
Install:
- Git
- Docker & Docker Compose
- Node.js (v18+)
- Go (v1.20+)
- Java (JDK 17+)
- Python (3.9+)

### Installation
1. Clone repository
```sh
git clone https://github.com/AndersonGACFilho/Game-Insight.git
cd Game-Insight
```
2. Copy env template & configure
```sh
cp example.env .env
```
3. Start services (when compose file available)
```sh
docker-compose up --build
```
4. (Optional) Run only web client (placeholder until structure is added)
```sh
cd web && npm install && npm start
```

---

## Environment Variables
(Adjust as services are created.)

| Variable | Description                | Example |
|----------|----------------------------|---------|
| STEAM_API_KEY | Steam Web API key          | abc123... |
| DATABASE_URL | Postgres connection string | postgres://user:pass@localhost:5432/game_insight |
| KAFKA_BROKERS | Kafka bootstrap servers    | localhost:9092 |
| RABBITMQ_URL | RabbitMQ connection URL    | amqp://guest:guest@localhost:5672/ |
| JWT_SECRET | Token signing secret       | (generated) |
| DEALS_API_KEY | IsThereAnyDeal key         | keyvalue |
| LOG_LEVEL | Global log verbosity       | info |

> Principle: store no secrets in code; prefer Docker secrets / vault in production.

---

## Development Workflow

1. **Branching**: `main` (stable), feature branches: `feat/<scope>`, fixes: `fix/<scope>`.
2. **Commits**: Prefer Conventional Commits (`feat:`, `fix:`, `chore:`, `docs:`, etc.).
3. **Code Style**: Add linters/formatters per language (planned): ESLint + Prettier, `gofmt`, `spotless` for Java, `black` for Python.
4. **Testing**: (Placeholder) Add minimal tests early—especially for rec engine evaluation metrics.
5. **CI/CD**: Planned pipeline stages: build → lint → unit tests → security scan → package → deploy.
6. **Security**: No production credentials in `.env`. Rotate API keys quarterly.
7. **Observability (Planned)**: Centralized logging, tracing (OpenTelemetry), metrics dashboards (Grafana).

---

## Core Metrics (Initial Targets)

| Metric | Definition | Phase 1 Target | Phase 2 Target |
|--------|------------|----------------|----------------|
| Activation Rate | % new users completing Steam link + quiz | ≥ 40% | ≥ 55% |
| 7-Day Retention | % returning within 7 days | ≥ 25% | ≥ 35% |
| Recommendation CTR | Clicks / impressions on rec list | ≥ 8% | ≥ 12% |
| Deal Alert Opt-in | % users enabling deal notifications | ≥ 30% | ≥ 45% |
| DAU/MAU Ratio | Engagement depth proxy | ≥ 0.18 | ≥ 0.25 |
| Premium Conversion | Free → paid (after launch) | – | 3–5% |

> KPIs evolve; see strategic doc appendix for extended list.

---

## Contributing

Contributions are welcome. Please read `CONTRIBUTING.md` for:
- Code of Conduct
- Branch & PR guidelines
- Review / merge criteria

Small improvements (docs, typos) are encouraged via separate PRs.

---

## License

Distributed under the MIT License. See `LICENSE`.

---

## Further Reading & Docs
- Strategic Analysis & Roadmap: `docs/architecture/strategic-analysis.md`
- ADRs: `docs/adr/*`
- Glossary: `docs/glossary.md`
- (Planned) API Reference: `docs/api/*.md`

---

## Disclaimer
This project does not use unofficial console APIs in production. Console data integration is contingent upon formal, lawful partnerships. Users retain control over revoking data access.

> Documentation metadata (Title, Version, Last Updated, Owner, Status) is being standardized across `docs/` progressively starting with v0.1.0.
