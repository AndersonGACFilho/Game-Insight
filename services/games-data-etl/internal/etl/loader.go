package etl

import (
	"game-data-etl/internal/domain/entities"
	"game-data-etl/internal/platform/repositories"
)

// Loader persists transformed & enriched games.
type Loader interface {
	Save(*entities.Game) error
	Update(*entities.Game) error
}

// GameLoader implements Loader using the GameRepository.
type GameLoader struct {
	repo *repositories.GameRepository
}

func NewGameLoader(repo *repositories.GameRepository) *GameLoader { return &GameLoader{repo: repo} }

func (l *GameLoader) Save(g *entities.Game) error {
	// Use SaveWithAssociations to persist game and its many-to-many relationships.
	return l.repo.SaveWithAssociations(g)
}

func (l *GameLoader) Update(game *entities.Game) error {
	// Use SaveWithAssociations to update game and its many-to-many relationships.
	return l.repo.SaveWithAssociations(game)
}
