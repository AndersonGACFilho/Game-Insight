package entities

import (
	"github.com/google/uuid"
)

// MediaAsset represents visual assets like covers, screenshots, and artwork.
type MediaAsset struct {
	ID uuid.UUID `json:"asset_id" gorm:"column:asset_id;type:uuid;primaryKey;default:gen_random_uuid()"`
	SourceMeta
	GameID   *uuid.UUID `json:"game_id" gorm:"column:game_id;type:uuid"`
	Type     string     `json:"type" gorm:"column:type"` // COVER, SCREENSHOT, ARTWORK, etc.
	Width    *int32     `json:"width" gorm:"column:width"`
	Height   *int32     `json:"height" gorm:"column:height"`
	URL      string     `json:"url" gorm:"column:url_original"`
	Checksum *string    `json:"checksum" gorm:"column:checksum"`

	// Association
	Game *Game `json:"game" gorm:"foreignKey:GameID"`
}

// TableName overrides pluralization.
func (MediaAsset) TableName() string { return "media_asset" }

// Asset is an alias for MediaAsset to maintain backward compatibility
type Asset = MediaAsset
