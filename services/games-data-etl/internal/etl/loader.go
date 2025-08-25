package etl

// loader.go
// SRP: Loader abstracts persistence of the fully enriched
// Game aggregate.
// OCP: Alternative storage engines (e.g., event sourcing,
// different DB) can implement Loader without changing
// pipeline.
// DIP: Pipeline depends on Loader interface.

import (
	"game-data-etl/internal/domain/entities"
	"game-data-etl/internal/platform/repositories"
)

// Loader persists transformed & enriched games.
// Implementations SHOULD be idempotent for upsert
// semantics.
type Loader interface {
	Save(*entities.Game) error
	Update(*entities.Game) error
}

// GameLoader implements Loader using the GameRepository.
type GameLoader struct {
	repo *repositories.GameRepository
}

// NewGameLoader constructs a GameLoader.
func NewGameLoader(
	repo *repositories.GameRepository,
) *GameLoader {
	return &GameLoader{repo: repo}
}

// Save performs an upsert of the aggregate graph.
func (l *GameLoader) Save(
	g *entities.Game,
) error {
	return l.repo.UpsertGraph(g)
}

// Update currently delegates to Save (semantic alias). Kept
// for interface symmetry / future diff logic.
func (l *GameLoader) Update(
	game *entities.Game,
) error {
	return l.repo.UpsertGraph(game)
}

// ExistsBySourceRef exposes a parent existence check for use by the
// pipeline's parent-aware flow (type-asserted interface).
func (l *GameLoader) ExistsBySourceRef(
	sourceRef int64,
) (bool, error) {
	return l.repo.ExistsBySourceRef(sourceRef)
}

// ResolveParentBySourceRef resolves and sets the ParentGameID on the provided
// Game aggregate given a parent source_ref.
func (l *GameLoader) ResolveParentBySourceRef(
	game *entities.Game,
	parentSourceRef int64,
) error {
	return l.repo.ResolveParentBySourceRef(game, parentSourceRef)
}
