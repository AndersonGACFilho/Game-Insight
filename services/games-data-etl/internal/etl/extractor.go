package etl

// extractor.go
// Responsibilities (SRP): Defines the Extractor abstraction
// and IGDB-specific implementation.
// SOLID Notes:
// * SRP: IGDBExtractor only performs network retrieval and
// decode of raw IGDB games; no transformation/enrichment.
// * OCP: Additional sources (Steam, PSN) can implement
// Extractor without modifying pipeline logic.
// * DIP: Pipeline depends on the Extractor interface;
// concrete IGDB implementation injected in main.

import (
	"encoding/json"
	"game-data-etl/internal/api/igdb"
	"game-data-etl/internal/domain/igdb_models"
	"github.com/rs/zerolog"
)

// Extractor defines contract for pulling raw game data from
// a source system.
// Implementations SHOULD be stateless (apart from injected
// collaborators) and safe for sequential reuse.
type Extractor interface {
	// Extract retrieves a page of raw source games.
	// Offset/limit paging strategy left to caller.
	Extract(offset, limit int) ([]igdb_models.IGDBGame, error)
	// BaseURL returns a debug-friendly string for
	// logging/metrics purposes.
	BaseURL() string
}

// IGDBExtractor implements Extractor using the IGDB API
// client.
// It intentionally keeps the "where" clause opaque so
// callers can tailor incremental or filtered extraction
// strategies.
type IGDBExtractor struct {
	client *igdb.Client
	logger zerolog.Logger
	where  string
}

// NewIGDBExtractor constructs a new IGDB-backed extractor.
func NewIGDBExtractor(
	client *igdb.Client,
	logger zerolog.Logger,
	where string,
) *IGDBExtractor {
	return &IGDBExtractor{client: client, logger: logger,
		where: where}
}

// BaseURL returns the underlying client's base URL (for
// diagnostics).
func (e *IGDBExtractor) BaseURL() string {
	return e.client.String()
}

// Extract fetches a window of games and JSON-unmarshals
// into model structs.
// Error Modes:
//   - Upstream HTTP / network failures propagate
//   - JSON unmarshal errors if contract drift occurs
func (e *IGDBExtractor) Extract(
	offset,
	limit int,
) ([]igdb_models.IGDBGame, error) {
	data, err := e.client.GetGames(
		igdb.GameQueryParams{Where: e.where, Offset: offset, Limit: limit},
	)
	if err != nil {
		return nil, err
	}
	var games []igdb_models.IGDBGame
	if err := json.Unmarshal(data, &games); err != nil {
		return nil, err
	}
	return games, nil
}
