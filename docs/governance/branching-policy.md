# Branching Policy

Title: Branching Policy <br>
Version: 0.1.0 <br>
Last Updated: 2025-08-15 <br>
Owner: Anderson (Sole Maintainer) <andersonfilho09@gmail.com> <br>
Status: Accepted <br>
Decision: Final <br>

## 1. Objective
Standardize branch creation, naming, and lifecycle to ensure traceable releases, predictable continuous integration, reduced conflicts, and enforcement of compatibility gates.

## 2. Scope
- Main repository (services, infra, models, docs).
- Integration with CI/CD pipelines, versioning policy, and changelog.

## 3. Main Branch
`main`
- Always stable; only receives merges via approved PR and green pipeline.
- Represents the release-ready state.
- Tags `vMAJOR.MINOR.PATCH` are applied to commits on this branch.

## 4. Branch Types
| Type | Prefix | Purpose | Example |
|------|--------|---------|---------|
| Feature | `feat/` | New backward compatible functionality | `feat/user-preferences` |
| Fix | `fix/` | Bug fixes | `fix/null-pointer-rec` |
| Refactor | `refactor/` | Internal refactor, no functional change | `refactor/cache-layer` |
| Chore | `chore/` | Operational tasks (build, deps) | `chore/update-sbom` |
| Docs | `docs/` | Documentation | `docs/branching-policy` |
| Spike | `spike/` | Disposable exploratory work (short-lived) | `spike/vector-db-test` |
| Hotfix | `hotfix/` | Urgent post-release fix | `hotfix/v1.3.2-cache-leak` |
| Release Candidate | `rc/` | Hardening before tagging | `rc/v1.4.0-rc.1` |
| Experimental | `exp/` | Long-lived experiment behind flags | `exp/new-ranking-graph` |
| Support (LTS) | `support/` | Maintenance of prior MAJOR (criteria-based) | `support/v1` |

## 5. Naming Convention
`<prefix>/<kebab-case-slug>`
- Short slug (≤5 words).
- Avoid isolated ticket numbers; if needed append: `feat/recs-ranking-123`.

## 6. General Flow
1. Create `feat/*` branch from `main`.
2. Commits follow Conventional Commits.
3. Open PR early (draft) for visibility (even as sole maintainer; acts as change log for context).
4. CI runs: build → tests → contract/schema diff → SAST/DAST (as applicable).
5. Approval: self-review (sole maintainer). For potential breaking changes (schema / API / DB) apply 24h cooling period before merge unless hotfix/SEV fix.
6. Rebase (no merge commit) before merge.
7. Squash recommended for small features; merge commit allowed if historical granularity is useful (models / migrations).

## 7. Release Candidates
- Create `rc/vX.Y.Z-rc.N` from `main` for final tests (load, security, canary).
- Only fixes (`fix/` or `docs/`) target the RC until stable.
- Max simultaneous open RC branches: 1 (avoid confusion).
- Once approved, create tag `vX.Y.Z` on the `main` commit (fast-forward from RC).

## 8. Hotfix
1. Branch `hotfix/vX.Y.(Z+1)-<slug>` from the affected tag or `main` (if minimal divergence).
2. Full CI required.
3. After merge into `main`, create tag `vX.Y.(Z+1)`.
4. If an RC branch is active, cherry-pick the fix.

## 9. Support / LTS Branches
Created only if BOTH:
- ≥ 2 active production MAJOR versions with enterprise consumers.
- Backport demand > 2 fixes / month for prior MAJOR.
Rules:
- Only security & critical fixes.
- Prefix `support/v<MAJOR>`.
- Must update CHANGELOG under that MAJOR section.

## 10. Experimental Branches
- Use `exp/` when feature spans multiple sprints and is fully guarded by flags.
- Rebase weekly to avoid drift.
- Must convert to `feat/` or merge within 30 days or be closed.

## 11. Protections (`main`)
- Force push prohibited.
- Requires green status checks (tests, OpenAPI/Avro diff, SAST).
- Self-checklist required for contract-impacting changes (schema diff, version bump, CHANGELOG updated).
- Cooling period (≥24h) for planned breaking changes unless emergency (document waiver in PR description).
- Blocks merge if: missing changelog, schema break without a version bump, endpoint removal without `Deprecation` header.

## 12. Sync
- Use `git fetch` + `git rebase main` (not merge) for long-lived branches.
- RC rebase only for critical hotfix; avoid rewriting history after performance testing starts.

## 13. Specific Policies
- Branch alive >14 days: mandatory review (avoid large feature branches).
- `spike/*` must be closed or converted to `feat/*` ≤ 5 business days.
- ML models: major evolution of input requires separate PR (`feat/model-api-v2`) for contract analysis.
- Squash merge aggregates commit bodies; if individual Conventional Commit messages contain BREAKING CHANGE footer, ensure final squash commit retains it.

## 14. Changelog
- Every PR altering external behavior must update `CHANGELOG.md` (an Unreleased section).
- Automation: verify presence of `(#PR)` reference.

## 15. Integration With Versioning
| Change | Recommended Branch | Initial Bump |
|--------|--------------------|-------------|
| New backward compatible feature | `feat/*` | MINOR |
| Bug fix | `fix/*` | PATCH |
| Planned breaking change | `feat/*` + ADR | MAJOR |
| Urgent production fix | `hotfix/*` | PATCH |
| Flag removal making change observable | `feat/*` | MAJOR |

## 16. Automations (CI Gates)
- Validate branch prefix.
- Script validates Conventional Commits.
- OpenAPI/Avro diff → auto label (`breaking`, `minor`, `patch`).
- Block if label ≠ intended bump in a version file (`VERSION`).

## Revision History
- v0.1.0 (2025-08-15): Initial version.
