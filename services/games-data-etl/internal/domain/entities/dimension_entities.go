package entities

import (
	"github.com/google/uuid"
	"time"
)

// Shared pattern dimensions: Genre, Theme, GameMode,
// PlayerPerspective, Keyword, Collection, Franchise
// Each has: id, source_ref, name, slug, created_at_source,
// updated_at_source, created_at, updated_at

// Genre maps to genre table
type Genre struct {
	ID uuid.UUID `gorm:"column:genre_id;type:uuid;primaryKey;default:gen_random_uuid()"`
	SourceMeta
	Name      string    `gorm:"column:name"`
	Slug      string    `gorm:"column:slug"`
	CreatedAt time.Time `gorm:"column:created_at;autoCreateTime"`
	UpdatedAt time.Time `gorm:"column:updated_at;autoUpdateTime"`
}

func (Genre) TableName() string { return "genre" }

// Theme maps to theme table
type Theme struct {
	ID uuid.UUID `gorm:"column:theme_id;type:uuid;primaryKey;default:gen_random_uuid()"`
	SourceMeta
	Name      string    `gorm:"column:name"`
	Slug      string    `gorm:"column:slug"`
	CreatedAt time.Time `gorm:"column:created_at;autoCreateTime"`
	UpdatedAt time.Time `gorm:"column:updated_at;autoUpdateTime"`
}

func (Theme) TableName() string { return "theme" }

// GameMode maps to game_mode table
type GameMode struct {
	ID uuid.UUID `gorm:"column:game_mode_id;type:uuid;primaryKey;default:gen_random_uuid()"`
	SourceMeta
	Name      string    `gorm:"column:name"`
	Slug      string    `gorm:"column:slug"`
	CreatedAt time.Time `gorm:"column:created_at;autoCreateTime"`
	UpdatedAt time.Time `gorm:"column:updated_at;autoUpdateTime"`
}

func (GameMode) TableName() string { return "game_mode" }

// PlayerPerspective maps to player_perspective table
type PlayerPerspective struct {
	ID uuid.UUID `gorm:"column:player_perspective_id;type:uuid;primaryKey;default:gen_random_uuid()"`
	SourceMeta
	Name      string    `gorm:"column:name"`
	Slug      string    `gorm:"column:slug"`
	CreatedAt time.Time `gorm:"column:created_at;autoCreateTime"`
	UpdatedAt time.Time `gorm:"column:updated_at;autoUpdateTime"`
}

func (PlayerPerspective) TableName() string { return "player_perspective" }

// Keyword maps to keyword table
type Keyword struct {
	ID uuid.UUID `gorm:"column:keyword_id;type:uuid;primaryKey;default:gen_random_uuid()"`
	SourceMeta
	Name      string    `gorm:"column:name"`
	Slug      string    `gorm:"column:slug"`
	CreatedAt time.Time `gorm:"column:created_at;autoCreateTime"`
	UpdatedAt time.Time `gorm:"column:updated_at;autoUpdateTime"`
}

func (Keyword) TableName() string { return "keyword" }

// Collection maps to collection table
type Collection struct {
	ID uuid.UUID `gorm:"column:collection_id;type:uuid;primaryKey;default:gen_random_uuid()"`
	SourceMeta
	Name      string    `gorm:"column:name"`
	Slug      string    `gorm:"column:slug"`
	CreatedAt time.Time `gorm:"column:created_at;autoCreateTime"`
	UpdatedAt time.Time `gorm:"column:updated_at;autoUpdateTime"`
}

func (Collection) TableName() string { return "collection" }

// Franchise maps to franchise table
type Franchise struct {
	ID uuid.UUID `gorm:"column:franchise_id;type:uuid;primaryKey;default:gen_random_uuid()"`
	SourceMeta
	Name      string    `gorm:"column:name"`
	Slug      string    `gorm:"column:slug"`
	CreatedAt time.Time `gorm:"column:created_at;autoCreateTime"`
	UpdatedAt time.Time `gorm:"column:updated_at;autoUpdateTime"`
}

func (Franchise) TableName() string { return "franchise" }

// Company maps to company table
type Company struct {
	ID uuid.UUID `gorm:"column:company_id;type:uuid;primaryKey;default:gen_random_uuid()"`
	SourceMeta
	Name        string     `gorm:"column:name"`
	Country     *int16     `gorm:"column:country"`
	Description *string    `gorm:"column:description"`
	LogoAssetID *uuid.UUID `gorm:"column:logo_asset_id"`
	CreatedAt   time.Time  `gorm:"column:created_at;autoCreateTime"`
	UpdatedAt   time.Time  `gorm:"column:updated_at;autoUpdateTime"`
}

func (Company) TableName() string { return "company" }

// InvolvedCompany maps to involved_company table
type InvolvedCompany struct {
	ID uuid.UUID `gorm:"column:involved_company_id;type:uuid;primaryKey;default:gen_random_uuid()"`
	SourceMeta
	GameID     uuid.UUID `gorm:"column:game_id;type:uuid;not null"`
	CompanyID  uuid.UUID `gorm:"column:company_id;type:uuid;not null"`
	Developer  bool      `gorm:"column:developer"`
	Publisher  bool      `gorm:"column:publisher"`
	Porting    bool      `gorm:"column:porting"`
	Supporting bool      `gorm:"column:supporting"`
	CreatedAt  time.Time `gorm:"column:created_at;autoCreateTime"`
	UpdatedAt  time.Time `gorm:"column:updated_at;autoUpdateTime"`
}

func (InvolvedCompany) TableName() string { return "involved_company" }

// Link tables (composite PK) for many-to-many relations

type GameGenre struct {
	GameID  uuid.UUID `gorm:"column:game_id;type:uuid;primaryKey"`
	GenreID uuid.UUID `gorm:"column:genre_id;type:uuid;primaryKey"`
}

func (GameGenre) TableName() string { return "game_genre" }

type GameTheme struct {
	GameID  uuid.UUID `gorm:"column:game_id;type:uuid;primaryKey"`
	ThemeID uuid.UUID `gorm:"column:theme_id;type:uuid;primaryKey"`
}

func (GameTheme) TableName() string { return "game_theme" }

type GameKeyword struct {
	GameID    uuid.UUID `gorm:"column:game_id;type:uuid;primaryKey"`
	KeywordID uuid.UUID `gorm:"column:keyword_id;type:uuid;primaryKey"`
}

func (GameKeyword) TableName() string { return "game_keyword" }

type GameModeLink struct {
	GameID     uuid.UUID `gorm:"column:game_id;type:uuid;primaryKey"`
	GameModeID uuid.UUID `gorm:"column:game_mode_id;type:uuid;primaryKey"`
}

func (GameModeLink) TableName() string { return "game_mode_link" }

type GamePlayerPerspective struct {
	GameID              uuid.UUID `gorm:"column:game_id;type:uuid;primaryKey"`
	PlayerPerspectiveID uuid.UUID `gorm:"column:player_perspective_id;type:uuid;primaryKey"`
}

func (GamePlayerPerspective) TableName() string { return "game_player_perspective" }

type GameFranchise struct {
	GameID      uuid.UUID `gorm:"column:game_id;type:uuid;primaryKey"`
	FranchiseID uuid.UUID `gorm:"column:franchise_id;type:uuid;primaryKey"`
}

func (GameFranchise) TableName() string { return "game_franchise" }
