package etl

import (
	"encoding/json"
	"game-data-etl/internal/api/igdb"
	"game-data-etl/internal/domain/entities"
	"game-data-etl/internal/domain/igdb_models"
	"game-data-etl/internal/platform/repositories"
	"github.com/google/uuid"
	"github.com/rs/zerolog"
	"strings"
	"time"
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
	// caches to reduce duplicate network calls within a run
	platformCache        map[int64]struct{}
	companyCache         map[int64]struct{}
	altNameCache         map[int64]igdb_models.IGDBAlternativeName
	releaseDateCache     map[int64]igdb_models.IGDBReleaseDate
	mediaCache           map[string]map[int64]igdb_models.IGDBMediaAsset // key: type (cover,screenshot,artwork)
	involvedCompanyCache map[int64]igdb_models.IGDBInvolvedCompany
	multiModeCache       map[int64]igdb_models.IGDBMultiplayerMode
	languageSupportCache map[int64]igdb_models.IGDBLanguageSupport
	ageRatingCache       map[int64]igdb_models.IGDBAgeRating
	achievementCache     map[int64]igdb_models.IGDBAchievement
	websiteCache         map[int64]igdb_models.IGDBWebsite
	videoCache           map[int64]igdb_models.IGDBGameVideo
}

func NewDimensionEnricher(dimRepo *repositories.DimensionRepository, client *igdb.Client, logger zerolog.Logger) *DimensionEnricher {
	return &DimensionEnricher{dimRepo: dimRepo, client: client, logger: logger,
		platformCache:        map[int64]struct{}{},
		companyCache:         map[int64]struct{}{},
		altNameCache:         map[int64]igdb_models.IGDBAlternativeName{},
		releaseDateCache:     map[int64]igdb_models.IGDBReleaseDate{},
		mediaCache:           map[string]map[int64]igdb_models.IGDBMediaAsset{"covers": {}, "screenshots": {}, "artworks": {}},
		involvedCompanyCache: map[int64]igdb_models.IGDBInvolvedCompany{},
		multiModeCache:       map[int64]igdb_models.IGDBMultiplayerMode{},
		languageSupportCache: map[int64]igdb_models.IGDBLanguageSupport{},
		ageRatingCache:       map[int64]igdb_models.IGDBAgeRating{},
		achievementCache:     map[int64]igdb_models.IGDBAchievement{},
		websiteCache:         map[int64]igdb_models.IGDBWebsite{},
		videoCache:           map[int64]igdb_models.IGDBGameVideo{},
	}
}

func (e *DimensionEnricher) Enrich(game *entities.Game, raw igdb_models.IGDBGame) error {
	// named dimensions
	game.Genres = fetchEnsure(e, "genres", raw.Genres, e.dimRepo.GenresBySourceRefs, e.dimRepo.UpsertGenres)
	game.Themes = fetchEnsure(e, "themes", raw.Themes, e.dimRepo.ThemesBySourceRefs, e.dimRepo.UpsertThemes)
	game.Keywords = fetchEnsure(e, "keywords", raw.Keywords, e.dimRepo.KeywordsBySourceRefs, e.dimRepo.UpsertKeywords)
	game.GameModes = fetchEnsure(e, "game_modes", raw.GameModes, e.dimRepo.GameModesBySourceRefs, e.dimRepo.UpsertGameModes)
	game.Perspectives = fetchEnsure(e, "player_perspectives", raw.PlayersPerspectives, e.dimRepo.PerspectivesBySourceRefs, e.dimRepo.UpsertPerspectives)
	game.Franchises = fetchEnsure(e, "franchises", raw.Frachises, e.dimRepo.FranchisesBySourceRefs, e.dimRepo.UpsertFranchises)
	game.Collections = fetchEnsure(e, "collections", raw.Collections, e.dimRepo.CollectionsBySourceRefs, e.dimRepo.UpsertCollections)
	// extended entities
	if err := e.enrichPlatforms(game, raw); err != nil {
		e.logger.Warn().Err(err).Msg("platform enrichment failed")
	}
	if err := e.enrichCompanies(game, raw); err != nil {
		e.logger.Warn().Err(err).Msg("company enrichment failed")
	}
	if err := e.enrichAltNames(game, raw); err != nil {
		e.logger.Warn().Err(err).Msg("alt names enrichment failed")
	}
	if err := e.enrichReleaseDates(game, raw); err != nil {
		e.logger.Warn().Err(err).Msg("release dates enrichment failed")
	}
	if err := e.enrichMedia(game, raw); err != nil {
		e.logger.Warn().Err(err).Msg("media enrichment failed")
	}
	if err := e.enrichMultiplayerModes(game, raw); err != nil {
		e.logger.Warn().Err(err).Msg("multiplayer mode enrichment failed")
	}
	if err := e.enrichLanguageSupports(game, raw); err != nil {
		e.logger.Warn().Err(err).Msg("language support enrichment failed")
	}
	if err := e.enrichAgeRatings(game, raw); err != nil {
		e.logger.Warn().Err(err).Msg("age ratings enrichment failed")
	}
	if err := e.enrichAchievements(game, raw); err != nil {
		e.logger.Warn().Err(err).Msg("achievement enrichment failed")
	}
	if err := e.enrichWebsites(game, raw); err != nil {
		e.logger.Warn().Err(err).Msg("website enrichment failed")
	}
	if err := e.enrichVideos(game, raw); err != nil {
		e.logger.Warn().Err(err).Msg("video enrichment failed")
	}
	return nil
}

