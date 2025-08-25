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

// Enricher is the high-level enrichment contract used by
// the Pipeline. Implemented by CompositeEnricher.
type Enricher interface {
	Enrich(*entities.Game, igdb_models.IGDBGame) error
}

// GameEnrichment defines a focused enrichment step.
type GameEnrichment interface {
	Enrich(*entities.Game, igdb_models.IGDBGame) error
	Name() string
}

// EnrichmentContext holds shared dependencies and caches
// across enrichment steps.
type EnrichmentContext struct {
	dimRepo *repositories.DimensionRepository
	client  *igdb.Client
	log     zerolog.Logger
	// caches (shared to reduce duplicate calls)
	platformCache             map[int64]struct{}
	companyCache              map[int64]struct{}
	altNameCache              map[int64]igdb_models.IGDBAlternativeName
	releaseDateCache          map[int64]igdb_models.IGDBReleaseDate
	mediaCache                map[string]map[int64]igdb_models.IGDBMediaAsset
	involvedCompanyCache      map[int64]igdb_models.IGDBInvolvedCompany
	multiModeCache            map[int64]igdb_models.IGDBMultiplayerMode
	languageSupportCache      map[int64]igdb_models.IGDBLanguageSupport
	ageRatingCache            map[int64]igdb_models.IGDBAgeRating
	achievementCache          map[int64]igdb_models.IGDBAchievement
	websiteCache              map[int64]igdb_models.IGDBWebsite
	videoCache                map[int64]igdb_models.IGDBGameVideo
	ageRatingOrgCache         map[int64]igdb_models.IGDBAgeRatingOrganization
	ageRatingContentDescCache map[int64]igdb_models.IGDBAgeRatingContentDescriptionV2
}

func newEnrichmentContext(
	dim *repositories.DimensionRepository,
	client *igdb.Client,
	log zerolog.Logger,
) *EnrichmentContext {
	return &EnrichmentContext{dimRepo: dim, client: client,
		log:              log,
		platformCache:    map[int64]struct{}{},
		companyCache:     map[int64]struct{}{},
		altNameCache:     map[int64]igdb_models.IGDBAlternativeName{},
		releaseDateCache: map[int64]igdb_models.IGDBReleaseDate{},
		mediaCache: map[string]map[int64]igdb_models.IGDBMediaAsset{
			"covers":      {},
			"screenshots": {},
			"artworks":    {},
		},
		involvedCompanyCache:      map[int64]igdb_models.IGDBInvolvedCompany{},
		multiModeCache:            map[int64]igdb_models.IGDBMultiplayerMode{},
		languageSupportCache:      map[int64]igdb_models.IGDBLanguageSupport{},
		ageRatingCache:            map[int64]igdb_models.IGDBAgeRating{},
		achievementCache:          map[int64]igdb_models.IGDBAchievement{},
		websiteCache:              map[int64]igdb_models.IGDBWebsite{},
		videoCache:                map[int64]igdb_models.IGDBGameVideo{},
		ageRatingOrgCache:         map[int64]igdb_models.IGDBAgeRatingOrganization{},
		ageRatingContentDescCache: map[int64]igdb_models.IGDBAgeRatingContentDescriptionV2{},
	}
}

// CompositeEnricher orchestrates multiple GameEnrichment
// steps.
type CompositeEnricher struct {
	steps []GameEnrichment
	log   zerolog.Logger
}

func NewCompositeEnricher(
	dim *repositories.DimensionRepository,
	client *igdb.Client,
	log zerolog.Logger,
) *CompositeEnricher {
	ctx := newEnrichmentContext(dim, client, log)
	steps := []GameEnrichment{
		&namedDimensionsEnrichment{ctx},
		&platformsEnrichment{ctx},
		&companiesEnrichment{ctx},
		&altNamesEnrichment{ctx},
		&releaseDatesEnrichment{ctx},
		&mediaEnrichment{ctx},
		&multiplayerModesEnrichment{ctx},
		&languageSupportsEnrichment{ctx},
		&ageRatingsEnrichment{ctx},
		&achievementsEnrichment{ctx},
		&websitesEnrichment{ctx},
		&videosEnrichment{ctx},
	}
	return &CompositeEnricher{steps: steps, log: log}
}

// Enrich implements Enricher interface.
func (c *CompositeEnricher) Enrich(
	g *entities.Game,
	raw igdb_models.IGDBGame,
) error {
	for _, s := range c.steps {
		if err := s.Enrich(g, raw); err != nil {
			c.log.Warn().Err(err).Str(
				"step", s.Name(),
			).Str(
				"game", g.Title,
			).Msg("enrichment step failed")
		}
	}
	return nil
}

