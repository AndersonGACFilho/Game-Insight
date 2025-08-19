package etl

import (
	"encoding/json"
	"game-data-etl/internal/api/igdb"
	"game-data-etl/internal/domain/entities"
	"game-data-etl/internal/domain/igdb_models"
	"game-data-etl/internal/platform/repositories"
	"github.com/rs/zerolog"
)

// Enricher augments a transformed Game with associated dimension entities.
type Enricher interface {
	Enrich(game *entities.Game, raw igdb_models.IGDBGame) error
}

// DimensionEnricher loads (and if missing, upserts) dimension entities via repositories based on source refs in IGDB payload.
type DimensionEnricher struct {
	dimRepo *repositories.DimensionRepository
	client  *igdb.Client
	logger  zerolog.Logger
}

func NewDimensionEnricher(dimRepo *repositories.DimensionRepository, client *igdb.Client, logger zerolog.Logger) *DimensionEnricher {
	return &DimensionEnricher{dimRepo: dimRepo, client: client, logger: logger}
}

func (e *DimensionEnricher) Enrich(game *entities.Game, raw igdb_models.IGDBGame) error {
	// Order: fetch or create each dimension list
	game.Genres = fetchEnsure(e, "genres", raw.Genres, e.dimRepo.GenresBySourceRefs, e.dimRepo.UpsertGenres)
	game.Themes = fetchEnsure(e, "themes", raw.Themes, e.dimRepo.ThemesBySourceRefs, e.dimRepo.UpsertThemes)
	game.Keywords = fetchEnsure(e, "keywords", raw.Keywords, e.dimRepo.KeywordsBySourceRefs, e.dimRepo.UpsertKeywords)
	game.GameModes = fetchEnsure(e, "game_modes", raw.GameModes, e.dimRepo.GameModesBySourceRefs, e.dimRepo.UpsertGameModes)
	game.Perspectives = fetchEnsure(e, "player_perspectives", raw.PlayersPerspectives, e.dimRepo.PerspectivesBySourceRefs, e.dimRepo.UpsertPerspectives)
	game.Franchises = fetchEnsure(e, "franchises", raw.Frachises, e.dimRepo.FranchisesBySourceRefs, e.dimRepo.UpsertFranchises)
	// Collection (single)
	if raw.Collection != nil {
		cols := fetchEnsure(e, "collections", []int32{*raw.Collection}, func(refs []int32) ([]entities.Collection, error) {
			results := make([]entities.Collection, 0)
			c, err := e.dimRepo.CollectionBySourceRef(raw.Collection)
			if err != nil {
				return results, err
			}
			if c != nil {
				results = append(results, *c)
			}
			return results, nil
		}, e.dimRepo.UpsertCollections)
		if len(cols) > 0 {
			game.Collection = &cols[0]
			game.CollectionID = &cols[0].ID
		}
	}
	return nil
}

// fetchEnsure retrieves existing dimension entities; if some requested IDs are missing it fetches from IGDB, upserts, and refetches.
func fetchEnsure[T any](e *DimensionEnricher, endpoint string, ids []int32, fetch func([]int32) ([]T, error), upsert func([]igdb_models.IGDBNamedEntity) error) []T {
	if len(ids) == 0 {
		return nil
	}
	existing, err := fetch(ids)
	if err != nil {
		e.logger.Warn().Err(err).Str("endpoint", endpoint).Msg("fetch existing failed")
	}
	missing := diffMissing(ids, existing)
	if len(missing) > 0 {
		// fetch from IGDB
		bytes, err := e.client.GetNamedEntities(endpoint, missing)
		if err != nil {
			e.logger.Warn().Err(err).Str("endpoint", endpoint).Ints("missing", ints32ToInt(missing)).Msg("GetNamedEntities failed")
			return existing
		}
		var named []igdb_models.IGDBNamedEntity
		if err := json.Unmarshal(bytes, &named); err != nil {
			e.logger.Warn().Err(err).Str("endpoint", endpoint).Msg("unmarshal named entities failed")
			return existing
		}
		if err := upsert(named); err != nil {
			e.logger.Warn().Err(err).Str("endpoint", endpoint).Msg("upsert named entities failed")
		}
		// refetch full set
		existing, err = fetch(ids)
		if err != nil {
			e.logger.Warn().Err(err).Str("endpoint", endpoint).Msg("refetch after upsert failed")
		}
	}
	return existing
}

// diffMissing returns ids not present in slice existing (where existing items implement SourceMeta via embedding).
func diffMissing[T any](requested []int32, existing []T) []int32 {
	present := map[int64]struct{}{}
	for _, ex := range existing {
		// use reflection only here to access SourceRef (small volume so acceptable)
		// ex must have field SourceMeta.SourceRef
		// fallback: skip if not accessible
		// reflection avoided for simplicity? We'll attempt type assertion
		// We'll handle known types explicitly
		switch v := any(ex).(type) {
		case entities.Genre:
			present[v.SourceRef] = struct{}{}
		case entities.Theme:
			present[v.SourceRef] = struct{}{}
		case entities.Keyword:
			present[v.SourceRef] = struct{}{}
		case entities.GameMode:
			present[v.SourceRef] = struct{}{}
		case entities.PlayerPerspective:
			present[v.SourceRef] = struct{}{}
		case entities.Franchise:
			present[v.SourceRef] = struct{}{}
		case entities.Collection:
			present[v.SourceRef] = struct{}{}
		}
	}
	missing := make([]int32, 0)
	for _, id := range idsToInt64(requested) {
		if _, ok := present[id]; !ok {
			missing = append(missing, int32(id))
		}
	}
	return missing
}

func idsToInt64(ids []int32) []int64 {
	out := make([]int64, len(ids))
	for i, v := range ids {
		out[i] = int64(v)
	}
	return out
}
func ints32ToInt(ids []int32) []int {
	out := make([]int, len(ids))
	for i, v := range ids {
		out[i] = int(v)
	}
	return out
}
