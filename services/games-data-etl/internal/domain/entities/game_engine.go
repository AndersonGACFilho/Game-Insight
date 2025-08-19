package entities

import (
	"github.com/google/uuid"
	"time"
)

// GameEngine represents an underlying technology used to build a game.
type GameEngine struct {
	ID uuid.UUID `json:"game_engine_id" gorm:"column:game_engine_id;type:uuid;primaryKey;default:gen_random_uuid()"`
	SourceMeta
	Name        string     `json:"name" gorm:"column:name"`
	Description *string    `json:"description" gorm:"column:description"`
	LogoAssetID *uuid.UUID `json:"logo_asset_id" gorm:"column:logo_asset_id"`
	CreatedAt   time.Time  `json:"created_at" gorm:"column:created_at;autoCreateTime"`
	UpdatedAt   time.Time  `json:"updated_at" gorm:"column:updated_at;autoUpdateTime"`
}

func (GameEngine) TableName() string { return "game_engine" }
