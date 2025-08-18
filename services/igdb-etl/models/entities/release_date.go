package entities

import (
	"github.com/google/uuid"
	"time"
)

// ReleaseDate represents a platform-specific release event for a Game.
type ReleaseDate struct {
	ID uuid.UUID `json:"release_date_id" gorm:"column:release_date_id;type:uuid;primaryKey;default:gen_random_uuid()"`
	SourceMeta
	GameID     uuid.UUID      `json:"game_id" gorm:"column:game_id;type:uuid;not null"`
	PlatformID uuid.UUID      `json:"platform_id" gorm:"column:platform_id;type:uuid;not null"`
	Date       *time.Time     `json:"date" gorm:"column:date"`
	Region     *Region        `json:"region" gorm:"column:region_code"`
	Category   *int16         `json:"category" gorm:"column:category_code"`
	Status     *ReleaseStatus `json:"status" gorm:"column:status_code"`
	CreatedAt  time.Time      `json:"created_at" gorm:"column:created_at;autoCreateTime"`
	UpdatedAt  time.Time      `json:"updated_at" gorm:"column:updated_at;autoUpdateTime"`
}

func (ReleaseDate) TableName() string { return "release_date" }