// Platform enrichment: fetch missing platforms, upsert, attach (in-memory only; join table not persisted yet)
func (e *DimensionEnricher) enrichPlatforms(game *entities.Game, raw igdb_models.IGDBGame) error {
	missing := make([]int32, 0)
	for _, id := range raw.Platforms {
		if _, ok := e.platformCache[int64(id)]; !ok {
			missing = append(missing, id)
		}
	}
	if len(missing) > 0 {
		bytes, err := e.client.GetEntitiesByIDs("platforms", missing, "id,name,abbreviation,generation,category,created_at,updated_at")
		if err != nil {
			return err
		}
		var plats []igdb_models.IGDBPlatform
		if err := json.Unmarshal(bytes, &plats); err != nil { // single unmarshal (duplicate removed)
			return err
		}
		if err := e.dimRepo.UpsertPlatforms(plats); err != nil {
			return err
		}
		for _, p := range plats {
			e.platformCache[p.ID] = struct{}{}
		}
	}
	// attach platforms to game (many2many) after ensuring they exist
	if len(raw.Platforms) > 0 {
		plats, err := e.dimRepo.PlatformsBySourceRefs(raw.Platforms)
		if err != nil {
			e.logger.Warn().Err(err).Msg("fetch platforms for game association failed")
		} else {
			game.Platforms = plats
		}
	}
	return nil
}

func (e *DimensionEnricher) enrichCompanies(game *entities.Game, raw igdb_models.IGDBGame) error {
	missingInv := make([]int32, 0)
	for _, id := range raw.InvolvedCompanies {
		if _, ok := e.involvedCompanyCache[int64(id)]; !ok {
			missingInv = append(missingInv, id)
		}
	}
	companyIDsForResolution := make([]int64, 0)
	if len(missingInv) > 0 {
		bytes, err := e.client.GetEntitiesByIDs("involved_companies", missingInv, "id,company,game,developer,publisher,porting,supporting,created_at,updated_at")
		if err != nil {
			return err
		}
		var invs []igdb_models.IGDBInvolvedCompany
		if err := json.Unmarshal(bytes, &invs); err != nil {
			return err
		}
		for _, ic := range invs {
			e.involvedCompanyCache[ic.ID] = ic
			if _, seen := e.companyCache[ic.Company]; !seen {
				companyIDsForResolution = append(companyIDsForResolution, ic.Company)
			}
		}
		if len(companyIDsForResolution) > 0 {
			c32 := make([]int32, 0, len(companyIDsForResolution))
			for _, id := range companyIDsForResolution {
				c32 = append(c32, int32(id))
			}
			b2, err := e.client.GetEntitiesByIDs("companies", c32, "id,name,country,description,created_at,updated_at")
			if err != nil {
				return err
			}
			var comps []igdb_models.IGDBCompany
			if err := json.Unmarshal(b2, &comps); err != nil {
				return err
			}
			if err := e.dimRepo.UpsertCompanies(comps); err != nil {
				return err
			}
			for _, c := range comps {
				e.companyCache[c.ID] = struct{}{}
			}
		}
	}
	// resolve company UUIDs mapping
	companySourceSet := map[int64]struct{}{}
	for _, id := range raw.InvolvedCompanies {
		if ic, ok := e.involvedCompanyCache[int64(id)]; ok {
			companySourceSet[ic.Company] = struct{}{}
		}
	}
	companyRefs := make([]int64, 0, len(companySourceSet))
	for k := range companySourceSet {
		companyRefs = append(companyRefs, k)
	}
	companyUUIDs, err := e.dimRepo.CompanyIDsBySourceRefs(companyRefs)
	if err != nil {
		e.logger.Warn().Err(err).Msg("company UUID resolution failed")
	}
	ics := make([]entities.InvolvedCompany, 0)
	for _, id := range raw.InvolvedCompanies {
		icRaw, ok := e.involvedCompanyCache[int64(id)]
		if !ok {
			continue
		}
		if icRaw.Game != game.SourceMeta.SourceRef {
			continue
		}
		compUUID, okc := companyUUIDs[icRaw.Company]
		if !okc {
			continue
		}
		ics = append(ics, entities.InvolvedCompany{
			ID:         uuid.New(),
			SourceMeta: entities.SourceMeta{SourceRef: icRaw.ID, CreatedAtSource: icRaw.CreatedAt.Time, UpdatedAtSource: icRaw.UpdatedAt.Time},
			GameID:     game.ID,
			CompanyID:  compUUID,
			Developer:  icRaw.Developer,
			Publisher:  icRaw.Publisher,
			Porting:    icRaw.Porting,
			Supporting: icRaw.Supporting,
		})
	}
	game.Companies = ics
	return nil
}

