package entities

import (
	"github.com/google/uuid"
	"time"
)

// GameWebsite represents an external website reference for
// a game (official, store, social, etc.).
type GameWebsite struct {
	ID uuid.UUID `json:"website_id" gorm:"column:game_website_id;type:uuid;primaryKey;default:gen_random_uuid()"`
	SourceMeta
	GameID    uuid.UUID       `json:"game_id" gorm:"column:game_id;type:uuid;not null"`
	Category  WebsiteCategory `json:"category" gorm:"column:category_code"`
	URL       string          `json:"url" gorm:"column:url"`
	Trusted   *bool           `json:"trusted" gorm:"column:trusted"`
	Checksum  *string         `json:"checksum" gorm:"column:checksum"`
	CreatedAt time.Time       `json:"created_at" gorm:"column:created_at;autoCreateTime"`
	UpdatedAt time.Time       `json:"updated_at" gorm:"column:updated_at;autoUpdateTime"`
}

func (GameWebsite) TableName() string { return "game_website" }
