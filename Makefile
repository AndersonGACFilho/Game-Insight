# Makefile (solo-maintainer friendly)

DB_URL ?= postgres://user:pass@localhost:5432/game_insight?sslmode=disable
MIGRATIONS_DIR = db/migrations
PSQL ?= psql

# Apply all migrations in order (assuming filenames 000N_*.sql)
.PHONY: db.migrate
db.migrate:
	@echo "Applying migrations to $(DB_URL)"
	@for f in `ls $(MIGRATIONS_DIR)/*.sql | sort`; do \
		echo "--> $$f"; \
		$(PSQL) $(DB_URL) -v ON_ERROR_STOP=1 -f $$f || exit 1; \
	done
	@echo "Migrations complete"

# Run lightweight smoke checks (schema presence, basic counts)
.PHONY: db.smoke
db.smoke:
	@echo "Running DB smoke tests";
	@$(PSQL) $(DB_URL) -v ON_ERROR_STOP=1 -f scripts/db_smoke.sql
	@echo "Smoke OK"

# Convenience: migrate + smoke
.PHONY: db.setup
db.setup: db.migrate db.smoke

# Validate documentation metadata headers (simple script)
.PHONY: docs.validate
docs.validate:
	@python scripts/validate-doc-metadata.py docs || echo "(Optional) Metadata validation script finished"

# Help
.PHONY: help
help:
	@echo "Targets:"; \
	echo "  db.migrate     - Apply SQL migrations"; \
	echo "  db.smoke       - Run smoke test queries"; \
	echo "  db.setup       - Migrate then smoke"; \
	echo "  docs.validate  - Run metadata validation (best-effort)";

