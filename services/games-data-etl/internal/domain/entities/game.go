package entities

import (
	"errors"
	"github.com/google/uuid"
	"strings"
	"time"
)

// Game is the central catalog entity representing a playable title.
// Relationship slice fields use separate link tables; they are ignored by GORM mapping with gorm:"-".
type Game struct {
	ID uuid.UUID `json:"game_id" gorm:"column:game_id;type:uuid;primaryKey;default:gen_random_uuid()"`
	SourceMeta
	Slug                  string     `json:"slug" gorm:"column:slug;uniqueIndex"`
	Title                 string     `json:"title" gorm:"column:title"`
	Summary               *string    `json:"summary" gorm:"column:summary"`
	Storyline             *string    `json:"storyline" gorm:"column:storyline"`
	FirstReleaseDate      *time.Time `json:"first_release_date" gorm:"column:first_release_date"`
	TotalRating           *float64   `json:"total_rating" gorm:"column:total_rating"`
	TotalRatingCount      *int32     `json:"total_rating_count" gorm:"column:total_rating_count"`
	AggregatedRating      *float64   `json:"aggregated_rating" gorm:"column:aggregated_rating"`
	AggregatedRatingCount *int32     `json:"aggregated_rating_count" gorm:"column:aggregated_rating_count"`
	Popularity            *float64   `json:"popularity" gorm:"column:popularity"`
	IngestionTimestamp    time.Time  `json:"ingestion_timestamp" gorm:"column:ingestion_timestamp;autoCreateTime:false"`
	Active                bool       `json:"active" gorm:"column:active;default:true"`

	// Foreign Key References
	CollectionID *uuid.UUID `json:"collection_id" gorm:"column:collection_id;type:uuid"`
	ParentGameID *uuid.UUID `json:"parent_game_id" gorm:"column:parent_game_id;type:uuid"`
	CategoryCode *int16     `json:"category_code" gorm:"column:category_code"`
	StatusCode   *int16     `json:"status_code" gorm:"column:status_code"`

	// Associations
	Collection *Collection    `json:"collection" gorm:"foreignKey:CollectionID"`
	ParentGame *Game          `json:"parent_game" gorm:"foreignKey:ParentGameID"`
	Category   *GameCategory  `json:"category" gorm:"-"`
	Status     *ReleaseStatus `json:"status" gorm:"-"`

	// Many-to-Many Relationships (handled via junction tables)`
	Keywords     []Keyword           `json:"keywords" gorm:"many2many:game_keyword"`
	Genres       []Genre             `json:"genres" gorm:"many2many:game_genre"`
	Themes       []Theme             `json:"themes" gorm:"many2many:game_theme"`
	GameModes    []GameMode          `json:"game_modes" gorm:"many2many:game_mode_link"`
	Perspectives []PlayerPerspective `json:"player_perspectives" gorm:"many2many:game_player_perspective"`
	Franchises   []Franchise         `json:"franchises" gorm:"many2many:game_franchise"`
	Platforms    []Platform          `json:"platforms" gorm:"-"` // join table not yet implemented

	// One-to-Many Relationships
	GameAltNames     []GameAltName         `json:"game_alternative_names" gorm:"foreignKey:GameID"`
	Achievements     []GameAchievement     `json:"achievements" gorm:"foreignKey:GameID"`
	Companies        []InvolvedCompany     `json:"involved_companies" gorm:"foreignKey:GameID"`
	ReleaseDates     []ReleaseDate         `json:"release_dates" gorm:"foreignKey:GameID"`
	AgeRatings       []AgeRating           `json:"age_ratings" gorm:"foreignKey:GameID"`
	MediaAssets      []MediaAsset          `json:"media_assets" gorm:"foreignKey:GameID"`
	Covers           []MediaAsset          `json:"covers" gorm:"foreignKey:GameID;where:type = 'COVER'"`
	Screenshots      []MediaAsset          `json:"screenshots" gorm:"foreignKey:GameID;where:type = 'SCREENSHOT'"`
	Artworks         []MediaAsset          `json:"artworks" gorm:"foreignKey:GameID;where:type = 'ARTWORK'"`
	Websites         []GameWebsite         `json:"websites" gorm:"foreignKey:GameID"`
	Videos           []GameVideo           `json:"videos" gorm:"foreignKey:GameID"`
	LanguageSupports []GameLanguageSupport `json:"language_supports" gorm:"foreignKey:GameID"`
	MultiplayerModes []MultiplayerMode     `json:"multiplayer_modes" gorm:"foreignKey:GameID"`
	ExternalRefs     []ExternalReference   `json:"external_references" gorm:"foreignKey:GameID"`

	// Derived relationships (computed from category and parent_game)
	DLCs       []Game `json:"dlcs" gorm:"foreignKey:ParentGameID;where:category_code = 1"`
	Expansions []Game `json:"expansions" gorm:"foreignKey:ParentGameID;where:category_code IN (2,4,10)"`
	Remasters  []Game `json:"remasters" gorm:"foreignKey:ParentGameID;where:category_code = 9"`
	Remakes    []Game `json:"remakes" gorm:"foreignKey:ParentGameID;where:category_code = 8"`
	Ports      []Game `json:"ports" gorm:"foreignKey:ParentGameID;where:category_code = 11"`

	// Steam enrichment fields
	SteamAppID            *int32     `json:"steam_app_id" gorm:"column:steam_app_id"`
	SteamSupportsAchieves *bool      `json:"steam_supports_achievements" gorm:"column:steam_supports_achievements"`
	SteamHasStats         *bool      `json:"steam_has_stats" gorm:"column:steam_has_stats"`
	SteamLastEnrichedAt   *time.Time `json:"steam_last_enriched_at" gorm:"column:steam_last_enriched_at"`
	PopularityComposite   *float64   `json:"popularity_composite" gorm:"column:popularity_composite"`
	UpdatedAt             time.Time  `json:"updated_at" gorm:"column:updated_at;autoUpdateTime"`
}

