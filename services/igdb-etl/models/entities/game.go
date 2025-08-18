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
	Slug                  string         `json:"slug" gorm:"column:slug;uniqueIndex"`
	Title                 string         `json:"title" gorm:"column:title"`
	Summary               *string        `json:"summary" gorm:"column:summary"`
	Storyline             *string        `json:"storyline" gorm:"column:storyline"`
	Category              *GameCategory  `json:"category" gorm:"column:category_code"`
	Status                *ReleaseStatus `json:"status" gorm:"column:status_code"`
	FirstReleaseDate      *time.Time     `json:"first_release_date" gorm:"column:first_release_date"`
	TotalRating           *float64       `json:"total_rating" gorm:"column:total_rating"`
	TotalRatingCount      *int32         `json:"total_rating_count" gorm:"column:total_rating_count"`
	AggregatedRating      *float64       `json:"aggregated_rating" gorm:"column:aggregated_rating"`
	AggregatedRatingCount *int32         `json:"aggregated_rating_count" gorm:"column:aggregated_rating_count"`
	Popularity            *float64       `json:"popularity" gorm:"column:popularity"`
	CollectionID          *uuid.UUID     `json:"collection_id" gorm:"column:collection_id"`
	ParentGameID          *uuid.UUID     `json:"parent_game_id" gorm:"column:parent_game_id"`
	IngestionTimestamp    time.Time      `json:"ingestion_timestamp" gorm:"column:ingestion_timestamp;autoCreateTime:false"`
	Active                bool           `json:"active" gorm:"column:active"`
	// Steam enrichment fields
	SteamAppID            *int32     `json:"steam_app_id" gorm:"column:steam_app_id"`
	SteamSupportsAchieves *bool      `json:"steam_supports_achievements" gorm:"column:steam_supports_achievements"`
	SteamHasStats         *bool      `json:"steam_has_stats" gorm:"column:steam_has_stats"`
	SteamLastEnrichedAt   *time.Time `json:"steam_last_enriched_at" gorm:"column:steam_last_enriched_at"`
	PopularityComposite   *float64   `json:"popularity_composite" gorm:"column:popularity_composite"`
	UpdatedAt             time.Time  `json:"updated_at" gorm:"column:updated_at;autoUpdateTime"`
	// Relationship slices (ignored by GORM; managed via explicit joins / link tables)
	KeywordIDs     []uuid.UUID `json:"keywords" gorm:"-"`
	GenreIDs       []uuid.UUID `json:"genres" gorm:"-"`
	ThemeIDs       []uuid.UUID `json:"themes" gorm:"-"`
	GameModeIDs    []uuid.UUID `json:"game_modes" gorm:"-"`
	PerspectiveIDs []uuid.UUID `json:"player_perspectives" gorm:"-"`
	FranchiseIDs   []uuid.UUID `json:"franchise_ids" gorm:"-"`
	DLCIDs         []uuid.UUID `json:"dlc_ids" gorm:"-"`
	ExpansionIDs   []uuid.UUID `json:"expansion_ids" gorm:"-"`
	RemakeIDs      []uuid.UUID `json:"remake_ids" gorm:"-"`
	RemasterIDs    []uuid.UUID `json:"remaster_ids" gorm:"-"`
	PortIDs        []uuid.UUID `json:"port_ids" gorm:"-"`
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
func WithCategory(c GameCategory) Option {
	return func(g *Game) error { g.Category = &c; return nil }
}

// WithStatus sets the release status.
func WithStatus(s ReleaseStatus) Option {
	return func(g *Game) error { g.Status = &s; return nil }
}

// To String Methods:

// String implements fmt.Stringer.
func (g Game) String() string {
	return g.Title
}