func (e *DimensionEnricher) enrichAltNames(game *entities.Game, raw igdb_models.IGDBGame) error {
	missing := make([]int32, 0)
	for _, id := range raw.AlternativeNames {
		if _, ok := e.altNameCache[int64(id)]; !ok {
			missing = append(missing, id)
		}
	}
	if len(missing) > 0 {
		bytes, err := e.client.GetEntitiesByIDs("alternative_names", missing, "id,game,name,comment")
		if err != nil {
			return err
		}
		var alts []igdb_models.IGDBAlternativeName
		if err := json.Unmarshal(bytes, &alts); err != nil {
			return err
		}
		for _, a := range alts {
			e.altNameCache[a.ID] = a
		}
	}
	now := time.Now().UTC()
	names := make([]entities.GameAltName, 0)
	for _, id := range raw.AlternativeNames {
		an, ok := e.altNameCache[int64(id)]
		if !ok {
			continue
		}
		if an.Game != game.SourceMeta.SourceRef {
			continue
		}
		created := an.CreatedAt.Time
		if created.IsZero() {
			created = now
		}
		updated := an.UpdatedAt.Time
		if updated.IsZero() {
			updated = now
		}
		names = append(names, entities.GameAltName{
			ID:         uuid.New(),
			SourceMeta: entities.SourceMeta{SourceRef: an.ID, CreatedAtSource: created, UpdatedAtSource: updated},
			GameID:     game.ID,
			Name:       an.Name,
			Comment:    ptrString(an.Comment),
		})
	}
	game.GameAltNames = names
	return nil
}

