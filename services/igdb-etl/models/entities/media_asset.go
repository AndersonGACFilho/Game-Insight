package entities

import (
	"github.com/google/uuid"
	"time"
)

// MediaAsset represents a visual or related binary asset (cover, screenshot, artwork, logos, achievement icons).
// Type differentiates asset variants.
type MediaAsset struct {
	ID uuid.UUID `json:"asset_id" gorm:"column:asset_id;type:uuid;primaryKey;default:gen_random_uuid()"`
	SourceMeta
	GameID      *uuid.UUID     `json:"game_id" gorm:"column:game_id"`
	Type        MediaAssetType `json:"type" gorm:"column:type"`
	Width       *int32         `json:"width" gorm:"column:width"`
	Height      *int32         `json:"height" gorm:"column:height"`
	URLOriginal string         `json:"url_original" gorm:"column:url_original"`
	Checksum    *string        `json:"checksum" gorm:"column:checksum"`
	CreatedAt   time.Time      `json:"created_at" gorm:"column:created_at;autoCreateTime"`
	UpdatedAt   time.Time      `json:"updated_at" gorm:"column:updated_at;autoUpdateTime"`
}

func (MediaAsset) TableName() string { return "media_asset" }
