package etl

import (
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

func (p *Pipeline) Run() {
	p.logger.Info().Msg("Running ETL pipeline...")

	offset := 0
	limit := p.batchLimit

	for {
		// 1. Extract
		gamesRaw, err := p.extractor.Extract(offset, limit)
		if err != nil {
			p.logger.Error().Err(err).Int("offset", offset).Msg("Extraction failed")
			return
		}
		p.logger.Info().Int("games_count", len(gamesRaw)).Int("offset", offset).Msg("Extracted games")
		if len(gamesRaw) == 0 {
			break
		}

		// 2..n Process each game (Transform -> Enrich -> Load)
		for i, raw := range gamesRaw {
			p.processOne(i, raw)
		}

		offset += limit
		p.logger.Info().Int("batch_size", len(gamesRaw)).Int("next_offset", offset).Msg("ETL batch completed")
		if len(gamesRaw) < limit {
			break
		}
	}
	p.logger.Info().Int("final_offset", offset).Msg("ETL pipeline completed")
}

func (p *Pipeline) processOne(idx int, raw igdb_models.IGDBGame) {
	p.logger.Debug().Int("game_index", idx).Str("game_name", raw.Name).Msg("Processing game")
	// Transform
	game, err := p.transformer.Transform(raw)
	if err != nil {
		p.logger.Error().Err(err).Int("game_index", idx).Str("game_name", raw.Name).Msg("Transformation failed")
		return
	}
	// Enrich
	if err := p.enricher.Enrich(game, raw); err != nil {
		p.logger.Warn().Err(err).Int("game_index", idx).Str("game_name", game.Title).Msg("Enrichment encountered issues")
	}
	// Load
	if err := p.loader.Save(game); err != nil {
		p.logger.Error().Err(err).Int("game_index", idx).Str("game_name", game.Title).Msg("Persistence failed")
		return
	}
	p.logger.Debug().Int("game_index", idx).Str("game_name", game.Title).Msg("Game processed successfully")
}
