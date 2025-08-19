package entities

import (
	"github.com/google/uuid"
	"time"
)

// GameAltName stores alternative titles for localization or regional branding.
type GameAltName struct {
	ID uuid.UUID `json:"alt_name_id" gorm:"column:game_alt_name_id;type:uuid;primaryKey;default:gen_random_uuid()"`
	SourceMeta
	GameID    uuid.UUID `json:"game_id" gorm:"column:game_id;type:uuid;not null"`
	Name      string    `json:"name" gorm:"column:name"`
	Comment   *string   `json:"comment" gorm:"column:comment"`
	Locale    *string   `json:"locale" gorm:"column:locale"`
	CreatedAt time.Time `json:"created_at" gorm:"column:created_at;autoCreateTime"`
	UpdatedAt time.Time `json:"updated_at" gorm:"column:updated_at;autoUpdateTime"`
}

func (GameAltName) TableName() string { return "game_alt_name" }
