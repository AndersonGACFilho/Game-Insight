package etl

// pipeline.go
// Coordinates the ETL flow (Extract -> Transform -> Enrich
// -> Load) applying paging and retry for child games.
// SOLID Notes:
// * SRP: Orchestrates sequence & control flow only (no
// domain mutation beyond calling collaborators).
// * OCP: New steps (e.g., validation) can wrap or decorate
// interfaces without changing Pipeline internals.
// * DIP: Depends exclusively on abstractions: Extractor,
// Transformer, Enricher, Loader.

import (
	"game-data-etl/internal/domain/entities"
	"game-data-etl/internal/domain/igdb_models"
	"github.com/rs/zerolog"
	"time"
)

// Pipeline wires the ETL stages and provides batch
// iteration & minimal retry for child records.
type Pipeline struct {
	logger      zerolog.Logger
	extractor   Extractor
	transformer Transformer
	enricher    Enricher
	loader      Loader
	metrics     *Metrics
	batchLimit  int
}

// NewPipeline constructs a pipeline with sane defaults
// (batchLimit coerced to >0).
func NewPipeline(logger zerolog.Logger, extractor Extractor,
	transformer Transformer, enricher Enricher, loader Loader,
	batchLimit int, metrics *Metrics) *Pipeline {
	if batchLimit <= 0 {
		batchLimit = 500
	}
	return &Pipeline{logger: logger, extractor: extractor,
		transformer: transformer, enricher: enricher, loader: loader, batchLimit: batchLimit, metrics: metrics}
}

type parentAwareLoader interface {
	Loader
	ExistsBySourceRef(int64) (bool, error)
	ResolveParentBySourceRef(*entities.Game, int64) error
}

// Run executes extraction in pages until an empty page is
// returned.
// Child game handling: games referencing a missing parent
// are deferred and retried up to 2 additional passes within
// the batch.
// Metrics: durations + counters emitted per stage; errors
// increment specific counters but do not halt batch unless
// extraction fails.
func (p *Pipeline) Run() {
	p.logger.Info().Msg("Running ETL pipeline...")

	offset := 0
	limit := p.batchLimit
	for {
		startExtract := time.Now()
		gamesRaw, err := p.extractor.Extract(
			offset,
			limit,
		)
		if p.metrics != nil {
			p.metrics.ObserveStep("extract", startExtract)
			p.metrics.ExtractedGames.Add(float64(len(gamesRaw)))
		}
		if err != nil {
			p.logger.Error().Err(err).Int("offset", offset).Msg("Extraction failed")
			return
		}
		p.logger.Info().Int("games_count", len(gamesRaw)).Int("offset", offset).Msg("Extracted games")
		if len(gamesRaw) == 0 {
			break
		}

		deferred := make([]igdb_models.IGDBGame, 0)
		for i, raw := range gamesRaw {
			if !p.processOne(i, raw, &deferred) {
				continue
			}
		}
		passes := 0
		for len(deferred) > 0 && passes < 2 {
			passes++
			p.logger.Debug().Int("deferred_count", len(deferred)).Int("pass", passes).Msg("Retrying deferred child games")
			next := make([]igdb_models.IGDBGame, 0)
			for i, raw := range deferred {
				if !p.processOne(i, raw, &next) {
					continue
				}
			}
			if len(next) == len(deferred) {
				p.logger.Warn().Int("remaining", len(next)).Msg("Stopping retries for deferred games (parents missing)")
				break
			}
			deferred = next
		}
		if len(deferred) > 0 {
			p.logger.Warn().Int("unprocessed_children", len(deferred)).Msg("Some child games skipped due to missing parents; will rely on future runs")
		}

		offset += limit
		p.logger.Info().Int("batch_size", len(gamesRaw)).Int("next_offset", offset).Msg("ETL batch completed")
		if len(gamesRaw) < limit {
			break
		}
	}
	p.logger.Info().Int("final_offset", offset).Msg("ETL pipeline completed")
}

// processOne executes Transform -> (parent resolution) ->
// Enrich -> Load for a single game.
// Returns true if processing concluded (success or
// non-retryable failure) and false if the game was
// deferred.
// Error Handling: transformation or load failures are
// logged & metered; enrichment failures are soft (partial
// data permitted).
func (p *Pipeline) processOne(
	idx int,
	raw igdb_models.IGDBGame,
	deferred *[]igdb_models.IGDBGame,
) bool {
	p.logger.Debug().Int("game_index", idx).Str("game_name", raw.Name).Msg("Processing game")
	// Transform
	startTransform := time.Now()
	game, err := p.transformer.Transform(raw)
	if p.metrics != nil {
		p.metrics.ObserveStep("transform", startTransform)
	}
	if err != nil {
		if p.metrics != nil {
			p.metrics.TransformErrors.Inc()
		}
		p.logger.Error().Err(err).Int("game_index", idx).Str("game_name", raw.Name).Msg("Transformation failed")
		return true
	}
	// Parent existence handling
	if raw.ParentGame != nil {
		if pl, ok := p.loader.(parentAwareLoader); ok {
			exists, err := pl.ExistsBySourceRef(int64(*raw.ParentGame))
			if err != nil {
				p.logger.Warn().Err(err).Int("game_index", idx).Msg("Parent existence check failed")
			} else if !exists {
				// Defer the game but still try to resolve parent when processed later
				*deferred = append(*deferred, raw)
				if p.metrics != nil {
					p.metrics.DeferredGames.Inc()
				}
				p.logger.Debug().Int("game_index", idx).Int64("parent_source_ref", int64(*raw.ParentGame)).Msg("Deferring child game (parent missing)")
				return false
			}

			// Always attempt to resolve parent if we reach this point
			if err := pl.ResolveParentBySourceRef(game, int64(*raw.ParentGame)); err != nil {
				p.logger.Warn().Err(err).Int("game_index", idx).Msg("Failed resolving parent FK")
			}
		}
	}
	// Enrich
	startEnrich := time.Now()
	if err := p.enricher.Enrich(game, raw); err != nil {
		if p.metrics != nil {
			p.metrics.EnrichErrors.Inc()
		}
		p.logger.Warn().Err(err).Int("game_index", idx).Str("game_name", game.Title).Msg("Enrichment encountered issues")
	}
	if p.metrics != nil {
		p.metrics.ObserveStep("enrich", startEnrich)
	}
	// Load
	startLoad := time.Now()
	if err := p.loader.Save(game); err != nil {
		if p.metrics != nil {
			p.metrics.LoadErrors.Inc()
		}
		p.logger.Error().Err(err).Int("game_index", idx).Str("game_name", game.Title).Msg("Persistence failed")
		if p.metrics != nil {
			p.metrics.ObserveStep("load", startLoad)
		}
		return true
	}
	if p.metrics != nil {
		p.metrics.ObserveStep("load", startLoad)
		p.metrics.ProcessedGames.Inc()
	}
	p.logger.Debug().Int("game_index", idx).Str("game_name", game.Title).Msg("Game processed successfully")
	return true
}
