package etl

import (
	"encoding/json"
	"game-data-etl/internal/api/igdb"
	"game-data-etl/internal/domain/igdb_models"
	"github.com/rs/zerolog"
)

// Extractor defines contract for pulling raw game data from a source system.
type Extractor interface {
	Extract(offset, limit int) ([]igdb_models.IGDBGame, error)
	BaseURL() string
}

// IGDBExtractor implements Extractor using the IGDB API client.
type IGDBExtractor struct {
	client *igdb.Client
	logger zerolog.Logger
	where  string
}

func NewIGDBExtractor(client *igdb.Client, logger zerolog.Logger, where string) *IGDBExtractor {
	return &IGDBExtractor{client: client, logger: logger, where: where}
}

func (e *IGDBExtractor) BaseURL() string { return e.client.String() }

func (e *IGDBExtractor) Extract(offset, limit int) ([]igdb_models.IGDBGame, error) {
	data, err := e.client.GetGames(igdb.GameQueryParams{Where: e.where, Offset: offset, Limit: limit})
	if err != nil {
		return nil, err
	}
	var games []igdb_models.IGDBGame
	if err := json.Unmarshal(data, &games); err != nil {
		return nil, err
	}
	return games, nil
}