// TableName overrides pluralization.
func (Game) TableName() string { return "game" }

// Option a functional option for constructing a Game.
type Option func(*Game) error

// normalizeSlug prepares a slug for storage and comparison.
// Currently, this just lower-cases and trims whitespace.
func normalizeSlug(s string) string {
	return strings.ToLower(strings.TrimSpace(s))
}

// strPtrOrNil returns a pointer to the string or nil if the string is empty or whitespace.
func strPtrOrNil(s string) *string {
	if strings.TrimSpace(s) == "" {
		return nil
	}
	v := s
	return &v
}

// New constructs a Game enforcing core invariants and applying supplied options.
// ID, SourceRef, Slug, Title, and IngestionTimestamp are required.
// The game is marked Active by default.
func New(id uuid.UUID, sourceRef int64, slug, title string, ingestion time.Time, opts ...Option) (*Game, error) {
	now := time.Now().UTC()
	g := &Game{
		ID:                 id,
		SourceMeta:         SourceMeta{SourceRef: sourceRef, CreatedAtSource: now, UpdatedAtSource: now},
		Slug:               normalizeSlug(slug),
		Title:              strings.TrimSpace(title),
		IngestionTimestamp: ingestion,
		Active:             true,
	}
	var problems []string
	if g.Slug == "" {
		problems = append(problems, "slug required")
	}
	if g.Title == "" {
		problems = append(problems, "title required")
	}
	for _, opt := range opts {
		if err := opt(g); err != nil {
			problems = append(problems, err.Error())
		}
	}
	if len(problems) > 0 {
		return nil, errors.New(strings.Join(problems, "; "))
	}
	return g, nil
}

// With Methods for functional options:

// WithSummary sets the summary.
func WithSummary(s string) Option {
	return func(g *Game) error { g.Summary = strPtrOrNil(s); return nil }
}

// WithStoryline sets the storyline.
func WithStoryline(s string) Option {
	return func(g *Game) error { g.Storyline = strPtrOrNil(s); return nil }
}

// WithCategory sets the category.
func WithCategory(code int16) Option {
	return func(g *Game) error { g.CategoryCode = &code; return nil }
}

// WithStatus sets the release status.
func WithStatus(code int16) Option {
	return func(g *Game) error { g.StatusCode = &code; return nil }
}

// WithCollection sets the collection ID.
func WithCollection(collectionID uuid.UUID) Option {
	return func(g *Game) error { g.CollectionID = &collectionID; return nil }
}

// WithParentGame sets the parent game ID.
func WithParentGame(parentGameID uuid.UUID) Option {
	return func(g *Game) error { g.ParentGameID = &parentGameID; return nil }
}

// WithFirstReleaseDate sets the first release date.
func WithFirstReleaseDate(date time.Time) Option {
	return func(g *Game) error { g.FirstReleaseDate = &date; return nil }
}

// WithRatings sets the rating information.
func WithRatings(totalRating *float64, totalCount *int32, aggRating *float64, aggCount *int32) Option {
	return func(g *Game) error {
		g.TotalRating = totalRating
		g.TotalRatingCount = totalCount
		g.AggregatedRating = aggRating
		g.AggregatedRatingCount = aggCount
		return nil
	}
}

// WithPopularity sets the popularity score.
func WithPopularity(popularity float64) Option {
	return func(g *Game) error { g.Popularity = &popularity; return nil }
}

// String implements fmt.Stringer.
func (g Game) String() string {
	return g.Title
}
