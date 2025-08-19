package etl

import (
	"game-data-etl/internal/domain/entities"
	"game-data-etl/internal/domain/igdb_models"
	"game-data-etl/internal/platform/repositories"
)

// Enricher augments a transformed Game with associated dimension entities.
type Enricher interface {
	Enrich(game *entities.Game, raw igdb_models.IGDBGame) error
}

// DimensionEnricher loads dimension entities via repositories based on source refs in IGDB payload.
type DimensionEnricher struct {
	dimRepo *repositories.DimensionRepository
}

func NewDimensionEnricher(dimRepo *repositories.DimensionRepository) *DimensionEnricher {
	return &DimensionEnricher{dimRepo: dimRepo}
}

func (e *DimensionEnricher) Enrich(game *entities.Game, raw igdb_models.IGDBGame) error {
	// Fetch and assign simple many-to-many dimensions. Ignore individual errors to allow partial enrichment.
	if genres, err := e.dimRepo.GenresBySourceRefs(raw.Genres); err == nil {
		game.Genres = genres
	}
	if themes, err := e.dimRepo.ThemesBySourceRefs(raw.Themes); err == nil {
		game.Themes = themes
	}
	if keywords, err := e.dimRepo.KeywordsBySourceRefs(raw.Keywords); err == nil {
		game.Keywords = keywords
	}
	if modes, err := e.dimRepo.GameModesBySourceRefs(raw.GameModes); err == nil {
		game.GameModes = modes
	}
	if perspectives, err := e.dimRepo.PerspectivesBySourceRefs(raw.PlayersPerspectives); err == nil {
		game.Perspectives = perspectives
	}
	if franchises, err := e.dimRepo.FranchisesBySourceRefs(raw.Frachises); err == nil { // note: raw.Frachises spelling retained from model
		game.Franchises = franchises
	}
	if collection, err := e.dimRepo.CollectionBySourceRef(raw.Collection); err == nil && collection != nil {
		game.Collection = collection
		game.CollectionID = &collection.ID
	}
	return nil
}
