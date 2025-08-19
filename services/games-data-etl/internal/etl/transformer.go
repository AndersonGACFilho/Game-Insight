package etl

import (
	"game-data-etl/internal/domain/entities"
	"game-data-etl/internal/domain/igdb_models"
	"github.com/google/uuid"
	"github.com/rs/zerolog"
	"strings"
	"time"
)

// Transformer abstracts converting a source (IGDB) model into the domain entity.
type Transformer interface {
	Transform(igdb_models.IGDBGame) (*entities.Game, error)
}

// GameTransformer implements Transformer for IGDB game payloads.
type GameTransformer struct {
	logger zerolog.Logger
}

func NewGameTransformer(logger zerolog.Logger) *GameTransformer {
	return &GameTransformer{logger: logger}
}

func (t *GameTransformer) Transform(igdbGame igdb_models.IGDBGame) (*entities.Game, error) {
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

	t.logger.Debug().Str("game_name", game.Title).Msg("Game base transformation completed")
	return game, nil
}

// Helper pointer builders (kept private to transformation layer)
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
