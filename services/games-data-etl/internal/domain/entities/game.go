package entities

import (
	"github.com/google/uuid"
	"time"
)

// Game is the central catalog entity representing a
// playable title.
// Relationship slice fields use separate link tables; they
// are ignored by GORM mapping with gorm:"-".
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

	// Foreign Key References (simple scalar FKs only)
	ParentGameID *uuid.UUID `json:"parent_game_id" gorm:"column:parent_game_id;type:uuid"`
	CategoryCode *int16     `json:"category_code" gorm:"column:category_code"`
	StatusCode   *int16     `json:"status_code" gorm:"column:status_code"`

	// Associations (non-scalar)
	ParentGame *Game          `json:"parent_game" gorm:"foreignKey:ParentGameID"`
	Category   *GameCategory  `json:"category" gorm:"-"`
	Status     *ReleaseStatus `json:"status" gorm:"-"`

	// Many-to-Many Relationships
	Keywords     []Keyword           `json:"keywords" gorm:"many2many:game_keyword"`
	Genres       []Genre             `json:"genres" gorm:"many2many:game_genre"`
	Themes       []Theme             `json:"themes" gorm:"many2many:game_theme"`
	GameModes    []GameMode          `json:"game_modes" gorm:"many2many:game_mode_link"`
	Perspectives []PlayerPerspective `json:"player_perspectives" gorm:"many2many:game_player_perspective"`
	Franchises   []Franchise         `json:"franchises" gorm:"many2many:game_franchise"`
	AgeRatings   []AgeRating         `json:"age_ratings" gorm:"many2many:game_age_rating"`
	Collections  []Collection        `json:"collections" gorm:"many2many:game_collection"`
	Platforms    []Platform          `json:"platforms" gorm:"many2many:game_platform"`

	// One-to-Many Relationships
	GameAltNames     []GameAltName         `json:"game_alternative_names" gorm:"foreignKey:GameID"`
	Achievements     []GameAchievement     `json:"achievements" gorm:"foreignKey:GameID"`
	Companies        []InvolvedCompany     `json:"involved_companies" gorm:"foreignKey:GameID"`
	ReleaseDates     []ReleaseDate         `json:"release_dates" gorm:"foreignKey:GameID"`
	MediaAssets      []MediaAsset          `json:"media_assets" gorm:"foreignKey:GameID"`
	Covers           []MediaAsset          `json:"covers" gorm:"foreignKey:GameID;where:type = 'COVER'"`
	Screenshots      []MediaAsset          `json:"screenshots" gorm:"foreignKey:GameID;where:type = 'SCREENSHOT'"`
	Artworks         []MediaAsset          `json:"artworks" gorm:"foreignKey:GameID;where:type = 'ARTWORK'"`
	Websites         []GameWebsite         `json:"websites" gorm:"foreignKey:GameID"`
	Videos           []GameVideo           `json:"videos" gorm:"foreignKey:GameID"`
	LanguageSupports []GameLanguageSupport `json:"language_supports" gorm:"foreignKey:GameID"`
	MultiplayerModes []MultiplayerMode     `json:"multiplayer_modes" gorm:"foreignKey:GameID"`
	ExternalRefs     []ExternalReference   `json:"external_references" gorm:"foreignKey:GameID"`

	// Derived relationships (computed from category and
	// parent_game)
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

// String implements fmt.Stringer.
func (g Game) String() string {
	return g.Title
}
