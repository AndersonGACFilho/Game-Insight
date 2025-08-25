package entities

import (
	"github.com/google/uuid"
	"time"
)

// GameVideo stores metadata about an official trailer or
// gameplay video.
type GameVideo struct {
	ID uuid.UUID `json:"game_video_id" gorm:"column:game_video_id;type:uuid;primaryKey;default:gen_random_uuid()"`
	SourceMeta
	GameID    uuid.UUID `json:"game_id" gorm:"column:game_id;type:uuid;not null"`
	Name      string    `json:"name" gorm:"column:name"`
	VideoID   string    `json:"video_id" gorm:"column:video_id"`
	Platform  *string   `json:"platform" gorm:"column:platform"`
	Checksum  *string   `json:"checksum" gorm:"column:checksum"`
	CreatedAt time.Time `json:"created_at" gorm:"column:created_at;autoCreateTime"`
	UpdatedAt time.Time `json:"updated_at" gorm:"column:updated_at;autoUpdateTime"`
}

func (GameVideo) TableName() string { return "game_video" }
