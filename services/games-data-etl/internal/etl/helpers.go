package etl

import (
	"encoding/json"
	"game-data-etl/internal/domain/entities"
	"game-data-etl/internal/domain/igdb_models"
)

// --- Pointer / value helpers (package scoped) ---
func ptrString(s string) *string {
	if s == "" {
		return nil
	}
	return &s
}
func int32Ptr(v int32) *int32 { return &v }
func int16Ptr(v int16) *int16 { return &v }
func boolPtr(b bool) *bool    { return &b }

// normalizeIGDBImageURL ensures protocol https and strips
// double slashes.
func normalizeIGDBImageURL(u string) string {
	if u == "" {
		return u
	}
	if len(u) >= 2 && u[0:2] == "//" {
		return "https:" + u
	}
	if len(u) >= 7 && u[0:7] == "http://" {
		return "https://" + u[7:]
	}
	return u
}

// fetchEnsure retrieves existing dimension entities; if
// some requested IDs are missing it fetches from IGDB,
// upserts, and refetches.
// T is a concrete entity slice type returned by fetch func;
// presence detection relies on SourceRef in diffMissing.
func fetchEnsure[T any](
	ctx *EnrichmentContext,
	endpoint string,
	ids []int32,
	fetch func([]int32) ([]T, error),
	upsert func([]igdb_models.IGDBNamedEntity) error,
) []T {
	if len(ids) == 0 {
		return nil
	}
	existing, err := fetch(ids)
	if err != nil {
		ctx.log.Warn().Err(err).Str("endpoint", endpoint).Msg("fetch existing failed")
	}
	missing := diffMissing(ids, existing)
	if len(missing) > 0 {
		bytes, err := ctx.client.GetNamedEntities(endpoint,
			missing)
		if err != nil {
			ctx.log.Warn().Err(err).Str("endpoint", endpoint).Msg("GetNamedEntities failed")
			return existing
		}
		var named []igdb_models.IGDBNamedEntity
		if err := json.Unmarshal(bytes, &named); err != nil {
			ctx.log.Warn().Err(err).Str("endpoint", endpoint).Msg("unmarshal named entities failed")
			return existing
		}
		if err := upsert(named); err != nil {
			ctx.log.Warn().Err(err).Str("endpoint", endpoint).Msg("upsert named entities failed")
		}
		existing, err = fetch(ids)
		if err != nil {
			ctx.log.Warn().Err(err).Str("endpoint", endpoint).Msg("refetch after upsert failed")
		}
	}
	return existing
}

// diffMissing returns source refs from requested not
// present in existing slice.
func diffMissing[T any](
	requested []int32,
	existing []T,
) []int32 {
	present := map[int64]struct{}{}
	for _, ex := range existing {
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
	for _, id := range requested {
		if _, ok := present[int64(id)]; !ok {
			missing = append(missing, id)
		}
	}
	return missing
}
