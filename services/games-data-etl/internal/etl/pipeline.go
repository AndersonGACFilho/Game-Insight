package etl

import (
	"game-data-etl/internal/domain/entities"
	"game-data-etl/internal/domain/igdb_models"
	"github.com/rs/zerolog"
)

type Pipeline struct {
	logger      zerolog.Logger
	extractor   Extractor
	transformer Transformer
	enricher    Enricher
	loader      Loader
	batchLimit  int
}

func NewPipeline(logger zerolog.Logger, extractor Extractor, transformer Transformer, enricher Enricher, loader Loader, batchLimit int) *Pipeline {
	if batchLimit <= 0 {
		batchLimit = 500
	}
	return &Pipeline{logger: logger, extractor: extractor, transformer: transformer, enricher: enricher, loader: loader, batchLimit: batchLimit}
}

type parentAwareLoader interface {
	Loader
	ExistsBySourceRef(int64) (bool, error)
	ResolveParentBySourceRef(*entities.Game, int64) error
}

func (p *Pipeline) Run() {
	p.logger.Info().Msg("Running ETL pipeline...")

	offset := 0
	limit := p.batchLimit
	for {
		gamesRaw, err := p.extractor.Extract(offset, limit)
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
		// Retry pass for deferred children (max 2 extra passes)
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
			if len(next) == len(deferred) { // no progress
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

func (p *Pipeline) processOne(idx int, raw igdb_models.IGDBGame, deferred *[]igdb_models.IGDBGame) bool {
	p.logger.Debug().Int("game_index", idx).Str("game_name", raw.Name).Msg("Processing game")
	// Transform
	game, err := p.transformer.Transform(raw)
	if err != nil {
		p.logger.Error().Err(err).Int("game_index", idx).Str("game_name", raw.Name).Msg("Transformation failed")
		return true
	}
	// Parent existence handling (rate-limit optimization)
	if raw.ParentGame != nil {
		if pl, ok := p.loader.(parentAwareLoader); ok {
			exists, err := pl.ExistsBySourceRef(int64(*raw.ParentGame))
			if err != nil {
				p.logger.Warn().Err(err).Int("game_index", idx).Msg("Parent existence check failed")
			} else if !exists {
				// Defer until later pass when parent may be processed
				*deferred = append(*deferred, raw)
				p.logger.Debug().Int("game_index", idx).Int64("parent_source_ref", int64(*raw.ParentGame)).Msg("Deferring child game (parent missing)")
				return false
			} else {
				// Resolve FK
				if err := pl.ResolveParentBySourceRef(game, int64(*raw.ParentGame)); err != nil {
					p.logger.Warn().Err(err).Int("game_index", idx).Msg("Failed resolving parent FK")
				}
			}
		}
	}
	// Enrich
	if err := p.enricher.Enrich(game, raw); err != nil {
		p.logger.Warn().Err(err).Int("game_index", idx).Str("game_name", game.Title).Msg("Enrichment encountered issues")
	}
	// Load
	if err := p.loader.Save(game); err != nil {
		p.logger.Error().Err(err).Int("game_index", idx).Str("game_name", game.Title).Msg("Persistence failed")
		return true
	}
	p.logger.Debug().Int("game_index", idx).Str("game_name", game.Title).Msg("Game processed successfully")
	return true
}