func (e *DimensionEnricher) enrichReleaseDates(game *entities.Game, raw igdb_models.IGDBGame) error {
	missing := make([]int32, 0)
	for _, id := range raw.ReleaseDates {
		if _, ok := e.releaseDateCache[int64(id)]; !ok {
			missing = append(missing, id)
		}
	}
	if len(missing) > 0 {
		bytes, err := e.client.GetEntitiesByIDs("release_dates", missing, "id,game,platform,date,region,category,status,created_at,updated_at")
		if err != nil {
			return err
		}
		var rds []igdb_models.IGDBReleaseDate
		if err := json.Unmarshal(bytes, &rds); err != nil {
			return err
		}
		for _, rd := range rds {
			e.releaseDateCache[rd.ID] = rd
		}
	}
	// map platform source->uuid
	platformSourceSet := map[int64]struct{}{}
	for _, id := range raw.ReleaseDates {
		if rd, ok := e.releaseDateCache[int64(id)]; ok {
			platformSourceSet[rd.Platform] = struct{}{}
		}
	}
	ps := make([]int64, 0, len(platformSourceSet))
	for k := range platformSourceSet {
		ps = append(ps, k)
	}
	platformUUIDs, err := e.dimRepo.PlatformIDsBySourceRefs(ps)
	if err != nil {
		e.logger.Warn().Err(err).Msg("platform id resolution failed for release dates")
	}
	rs := make([]entities.ReleaseDate, 0)
	for _, id := range raw.ReleaseDates {
		rd, ok := e.releaseDateCache[int64(id)]
		if !ok {
			continue
		}
		if rd.Game != game.SourceMeta.SourceRef {
			continue
		}
		platUUID, okp := platformUUIDs[rd.Platform]
		if !okp {
			continue
		}
		var datePtr *time.Time
		if rd.Date > 0 {
			t := time.Unix(rd.Date, 0).UTC()
			datePtr = &t
		}
		// region, category, status
		var regionPtr *entities.Region
		if rd.Region > 0 {
			rv := entities.Region(rd.Region)
			regionPtr = &rv
		}
		var catPtr *int16
		if rd.Category > 0 {
			cv := int16(rd.Category)
			catPtr = &cv
		}
		var statusPtr *entities.ReleaseStatus
		if rd.Status > 0 {
			sv := entities.ReleaseStatus(rd.Status)
			statusPtr = &sv
		}
		rs = append(rs, entities.ReleaseDate{
			ID:         uuid.New(),
			SourceMeta: entities.SourceMeta{SourceRef: rd.ID, CreatedAtSource: rd.CreatedAt.Time, UpdatedAtSource: rd.UpdatedAt.Time},
			GameID:     game.ID, PlatformID: platUUID, Date: datePtr, Region: regionPtr, Category: catPtr, Status: statusPtr,
		})
	}
	game.ReleaseDates = rs
	return nil
}

func (e *DimensionEnricher) enrichMedia(game *entities.Game, raw igdb_models.IGDBGame) error {
	coverIDs := []int32{}
	if raw.Cover != nil {
		coverIDs = append(coverIDs, *raw.Cover)
	}
	if err := e.fetchMediaType("covers", coverIDs); err != nil {
		return err
	}
	if err := e.fetchMediaType("screenshots", raw.Screenshots); err != nil {
		return err
	}
	if err := e.fetchMediaType("artworks", raw.Artworks); err != nil {
		return err
	}
	assets := make([]entities.MediaAsset, 0)
	now := time.Now().UTC()
	add := func(kind string, ids []int32) {
		for _, id := range ids {
			if ma, ok := e.mediaCache[kind][int64(id)]; ok {
				if ma.Game != game.SourceMeta.SourceRef {
					continue
				}
				url := normalizeIGDBImageURL(ma.URL)
				assets = append(assets, entities.MediaAsset{ID: uuid.New(), SourceMeta: entities.SourceMeta{SourceRef: ma.ID, CreatedAtSource: now, UpdatedAtSource: now}, GameID: &game.ID, Type: strings.ToUpper(strings.TrimSuffix(kind, "s")), Width: int32Ptr(ma.Width), Height: int32Ptr(ma.Height), URL: url, Checksum: ptrString(ma.Checksum)})
			}
		}
	}
	add("covers", coverIDs)
	add("screenshots", raw.Screenshots)
	add("artworks", raw.Artworks)
	game.MediaAssets = assets
	return nil
}

func (e *DimensionEnricher) fetchMediaType(kind string, ids []int32) error {
	missing := make([]int32, 0)
	cache := e.mediaCache[kind]
	for _, id := range ids {
		if _, ok := cache[int64(id)]; !ok {
			missing = append(missing, id)
		}
	}
	if len(missing) == 0 {
		return nil
	}
	endpoint := kind // IGDB endpoints plural: covers, screenshots, artworks
	// Image endpoints do NOT expose created_at / updated_at per IGDB; request only supported fields
	bytes, err := e.client.GetEntitiesByIDs(endpoint, missing, "id,game,width,height,url,checksum")
	if err != nil {
		return err
	}
	var items []igdb_models.IGDBMediaAsset
	if err := json.Unmarshal(bytes, &items); err != nil {
		return err
	}
	for _, it := range items {
		cache[it.ID] = it
	}
	return nil
}

