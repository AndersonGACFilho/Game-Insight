# Contributing

## Code of Conduct
See `CODE_OF_CONDUCT.md` (mandatory for all contributions).

## Environment
- Copy `example.env` to `.env` for local dev (DO NOT commit `.env`).
- New required env var: document in `example.env` + this file + related ADR if contract affecting.
- Secrets: use local overrides only; production secrets via secrets manager (Vault/SOPS placeholder).

## Workflow
1. Create branch per `docs/governance/branching-policy.md` (e.g. feat/my-feature).
2. Commits: Conventional Commits.
3. Open a draft PR early for visibility (serves as an audit trail since there is a single maintainer).
4. CI must be green (build, tests, OpenAPI/Avro diffs, lint, security).
5. Review: self-review (single maintainer). For potential breaking changes (API / schema / DB) apply a 24h cooling period before merge (except severe hotfix).

## Commit Message Examples
- `feat(game-service): add playtime aggregation endpoint`
- `fix(recs): handle empty library edge case`
- `chore(deps): bump postgres driver to 42.6.0`
- `refactor(ingestion): extract normalization pipeline`
- `docs: update messaging envelope examples`

Include `BREAKING CHANGE:` footer if removing/renaming externally visible field.

## Code Standards
- Go: `gofmt`, `golangci-lint`
- Java: Spotless + Checkstyle
- Python: `black`, `ruff`
- JS/TS: ESLint + Prettier

## Generating Artifacts
(Placeholders until implemented)
- OpenAPI spec: `make openapi` or `./scripts/gen-openapi.sh`
- Avro schema lint: `make schema-lint`
- SBOM: `make sbom`

## Running Tests
- All: `make test` (aggregator when available)
- Go: `go test ./...`
- Java: `./mvnw test`
- Python: `pytest`
- JS/TS: `npm test`

## Testing
- Unit: initial coverage target 50% (raise gradually)
- Contract: OpenAPI / Avro diffs enforced in CI
- Load: scenarios in `docs/operations/testing/load-testing-plan.md`

## Security
- Secrets must not be committed.
- Dependencies updated via Renovate (to configure).
- Report vulnerabilities per `SECURITY.md` (private email, no public issue).

## Versioning & Contracts
- Follow ADR 0003 (versioning policy) for bumps.
- Message schema/envelope changes must stay backward compatible unless MAJOR planned.
- Add new fields as optional with sensible defaults.

## Containers
- Do NOT use `latest` tags in examples; pin versions.
- Multi-arch images: build manifests in a release pipeline (future).

## Changelog
All external-facing changes: update the Unreleased section in `CHANGELOG.md`.
Categories: Added / Changed / Deprecated / Removed / Fixed / Security / Maintenance.

## PR Checklist (Copy into description)
- [ ] Follows branch naming policy
- [ ] Conventional Commits used
- [ ] Tests added/updated
- [ ] Contract (API/schema) backward compatible OR version bump justified
- [ ] CHANGELOG updated
- [ ] No secrets / credentials added
- [ ] Docs updated (ADR / README / envelope / observability if needed)
- [ ] (If breaking) 24h cooling period respected.

## Questions
Open issue with label `question`.