// ===== Individual enrichment step implementations =====

// namedDimensionsEnrichment handles simple named entities
// (genres, themes, etc.)
type namedDimensionsEnrichment struct {
	ctx *EnrichmentContext
}

func (n *namedDimensionsEnrichment) Name() string {
	return "named_dimensions"
}
func (n *namedDimensionsEnrichment) Enrich(
	game *entities.Game,
	raw igdb_models.IGDBGame,
) error {
	game.Genres = fetchEnsure(
		n.ctx,
		"genres",
		raw.Genres,
		n.ctx.dimRepo.GenresBySourceRefs,
		n.ctx.dimRepo.UpsertGenres,
	)
	game.Themes = fetchEnsure(
		n.ctx,
		"themes",
		raw.Themes,
		n.ctx.dimRepo.ThemesBySourceRefs,
		n.ctx.dimRepo.UpsertThemes,
	)
	game.Keywords = fetchEnsure(
		n.ctx,
		"keywords",
		raw.Keywords,
		n.ctx.dimRepo.KeywordsBySourceRefs,
		n.ctx.dimRepo.UpsertKeywords,
	)
	game.GameModes = fetchEnsure(
		n.ctx,
		"game_modes",
		raw.GameModes,
		n.ctx.dimRepo.GameModesBySourceRefs,
		n.ctx.dimRepo.UpsertGameModes,
	)
	game.Perspectives = fetchEnsure(
		n.ctx,
		"player_perspectives",
		raw.PlayersPerspectives,
		n.ctx.dimRepo.PerspectivesBySourceRefs,
		n.ctx.dimRepo.UpsertPerspectives,
	)
	game.Franchises = fetchEnsure(
		n.ctx,
		"franchises",
		raw.Frachises,
		n.ctx.dimRepo.FranchisesBySourceRefs,
		n.ctx.dimRepo.UpsertFranchises,
	)
	game.Collections = fetchEnsure(
		n.ctx,
		"collections",
		raw.Collections,
		n.ctx.dimRepo.CollectionsBySourceRefs,
		n.ctx.dimRepo.UpsertCollections,
	)
	return nil
}

// platformsEnrichment replicates previous platform logic
type platformsEnrichment struct{ ctx *EnrichmentContext }

func (p *platformsEnrichment) Name() string { return "platforms" }
func (p *platformsEnrichment) Enrich(game *entities.Game,
	raw igdb_models.IGDBGame) error {
	missing := make([]int32, 0)
	for _, id := range raw.Platforms {
		if _, ok := p.ctx.platformCache[int64(id)]; !ok {
			missing = append(missing, id)
		}
	}
	if len(missing) > 0 {
		bytes, err := p.ctx.client.GetEntitiesByIDs(
			"platforms",
			missing,
			"id,name,abbreviation,generation,category,"+
				"created_at,updated_at",
		)
		if err != nil {
			return err
		}
		var plats []igdb_models.IGDBPlatform
		if err := json.Unmarshal(bytes, &plats); err != nil {
			return err
		}
		if err := p.ctx.dimRepo.UpsertPlatforms(plats); err != nil {
			return err
		}
		for _, pl := range plats {
			p.ctx.platformCache[pl.ID] = struct{}{}
		}
	}
	if len(raw.Platforms) > 0 {
		plats, err :=
			p.ctx.dimRepo.PlatformsBySourceRefs(raw.Platforms)
		if err == nil {
			game.Platforms = plats
		} else {
			p.ctx.log.Warn().Err(err).Msg("platform association fetch failed")
		}
	}
	return nil
}

// companiesEnrichment replicates involved companies logic
type companiesEnrichment struct{ ctx *EnrichmentContext }