func (e *DimensionEnricher) enrichMultiplayerModes(game *entities.Game, raw igdb_models.IGDBGame) error {
	missing := make([]int32, 0)
	for _, id := range raw.MultiplayerModes {
		if _, ok := e.multiModeCache[int64(id)]; !ok {
			missing = append(missing, id)
		}
	}
	if len(missing) > 0 {
		// Remove created_at/updated_at (not supported by endpoint -> 400 Invalid Field)
		bytes, err := e.client.GetEntitiesByIDs("multiplayer_modes", missing, "id,game,campaigncoop,dropin,lancoop,offlinecoop,offlinecoopmax,offlinemax,onlinecoop,onlinecoopmax,onlinemax,splitscreen,splitscreenonline")
		if err != nil {
			return err
		}
		var modes []igdb_models.IGDBMultiplayerMode
		if err := json.Unmarshal(bytes, &modes); err != nil {
			return err
		}
		for _, m := range modes {
			e.multiModeCache[m.ID] = m
		}
	}
	now := time.Now().UTC()
	mm := make([]entities.MultiplayerMode, 0)
	for _, id := range raw.MultiplayerModes {
		if m, ok := e.multiModeCache[int64(id)]; ok && m.Game == game.SourceMeta.SourceRef {
			created := m.CreatedAt.Time
			if created.IsZero() {
				created = now
			}
			updated := m.UpdatedAt.Time
			if updated.IsZero() {
				updated = now
			}
			mm = append(mm, entities.MultiplayerMode{ID: uuid.New(), SourceMeta: entities.SourceMeta{SourceRef: m.ID, CreatedAtSource: created, UpdatedAtSource: updated}, GameID: game.ID,
				CampaignCoop: boolPtr(m.CampaignCoop), DropIn: boolPtr(m.DropIn), LANCoop: boolPtr(m.LanCoop), OfflineCoop: boolPtr(m.OfflineCoop), OfflineCoopMax: int16Ptr(m.OfflineCoopMax), OfflineMax: int16Ptr(m.OfflineMax), OnlineCoop: boolPtr(m.OnlineCoop), OnlineCoopMax: int16Ptr(m.OnlineCoopMax), OnlineMax: int16Ptr(m.OnlineMax), Splitscreen: boolPtr(m.Splitscreen), SplitscreenOnline: boolPtr(m.SplitscreenOnline)})
		}
	}
	game.MultiplayerModes = mm
	return nil
}

func (e *DimensionEnricher) enrichLanguageSupports(game *entities.Game, raw igdb_models.IGDBGame) error {
	missing := make([]int32, 0)
	for _, id := range raw.LanguageSupports {
		if _, ok := e.languageSupportCache[int64(id)]; !ok {
			missing = append(missing, id)
		}
	}
	if len(missing) > 0 {
		bytes, err := e.client.GetEntitiesByIDs("language_supports", missing, "id,game,language,language_support_type,created_at,updated_at")
		if err != nil {
			return err
		}
		var ls []igdb_models.IGDBLanguageSupport
		if err := json.Unmarshal(bytes, &ls); err != nil {
			return err
		}
		for _, l := range ls {
			e.languageSupportCache[l.ID] = l
		}
	}
	supports := make([]entities.GameLanguageSupport, 0)
	for _, id := range raw.LanguageSupports {
		if l, ok := e.languageSupportCache[int64(id)]; ok && l.Game == game.SourceMeta.SourceRef {
			supports = append(supports, entities.GameLanguageSupport{ID: uuid.New(), SourceMeta: entities.SourceMeta{SourceRef: l.ID, CreatedAtSource: l.CreatedAt.Time, UpdatedAtSource: l.UpdatedAt.Time}, GameID: game.ID, LanguageCode: int(l.Language), SupportTypeCode: int(l.Type)})
		}
	}
	game.LanguageSupports = supports
	return nil
}

