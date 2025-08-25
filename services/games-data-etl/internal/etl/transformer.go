package etl

// transformer.go
// SRP: Transformer converts raw source DTO into internal
// aggregate root skeleton (Game) without enrichment.
// OCP: Additional transformers for other sources can
// implement Transformer interface.
// DIP: Pipeline uses the Transformer interface abstraction.

import (
	"game-data-etl/internal/domain/entities"
	"game-data-etl/internal/domain/igdb_models"
	"github.com/google/uuid"
	"github.com/rs/zerolog"
	"strings"
	"time"
)

// Transformer abstracts converting a source (IGDB) model
// into the domain entity.
// Must not perform persistence or network calls.
type Transformer interface {
	Transform(igdb_models.IGDBGame) (*entities.Game, error)
}

// GameTransformer implements Transformer for IGDB game
// payloads.
// Minimal validation logic lives here (e.g., slug
// normalization); complex rules belong to a validation
// layer (future).
type GameTransformer struct {
	logger zerolog.Logger
}

// NewGameTransformer creates a new game transformer
// instance.
func NewGameTransformer(
	logger zerolog.Logger,
) *GameTransformer {
	return &GameTransformer{logger: logger}
}

// Transform maps IGDBGame -> entities.Game (base fields
// only).
// Side effects: logging only.
func (t *GameTransformer) Transform(
	igdbGame igdb_models.IGDBGame,
) (*entities.Game, error) {
	// Start log context
	t.logger.Debug().Str("game_name", igdbGame.Name).Msg("Starting game transformation")

	game := &entities.Game{
		ID: uuid.New(),
		SourceMeta: entities.SourceMeta{
			SourceRef:       igdbGame.ID,
			CreatedAtSource: igdbGame.CreatedAt.Time,
			UpdatedAtSource: igdbGame.UpdatedAt.Time,
		},
		Title:                 strings.TrimSpace(igdbGame.Name),
		Slug:                  strings.ToLower(strings.TrimSpace(igdbGame.Name)),
		Summary:               ptrOrNil(igdbGame.Summary),
		Storyline:             ptrOrNil(igdbGame.StoryLine),
		FirstReleaseDate:      timePtr(igdbGame.FirstReleaseDate.Time),
		TotalRating:           floatPtrOrNil(igdbGame.TotalRating),
		TotalRatingCount:      int32PtrOrNil(igdbGame.TotalRatingCount),
		AggregatedRating:      floatPtrOrNil(igdbGame.AggregatedRating),
		AggregatedRatingCount: int32PtrOrNil(igdbGame.AggregatedRatingCount),
		Popularity:            floatPtrOrNil(igdbGame.Popularity),
		IngestionTimestamp:    time.Now().UTC(),
	}
	// Category & Status
	game.CategoryCode = int16PtrOrNil(igdbGame.Category)
	game.StatusCode = int16PtrOrNil(igdbGame.Status)
	// End
	t.logger.Debug().Str("game_name", game.Title).Msg("Game base transformation completed")
	return game, nil
}

// Helper pointer builders (kept private to transformation
// layer)
func ptrOrNil(s string) *string {
	if strings.TrimSpace(s) == "" {
		return nil
	}
	v := strings.TrimSpace(s)
	return &v
}
func floatPtrOrNil(f float64) *float64 {
	if f == 0 {
		return nil
	}
	return &f
}
func int32PtrOrNil(v int32) *int32 {
	if v == 0 {
		return nil
	}
	return &v
}
func timePtr(t time.Time) *time.Time {
	if t.IsZero() {
		return nil
	}
	return &t
}
func int16PtrOrNil(v int16) *int16 { return &v }