func (c *companiesEnrichment) Name() string { return "companies" }
func (c *companiesEnrichment) Enrich(game *entities.Game,
	raw igdb_models.IGDBGame) error {
	missingInv := make([]int32, 0)
	for _, id := range raw.InvolvedCompanies {
		if _, ok := c.ctx.involvedCompanyCache[int64(id)]; !ok {
			missingInv = append(missingInv, id)
		}
	}
	companyIDsForResolution := make([]int64, 0)
	if len(missingInv) > 0 {
		bytes, err := c.ctx.client.GetEntitiesByIDs(
			"involved_companies",
			missingInv,
			"id,company,game,developer,publisher,porting,"+
				"supporting,created_at,updated_at",
		)
		if err != nil {
			return err
		}
		var invs []igdb_models.IGDBInvolvedCompany
		if err := json.Unmarshal(bytes, &invs); err != nil {
			return err
		}
		for _, ic := range invs {
			c.ctx.involvedCompanyCache[ic.ID] = ic
			if _, seen := c.ctx.companyCache[ic.Company]; !seen {
				companyIDsForResolution =
					append(companyIDsForResolution, ic.Company)
			}
		}
		if len(companyIDsForResolution) > 0 {
			c32 := make([]int32, 0, len(companyIDsForResolution))
			for _, id := range companyIDsForResolution {
				c32 = append(c32, int32(id))
			}
			b2, err := c.ctx.client.GetEntitiesByIDs(
				"companies",
				c32,
				"id,name,country,description,created_at,updated_at",
			)
			if err != nil {
				return err
			}
			var comps []igdb_models.IGDBCompany
			if err := json.Unmarshal(b2, &comps); err != nil {
				return err
			}
			if err := c.ctx.dimRepo.UpsertCompanies(comps); err !=
				nil {
				return err
			}
			for _, cp := range comps {
				c.ctx.companyCache[cp.ID] = struct{}{}
			}
		}
	}
	companySourceSet := map[int64]struct{}{}
	for _, id := range raw.InvolvedCompanies {
		if ic, ok := c.ctx.involvedCompanyCache[int64(id)]; ok {
			companySourceSet[ic.Company] = struct{}{}
		}
	}
	companyRefs := make([]int64, 0, len(companySourceSet))
	for k := range companySourceSet {
		companyRefs = append(companyRefs, k)
	}
	companyUUIDs, err :=
		c.ctx.dimRepo.CompanyIDsBySourceRefs(companyRefs)
	if err != nil {
		c.ctx.log.Warn().Err(err).Msg("company UUID resolution failed")
	}
	ics := make([]entities.InvolvedCompany, 0)
	for _, id := range raw.InvolvedCompanies {
		icRaw, ok := c.ctx.involvedCompanyCache[int64(id)]
		if !ok || icRaw.Game != game.SourceMeta.SourceRef {
			continue
		}
		compUUID, okc := companyUUIDs[icRaw.Company]
		if !okc {
			continue
		}
		ics = append(ics, entities.InvolvedCompany{
			ID: uuid.New(),
			SourceMeta: entities.SourceMeta{
				SourceRef:       icRaw.ID,
				CreatedAtSource: icRaw.CreatedAt.Time,
				UpdatedAtSource: icRaw.UpdatedAt.Time,
			},
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

type altNamesEnrichment struct{ ctx *EnrichmentContext }

func (a *altNamesEnrichment) Name() string { return "alt_names" }
func (a *altNamesEnrichment) Enrich(
	game *entities.Game,
	raw igdb_models.IGDBGame,
) error {
	missing := make([]int32, 0)
	for _, id := range raw.AlternativeNames {
		if _, ok := a.ctx.altNameCache[int64(id)]; !ok {
			missing = append(missing, id)
		}
	}
	if len(missing) > 0 {
		bytes, err := a.ctx.client.GetEntitiesByIDs(
			"alternative_names",
			missing, "id,game,name,comment",
		)
		if err != nil {
			return err
		}
		var alts []igdb_models.IGDBAlternativeName
		if err := json.Unmarshal(bytes, &alts); err != nil {
			return err
		}
		for _, al := range alts {
			a.ctx.altNameCache[al.ID] = al
		}
	}
	now := time.Now().UTC()
	names := make([]entities.GameAltName, 0)
	for _, id := range raw.AlternativeNames {
		an, ok := a.ctx.altNameCache[int64(id)]
		if !ok || an.Game != game.SourceMeta.SourceRef {
			continue
		}
		created, updated := an.CreatedAt.Time, an.UpdatedAt.Time
		if created.IsZero() {
			created = now
		}
		if updated.IsZero() {
			updated = now
		}
		names = append(names, entities.GameAltName{ID: uuid.New(),
			SourceMeta: entities.SourceMeta{SourceRef: an.ID,
				CreatedAtSource: created, UpdatedAtSource: updated},
			GameID: game.ID, Name: an.Name, Comment: ptrString(an.Comment)})
	}
	game.GameAltNames = names
	return nil
}

type releaseDatesEnrichment struct{ ctx *EnrichmentContext }

func (r *releaseDatesEnrichment) Name() string { return "release_dates" }
func (r *releaseDatesEnrichment) Enrich(game *entities.Game,
	raw igdb_models.IGDBGame) error {
	missing := make([]int32, 0)
	for _, id := range raw.ReleaseDates {
		if _, ok := r.ctx.releaseDateCache[int64(id)]; !ok {
			missing = append(missing, id)
		}
	}
	if len(missing) > 0 {
		bytes, err := r.ctx.client.GetEntitiesByIDs(
			"release_dates",
			missing,
			"id,game,platform,date,region,category,status,created_at,updated_at",
		)
		if err != nil {
			return err
		}
		var rds []igdb_models.IGDBReleaseDate
		if err := json.Unmarshal(bytes, &rds); err != nil {
			return err
		}
		for _, rd := range rds {
			r.ctx.releaseDateCache[rd.ID] = rd
		}
	}
	platformSourceSet := map[int64]struct{}{}
	for _, id := range raw.ReleaseDates {
		if rd, ok := r.ctx.releaseDateCache[int64(id)]; ok {
			platformSourceSet[rd.Platform] = struct{}{}
		}
	}
	ps := make([]int64, 0, len(platformSourceSet))
	for k := range platformSourceSet {
		ps = append(ps, k)
	}
	platformUUIDs, err :=
		r.ctx.dimRepo.PlatformIDsBySourceRefs(ps)
	if err != nil {
		r.ctx.log.Warn().Err(err).Msg(
			"platform id resolution failed for release dates",
		)
	}
	rs := make([]entities.ReleaseDate, 0)
	for _, id := range raw.ReleaseDates {
		rd, ok := r.ctx.releaseDateCache[int64(id)]
		if !ok || rd.Game != game.SourceMeta.SourceRef {
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
		rs = append(rs, entities.ReleaseDate{ID: uuid.New(),
			SourceMeta: entities.SourceMeta{
				SourceRef:       rd.ID,
				CreatedAtSource: rd.CreatedAt.Time,
				UpdatedAtSource: rd.UpdatedAt.Time,
			},
			GameID:     game.ID,
			PlatformID: platUUID,
			Date:       datePtr,
			Region:     regionPtr,
			Category:   catPtr,
			Status:     statusPtr,
		})
	}
	game.ReleaseDates = rs
	return nil
}

type mediaEnrichment struct{ ctx *EnrichmentContext }

func (m *mediaEnrichment) Name() string { return "media" }

func (m *mediaEnrichment) Enrich(
	game *entities.Game,
	raw igdb_models.IGDBGame,
) error {
	coverIDs := []int32{}
	if raw.Cover != nil {
		coverIDs = append(coverIDs, *raw.Cover)
	}
	if err := m.fetchMediaType("covers", coverIDs); err != nil {
		return err
	}
	if err := m.fetchMediaType("screenshots", raw.Screenshots); err != nil {
		return err
	}
	if err := m.fetchMediaType("artworks", raw.Artworks); err != nil {
		return err
	}
	assets := make([]entities.MediaAsset, 0)
	seen := make(map[int64]struct{})
	now := time.Now().UTC()
	add := func(kind string, ids []int32) {
		for _, id := range ids {
			if ma, ok := m.ctx.mediaCache[kind][int64(id)]; ok &&
				ma.Game == game.SourceMeta.SourceRef {
				if _, dup := seen[ma.ID]; dup {
					continue
				}
				seen[ma.ID] = struct{}{}
				url := normalizeIGDBImageURL(ma.URL)
				assets = append(
					assets,
					entities.MediaAsset{
						ID: uuid.New(),
						SourceMeta: entities.SourceMeta{
							SourceRef:       ma.ID,
							CreatedAtSource: now,
							UpdatedAtSource: now,
						},
						GameID: &game.ID,
						Type: strings.ToUpper(
							strings.TrimSuffix(kind, "s"),
						),
						Width:    int32Ptr(ma.Width),
						Height:   int32Ptr(ma.Height),
						URL:      url,
						Checksum: ptrString(ma.Checksum)},
				)
			}
		}
	}
	add("covers", coverIDs)
	add("screenshots", raw.Screenshots)
	add("artworks", raw.Artworks)
	game.MediaAssets = assets
	return nil
}
func (m *mediaEnrichment) fetchMediaType(
	kind string,
	ids []int32,
) error {
	missing := make([]int32, 0)
	cache := m.ctx.mediaCache[kind]
	for _, id := range ids {
		if _, ok := cache[int64(id)]; !ok {
			missing = append(missing, id)
		}
	}
	if len(missing) == 0 {
		return nil
	}
	bytes, err := m.ctx.client.GetEntitiesByIDs(
		kind,
		missing,
		"id,game,width,height,url,checksum",
	)
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

type multiplayerModesEnrichment struct {
	ctx *EnrichmentContext
}

func (m *multiplayerModesEnrichment) Name() string { return "multiplayer_modes" }
func (m *multiplayerModesEnrichment) Enrich(
	game *entities.Game,
	raw igdb_models.IGDBGame,
) error {
	missing := make([]int32, 0)
	for _, id := range raw.MultiplayerModes {
		if _, ok := m.ctx.multiModeCache[int64(id)]; !ok {
			missing = append(missing, id)
		}
	}
	if len(missing) > 0 {
		bytes, err := m.ctx.client.GetEntitiesByIDs(
			"multiplayer_modes",
			missing,
			"id,game,campaigncoop,dropin,lancoop,offlinecoop,offlinecoopmax"+
				",offlinemax,onlinecoop,onlinecoopmax,onlinemax,splitscreen,"+
				"splitscreenonline",
		)
		if err != nil {
			return err
		}
		var modes []igdb_models.IGDBMultiplayerMode
		if err := json.Unmarshal(bytes, &modes); err != nil {
			return err
		}
		for _, md := range modes {
			m.ctx.multiModeCache[md.ID] = md
		}
	}
	now := time.Now().UTC()
	mm := make([]entities.MultiplayerMode, 0)
	for _, id := range raw.MultiplayerModes {
		if mde, ok := m.ctx.multiModeCache[int64(id)]; ok &&
			mde.Game == game.SourceMeta.SourceRef {
			created, updated := mde.CreatedAt.Time,
				mde.UpdatedAt.Time
			if created.IsZero() {
				created = now
			}
			if updated.IsZero() {
				updated = now
			}
			mm = append(mm, entities.MultiplayerMode{ID: uuid.New(),
				SourceMeta: entities.SourceMeta{SourceRef: mde.ID,
					CreatedAtSource: created, UpdatedAtSource: updated},
				GameID: game.ID, CampaignCoop: boolPtr(mde.CampaignCoop),
				DropIn:            boolPtr(mde.DropIn),
				LANCoop:           boolPtr(mde.LanCoop),
				OfflineCoop:       boolPtr(mde.OfflineCoop),
				OfflineCoopMax:    int16Ptr(mde.OfflineCoopMax),
				OfflineMax:        int16Ptr(mde.OfflineMax),
				OnlineCoop:        boolPtr(mde.OnlineCoop),
				OnlineCoopMax:     int16Ptr(mde.OnlineCoopMax),
				OnlineMax:         int16Ptr(mde.OnlineMax),
				Splitscreen:       boolPtr(mde.Splitscreen),
				SplitscreenOnline: boolPtr(mde.SplitscreenOnline),
			})
		}
	}
	game.MultiplayerModes = mm
	return nil
}

type languageSupportsEnrichment struct {
	ctx *EnrichmentContext
}

func (l *languageSupportsEnrichment) Name() string { return "language_supports" }
func (l *languageSupportsEnrichment) Enrich(
	game *entities.Game,
	raw igdb_models.IGDBGame,
) error {
	missing := make([]int32, 0)
	for _, id := range raw.LanguageSupports {
		if _, ok := l.ctx.languageSupportCache[int64(id)]; !ok {
			missing = append(missing, id)
		}
	}
	if len(missing) > 0 {
		bytes, err := l.ctx.client.GetEntitiesByIDs(
			"language_supports",
			missing,
			"id,game,language,language_support_type,created_at,updated_at",
		)
		if err != nil {
			return err
		}
		var ls []igdb_models.IGDBLanguageSupport
		if err := json.Unmarshal(bytes, &ls); err != nil {
			return err
		}
		for _, it := range ls {
			l.ctx.languageSupportCache[it.ID] = it
		}
	}
	supports := make([]entities.GameLanguageSupport, 0)
	for _, id := range raw.LanguageSupports {
		lraw, ok := l.ctx.languageSupportCache[int64(id)]
		if ok && lraw.Game == game.SourceMeta.SourceRef {
			supports = append(supports,
				entities.GameLanguageSupport{
					ID: uuid.New(),
					SourceMeta: entities.SourceMeta{
						SourceRef:       lraw.ID,
						CreatedAtSource: lraw.CreatedAt.Time,
						UpdatedAtSource: lraw.UpdatedAt.Time,
					},
					GameID:          game.ID,
					LanguageCode:    int(lraw.Language),
					SupportTypeCode: int(lraw.Type),
				},
			)
		}
	}
	game.LanguageSupports = supports
	return nil
}

type ageRatingsEnrichment struct{ ctx *EnrichmentContext }

func (a *ageRatingsEnrichment) Name() string { return "age_ratings" }
func (a *ageRatingsEnrichment) Enrich(game *entities.Game,
	raw igdb_models.IGDBGame) error {
	if len(raw.AgeRatings) == 0 {
		return nil
	}
	existing, err :=
		a.ctx.dimRepo.AgeRatingsBySourceRefs(raw.AgeRatings)
	if err != nil {
		a.ctx.log.Warn().Err(err).Msg("fetch existing age ratings failed")
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
	var fetched []igdb_models.IGDBAgeRating
	if len(missing) > 0 {
		fieldsPrimary := "id,organization,rating_category,synopsis,rating_content_descriptions,created_at,updated_at"
		fieldsFallback := "id,organization,rating_category,synopsis,rating_content_descriptions"
		bytes, err := a.ctx.client.GetEntitiesByIDs(
			"age_ratings",
			missing,
			fieldsPrimary,
		)
		if err != nil && strings.Contains(err.Error(), "Invalid field name") {
			bytes, err = a.ctx.client.GetEntitiesByIDs(
				"age_ratings",
				missing,
				fieldsFallback,
			)
		}
		if err != nil {
			return err
		}
		if err := json.Unmarshal(bytes, &fetched); err != nil {
			return err
		}
		if err := a.ctx.dimRepo.UpsertAgeRatings(fetched); err != nil {
			return err
		}
	}
	// refresh existing after inserts
	existing, err = a.ctx.dimRepo.AgeRatingsBySourceRefs(raw.AgeRatings)
	if err != nil {
		return err
	}
	// Gather organization IDs & content description IDs
	orgSet := map[int64]struct{}{}
	cdSet := map[int64]struct{}{}
	var combined = fetched
	if len(combined) == 0 { // no new fetch; need minimal struct list for linking
		// only need to build cd set from raw cache if already cached
		for _, id := range raw.AgeRatings {
			if ar, ok := a.ctx.ageRatingCache[int64(id)]; ok {
				combined = append(combined, ar)
			}
		}
	}
	for _, ar := range combined {
		if ar.Organization > 0 {
			orgSet[ar.Organization] = struct{}{}
		}
		for _, cd := range ar.ContentDescriptions {
			cdSet[cd] = struct{}{}
		}
		// cache
		a.ctx.ageRatingCache[ar.ID] = ar
	}
	// Ensure organizations are upserted
	if len(orgSet) > 0 {
		orgIDs32 := make([]int32, 0, len(orgSet))
		for k := range orgSet {
			orgIDs32 = append(orgIDs32, int32(k))
		}
		existOrg, _ := a.ctx.dimRepo.AgeRatingOrganizationsBySourceRefs(orgIDs32)
		presentOrg := map[int64]struct{}{}
		for _, eo := range existOrg {
			presentOrg[eo.SourceRef] = struct{}{}
		}
		missingOrg := make([]int32, 0)
		for k := range orgSet {
			if _, ok := presentOrg[k]; !ok {
				missingOrg = append(missingOrg, int32(k))
			}
		}
		if len(missingOrg) > 0 {
			bytes, err := a.ctx.client.GetEntitiesByIDs(
				"age_rating_organizations",
				missingOrg,
				"id,name,checksum,created_at,updated_at",
			)
			if err != nil && strings.Contains(
				err.Error(),
				"Invalid field",
			) {
				bytes,
					err = a.ctx.client.GetEntitiesByIDs(
					"age_rating_organizations",
					missingOrg,
					"id,name,checksum",
				)
			}
			if err == nil {
				var fetchedOrg []igdb_models.IGDBAgeRatingOrganization
				if err := json.Unmarshal(bytes, &fetchedOrg); err == nil {
					_ = a.ctx.dimRepo.UpsertAgeRatingOrganizations(fetchedOrg)
				}
			}
		}
	}
	// Determine which content descriptions are missing in DB
	if len(cdSet) > 0 {
		cdRefs := make([]int32, 0, len(cdSet))
		for k := range cdSet {
			cdRefs = append(cdRefs, int32(k))
		}
		existCD, _ := a.ctx.dimRepo.AgeRatingContentDescriptionsBySourceRefs(cdRefs)
		presentCD := map[int64]struct{}{}
		for _, e := range existCD {
			presentCD[e.SourceRef] = struct{}{}
		}
		missingCD := make([]int32, 0)
		for k := range cdSet {
			if _, ok := presentCD[k]; !ok {
				missingCD = append(missingCD, int32(k))
			}
		}
		var fetchedCD []igdb_models.IGDBAgeRatingContentDescriptionV2
		if len(missingCD) > 0 {
			fieldsPrimary := "id,organization,description,description_type,checksum,created_at,updated_at"
			fieldsFallback := "id,organization,description,description_type,checksum"
			bytes, err := a.ctx.client.GetEntitiesByIDs("age_rating_content_descriptions_v2", missingCD, fieldsPrimary)
			if err != nil && strings.Contains(err.Error(), "Invalid field") {
				bytes, err = a.ctx.client.GetEntitiesByIDs("age_rating_content_descriptions_v2", missingCD, fieldsFallback)
			}
			if err != nil {
				return err
			}
			if err := json.Unmarshal(bytes, &fetchedCD); err != nil {
				return err
			}
			for _, cd := range fetchedCD {
				a.ctx.ageRatingContentDescCache[cd.ID] = cd
				if cd.Organization > 0 {
					orgSet[cd.Organization] = struct{}{}
				}
			}
			// Upsert organizations first (may include new ones from content descriptions)
			if len(orgSet) > 0 {
				orgMissing := make([]int32, 0)
				orgIDs := make([]int32, 0, len(orgSet))
				for k := range orgSet {
					orgIDs = append(orgIDs, int32(k))
				}
				existOrg, _ := a.ctx.dimRepo.AgeRatingOrganizationsBySourceRefs(orgIDs)
				presentOrg := map[int64]struct{}{}
				for _, eo := range existOrg {
					presentOrg[eo.SourceRef] = struct{}{}
				}
				for k := range orgSet {
					if _, ok := presentOrg[k]; !ok {
						orgMissing = append(orgMissing, int32(k))
					}
				}
				if len(orgMissing) > 0 {
					bytes, err := a.ctx.client.GetEntitiesByIDs("age_rating_organizations", orgMissing, "id,name,checksum,created_at,updated_at")
					if err != nil && strings.Contains(err.Error(), "Invalid field") {
						bytes, err = a.ctx.client.GetEntitiesByIDs("age_rating_organizations", orgMissing, "id,name,checksum")
					}
					if err != nil {
						return err
					}
					var fetchedOrg []igdb_models.IGDBAgeRatingOrganization
					if err := json.Unmarshal(bytes, &fetchedOrg); err != nil {
						return err
					}
					if err := a.ctx.dimRepo.UpsertAgeRatingOrganizations(fetchedOrg); err != nil {
						return err
					}
				}
			}
			if len(fetchedCD) > 0 {
				if err := a.ctx.dimRepo.UpsertAgeRatingContentDescriptions(fetchedCD); err != nil {
					return err
				}
			}
		}
		// Link all content descriptions to age ratings
		if err := a.ctx.dimRepo.UpsertAgeRatingContentDescriptionLinks(combined); err != nil {
			a.ctx.log.Warn().Err(err).Msg("link age rating content descriptions failed")
		}
	}
	game.AgeRatings = existing
	return nil
}

type achievementsEnrichment struct{ ctx *EnrichmentContext }

func (a *achievementsEnrichment) Name() string { return "achievements" }
func (a *achievementsEnrichment) Enrich(game *entities.Game,
	raw igdb_models.IGDBGame) error {
	if len(raw.Achievements) == 0 {
		return nil
	}
	missing := make([]int32, 0)
	for _, id := range raw.Achievements {
		if _, ok := a.ctx.achievementCache[int64(id)]; !ok {
			missing = append(missing, id)
		}
	}
	if len(missing) > 0 {
		bytes, err := a.ctx.client.GetEntitiesByIDs("achievements", missing, "id,game,name,slug,description,category,order,points,hidden,unlocked_icon,locked_icon,checksum,created_at,updated_at")
		if err != nil && strings.Contains(err.Error(), "Invalid field") {
			bytes, err = a.ctx.client.GetEntitiesByIDs("achievements", missing, "id,game,name,slug,description,category,order,points,hidden,unlocked_icon,locked_icon,checksum")
		}
		if err != nil {
			return err
		}
		var items []igdb_models.IGDBAchievement
		if err := json.Unmarshal(bytes, &items); err != nil {
			return err
		}
		for _, it := range items {
			a.ctx.achievementCache[it.ID] = it
		}
	}
	ach := make([]entities.GameAchievement, 0,
		len(raw.Achievements))
	for _, id := range raw.Achievements {
		it, ok := a.ctx.achievementCache[int64(id)]
		if !ok || it.Game != game.SourceMeta.SourceRef {
			continue
		}
		var cat *entities.AchievementCategory
		if it.Category != 0 {
			c := entities.AchievementCategory(it.Category)
			cat = &c
		}
		ach = append(ach, entities.GameAchievement{ID: uuid.New(),
			SourceMeta: entities.SourceMeta{SourceRef: it.ID,
				CreatedAtSource: it.CreatedAt.Time, UpdatedAtSource: it.UpdatedAt.Time}, GameID: game.ID, Name: it.Name, Slug: it.Slug, Description: ptrString(it.Description), Category: cat, OrderIndex: int32Ptr(it.Order), Points: int32Ptr(it.Points), Secret: it.Hidden, Checksum: ptrString(it.Checksum)})
	}
	game.Achievements = ach
	return nil
}

type websitesEnrichment struct{ ctx *EnrichmentContext }

func (w *websitesEnrichment) Name() string { return "websites" }
func (w *websitesEnrichment) Enrich(
	game *entities.Game,
	raw igdb_models.IGDBGame,
) error {
	if len(raw.Websites) == 0 {
		return nil
	}
	missing := make([]int32, 0)
	for _, id := range raw.Websites {
		if _, ok := w.ctx.websiteCache[int64(id)]; !ok {
			missing = append(missing, id)
		}
	}
	if len(missing) > 0 {
		bytes, err := w.ctx.client.GetEntitiesByIDs("websites", missing, "id,game,category,url,trusted,checksum,created_at,updated_at")
		if err != nil && strings.Contains(err.Error(), "Invalid field") {
			bytes, err = w.ctx.client.GetEntitiesByIDs("websites", missing, "id,game,category,url,trusted,checksum")
		}
		if err != nil {
			return err
		}
		var items []igdb_models.IGDBWebsite
		if err := json.Unmarshal(bytes, &items); err != nil {
			return err
		}
		for _, it := range items {
			w.ctx.websiteCache[it.ID] = it
		}
	}
	ws := make([]entities.GameWebsite, 0, len(raw.Websites))
	for _, id := range raw.Websites {
		it, ok := w.ctx.websiteCache[int64(id)]
		if !ok || it.Game != game.SourceMeta.SourceRef {
			continue
		}
		trusted := boolPtr(it.Trusted)
		cat := entities.WebsiteCategory(it.Category)
		ws = append(ws, entities.GameWebsite{ID: uuid.New(),
			SourceMeta: entities.SourceMeta{SourceRef: it.ID,
				CreatedAtSource: it.CreatedAt.Time, UpdatedAtSource: it.UpdatedAt.Time}, GameID: game.ID, Category: cat, URL: it.URL, Trusted: trusted, Checksum: ptrString(it.Checksum)})
	}
	game.Websites = ws
	return nil
}

type videosEnrichment struct{ ctx *EnrichmentContext }

func (v *videosEnrichment) Name() string { return "videos" }
func (v *videosEnrichment) Enrich(
	game *entities.Game,
	raw igdb_models.IGDBGame,
) error {
	if len(raw.Videos) == 0 {
		return nil
	}
	missing := make([]int32, 0)
	for _, id := range raw.Videos {
		if _, ok := v.ctx.videoCache[int64(id)]; !ok {
			missing = append(missing, id)
		}
	}
	if len(missing) > 0 {
		bytes, err := v.ctx.client.GetEntitiesByIDs("game_videos", missing, "id,game,name,video_id,checksum,created_at,updated_at")
		if err != nil && strings.Contains(err.Error(), "Invalid field") {
			bytes, err = v.ctx.client.GetEntitiesByIDs("game_videos", missing, "id,game,name,video_id,checksum")
		}
		if err != nil {
			return err
		}
		var items []igdb_models.IGDBGameVideo
		if err := json.Unmarshal(bytes, &items); err != nil {
			return err
		}
		for _, it := range items {
			v.ctx.videoCache[it.ID] = it
		}
	}
	vids := make([]entities.GameVideo, 0, len(raw.Videos))
	for _, id := range raw.Videos {
		it, ok := v.ctx.videoCache[int64(id)]
		if !ok || it.Game != game.SourceMeta.SourceRef {
			continue
		}
		vids = append(vids, entities.GameVideo{ID: uuid.New(),
			SourceMeta: entities.SourceMeta{SourceRef: it.ID,
				CreatedAtSource: it.CreatedAt.Time, UpdatedAtSource: it.UpdatedAt.Time}, GameID: game.ID, Name: it.Name,
			VideoID: it.VideoID, Checksum: ptrString(it.Checksum)})
	}
	game.Videos = vids
	return nil
}
