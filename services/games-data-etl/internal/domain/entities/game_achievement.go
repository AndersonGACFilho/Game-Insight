package entities

import (
	"github.com/google/uuid"
	"time"
)

// GameAchievement stores achievement metadata for a game.
type GameAchievement struct {
	ID uuid.UUID `json:"game_achievement_id" gorm:"column:game_achievement_id;type:uuid;primaryKey;default:gen_random_uuid()"`
	SourceMeta
	GameID              uuid.UUID            `json:"game_id" gorm:"column:game_id;type:uuid;not null"`
	Name                string               `json:"name" gorm:"column:name"`
	Slug                string               `json:"slug" gorm:"column:slug"`
	Description         *string              `json:"description" gorm:"column:description"`
	Category            *AchievementCategory `json:"category" gorm:"column:category_code"`
	OrderIndex          *int32               `json:"order_index" gorm:"column:order_index"`
	Points              *int32               `json:"points" gorm:"column:points"`
	Secret              bool                 `json:"secret" gorm:"column:secret"`
	UnlockedIconAssetID *uuid.UUID           `json:"unlocked_icon_asset_id" gorm:"column:unlocked_icon_asset_id"`
	LockedIconAssetID   *uuid.UUID           `json:"locked_icon_asset_id" gorm:"column:locked_icon_asset_id"`
	Checksum            *string              `json:"checksum" gorm:"column:checksum"`
	CreatedAt           time.Time            `json:"created_at" gorm:"column:created_at;autoCreateTime"`
	UpdatedAt           time.Time            `json:"updated_at" gorm:"column:updated_at;autoUpdateTime"`
}

func (GameAchievement) TableName() string { return "game_achievement" }
