package etl

// Deprecated: The monolithic DimensionEnricher has been
// replaced by the
// step‑oriented CompositeEnricher which composes focused
// GameEnrichment
// steps (see enrichment.go). This file is kept only for
// backwards
// compatibility with any still‑importing code or old
// runbooks.
// New code should call NewCompositeEnricher.

import (
	"game-data-etl/internal/api/igdb"
	"game-data-etl/internal/platform/repositories"
	"github.com/rs/zerolog"
)

// DimensionEnricher is now an alias of CompositeEnricher
// for backwards compatibility.
type DimensionEnricher = CompositeEnricher

// NewDimensionEnricher returns a CompositeEnricher.
// Deprecated: use NewCompositeEnricher.
func NewDimensionEnricher(
	dimRepo *repositories.DimensionRepository,
	client *igdb.Client,
	logger zerolog.Logger,
) *CompositeEnricher {
	return NewCompositeEnricher(dimRepo, client, logger)
}