// enrichAgeRatings fetches age rating records and maps them to entity AgeRating instances.
func (e *DimensionEnricher) enrichAgeRatings(game *entities.Game, raw igdb_models.IGDBGame) error {
	if len(raw.AgeRatings) == 0 {
		return nil
	}
	// fetch existing by source refs
	existing, err := e.dimRepo.AgeRatingsBySourceRefs(raw.AgeRatings)
	if err != nil {
		e.logger.Warn().Err(err).Msg("fetch existing age ratings failed")
	}
	present := map[int64]struct{}{}
	for _, ar := range existing {
		present[ar.SourceRef] = struct{}{}
	}
	missing := make([]int32, 0)
	for _, id := range raw.AgeRatings {
		if _, ok := present[int64(id)]; !ok {
			missing = append(missing, id)
		}
	}
	if len(missing) > 0 {
		// Some IGDB environments do not expose created_at/updated_at for age_ratings – request minimal fields first.
		fieldsPrimary := "id,category,rating,synopsis,created_at,updated_at"
		fieldsFallback := "id,category,rating,synopsis"
		bytes, err := e.client.GetEntitiesByIDs("age_ratings", missing, fieldsPrimary)
		if err != nil && strings.Contains(err.Error(), "Invalid field name") {
			// retry without timestamps
			bytes, err = e.client.GetEntitiesByIDs("age_ratings", missing, fieldsFallback)
		}
		if err != nil {
			return err
		}
		var apiAR []igdb_models.IGDBAgeRating
		if err := json.Unmarshal(bytes, &apiAR); err != nil {
			return err
		}
		if err := e.dimRepo.UpsertAgeRatings(apiAR); err != nil {
			return err
		}
		// refetch full set after upsert to ensure we have all
		existing, err = e.dimRepo.AgeRatingsBySourceRefs(raw.AgeRatings)
		if err != nil {
			return err
		}
	}
	game.AgeRatings = existing
	return nil
}

// Achievements: fetch missing, upsert, attach (in-memory only; join table not persisted yet)
func (e *DimensionEnricher) enrichAchievements(game *entities.Game, raw igdb_models.IGDBGame) error {
	if len(raw.Achievements) == 0 {
		return nil
	}
	missing := make([]int32, 0)
	for _, id := range raw.Achievements {
		if _, ok := e.achievementCache[int64(id)]; !ok {
			missing = append(missing, id)
		}
	}
	if len(missing) > 0 {
		bytes, err := e.client.GetEntitiesByIDs("achievements", missing, "id,game,name,slug,description,category,order,points,hidden,unlocked_icon,locked_icon,checksum,created_at,updated_at")
		if err != nil { // fallback without timestamps
			if strings.Contains(err.Error(), "Invalid field") {
				bytes, err = e.client.GetEntitiesByIDs("achievements", missing, "id,game,name,slug,description,category,order,points,hidden,unlocked_icon,locked_icon,checksum")
			}
		}
		if err != nil {
			return err
		}
		var items []igdb_models.IGDBAchievement
		if err := json.Unmarshal(bytes, &items); err != nil {
			return err
		}
		for _, it := range items {
			e.achievementCache[it.ID] = it
		}
	}
	ach := make([]entities.GameAchievement, 0, len(raw.Achievements))
	for _, id := range raw.Achievements {
		it, ok := e.achievementCache[int64(id)]
		if !ok || it.Game != game.SourceMeta.SourceRef {
			continue
		}
		var cat *entities.AchievementCategory
		if it.Category != 0 {
			c := entities.AchievementCategory(it.Category)
			cat = &c
		}
		ach = append(ach, entities.GameAchievement{ID: uuid.New(), SourceMeta: entities.SourceMeta{SourceRef: it.ID, CreatedAtSource: it.CreatedAt.Time, UpdatedAtSource: it.UpdatedAt.Time}, GameID: game.ID, Name: it.Name, Slug: it.Slug, Description: ptrString(it.Description), Category: cat, OrderIndex: int32Ptr(it.Order), Points: int32Ptr(it.Points), Secret: it.Hidden, Checksum: ptrString(it.Checksum)})
	}
	game.Achievements = ach
	return nil
}

