package entities

import (
	"github.com/google/uuid"
	"time"
)

// ExternalReference stores an external store or system
// identifier for a game.
type ExternalReference struct {
	ID         uuid.UUID `json:"external_reference_id" gorm:"column:external_reference_id;type:uuid;primaryKey;default:gen_random_uuid()"`
	GameID     uuid.UUID `json:"game_id" gorm:"column:game_id;type:uuid;not null"`
	Provider   string    `json:"provider" gorm:"column:provider;size:16;not null"`
	Category   string    `json:"category" gorm:"column:category;size:32;not null"`
	ExternalID string    `json:"external_id" gorm:"column:external_id;size:64;not null"`
	URL        *string   `json:"url" gorm:"column:url"`
	CreatedAt  time.Time `json:"created_at" gorm:"column:created_at;autoCreateTime"`
}

func (ExternalReference) TableName() string { return "external_reference" }