// Websites: fetch missing, upsert, attach (in-memory only; join table not persisted yet)
func (e *DimensionEnricher) enrichWebsites(game *entities.Game, raw igdb_models.IGDBGame) error {
	if len(raw.Websites) == 0 {
		return nil
	}
	missing := make([]int32, 0)
	for _, id := range raw.Websites {
		if _, ok := e.websiteCache[int64(id)]; !ok {
			missing = append(missing, id)
		}
	}
	if len(missing) > 0 {
		bytes, err := e.client.GetEntitiesByIDs("websites", missing, "id,game,category,url,trusted,checksum,created_at,updated_at")
		if err != nil && strings.Contains(err.Error(), "Invalid field") {
			bytes, err = e.client.GetEntitiesByIDs("websites", missing, "id,game,category,url,trusted,checksum")
		}
		if err != nil {
			return err
		}
		var items []igdb_models.IGDBWebsite
		if err := json.Unmarshal(bytes, &items); err != nil {
			return err
		}
		for _, it := range items {
			e.websiteCache[it.ID] = it
		}
	}
	ws := make([]entities.GameWebsite, 0, len(raw.Websites))
	for _, id := range raw.Websites {
		it, ok := e.websiteCache[int64(id)]
		if !ok || it.Game != game.SourceMeta.SourceRef {
			continue
		}
		trusted := boolPtr(it.Trusted)
		cat := entities.WebsiteCategory(it.Category)
		ws = append(ws, entities.GameWebsite{ID: uuid.New(), SourceMeta: entities.SourceMeta{SourceRef: it.ID, CreatedAtSource: it.CreatedAt.Time, UpdatedAtSource: it.UpdatedAt.Time}, GameID: game.ID, Category: cat, URL: it.URL, Trusted: trusted, Checksum: ptrString(it.Checksum)})
	}
	game.Websites = ws
	return nil
}

// Videos: fetch missing, upsert, attach (in-memory only; join table not persisted yet)
func (e *DimensionEnricher) enrichVideos(game *entities.Game, raw igdb_models.IGDBGame) error {
	if len(raw.Videos) == 0 {
		return nil
	}
	missing := make([]int32, 0)
	for _, id := range raw.Videos {
		if _, ok := e.videoCache[int64(id)]; !ok {
			missing = append(missing, id)
		}
	}
	if len(missing) > 0 {
		bytes, err := e.client.GetEntitiesByIDs("game_videos", missing, "id,game,name,video_id,checksum,created_at,updated_at")
		if err != nil && strings.Contains(err.Error(), "Invalid field") {
			bytes, err = e.client.GetEntitiesByIDs("game_videos", missing, "id,game,name,video_id,checksum")
		}
		if err != nil {
			return err
		}
		var items []igdb_models.IGDBGameVideo
		if err := json.Unmarshal(bytes, &items); err != nil {
			return err
		}
		for _, it := range items {
			e.videoCache[it.ID] = it
		}
	}
	vids := make([]entities.GameVideo, 0, len(raw.Videos))
	for _, id := range raw.Videos {
		it, ok := e.videoCache[int64(id)]
		if !ok || it.Game != game.SourceMeta.SourceRef {
			continue
		}
		vids = append(vids, entities.GameVideo{ID: uuid.New(), SourceMeta: entities.SourceMeta{SourceRef: it.ID, CreatedAtSource: it.CreatedAt.Time, UpdatedAtSource: it.UpdatedAt.Time}, GameID: game.ID, Name: it.Name, VideoID: it.VideoID, Checksum: ptrString(it.Checksum)})
	}
	game.Videos = vids
	return nil
}

// C

// Add generic fetchEnsure & diffMissing helpers (previously removed) for named dimension enrichment.

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
		// refetch after upsert
		existing, err = fetch(ids)
		if err != nil {
			e.logger.Warn().Err(err).Str("endpoint", endpoint).Msg("refetch after upsert failed")
		}
	}
	return existing
}

// diffMissing returns ids not present in existing slice (by SourceRef) for supported entity types.
func diffMissing[T any](requested []int32, existing []T) []int32 {
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

func ints32ToInt(in []int32) []int {
	out := make([]int, len(in))
	for i, v := range in {
		out[i] = int(v)
	}
	return out
}

// helper pointer builders for enrichment specific types
func ptrString(s string) *string {
	if s == "" {
		return nil
	}
	return &s
}
func int32Ptr(v int32) *int32 { return &v }
func int16Ptr(v int16) *int16 { return &v }
func boolPtr(b bool) *bool    { return &b }

func normalizeIGDBImageURL(u string) string {
	if u == "" {
		return u
	}
	if strings.HasPrefix(u, "//") {
		return "https:" + u
	}
	if strings.HasPrefix(u, "http://") {
		return "https://" + strings.TrimPrefix(u, "http://")
	}
	return u
}
