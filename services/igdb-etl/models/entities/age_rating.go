package entities

import (
	"time"

	"github.com/google/uuid"
)

// AgeRating stores age classification for a game issued by a regional organization.
type AgeRating struct {
	ID           uuid.UUID     `json:"age_rating_id" gorm:"column:age_rating_id;type:uuid;primaryKey;default:gen_random_uuid()"`
	SourceMeta                 // embeds source_ref, created_at_source, updated_at_source
	Organization *AgeRatingOrg `json:"organization" gorm:"column:organization_code"`
	RatingCode   *int16        `json:"rating_code" gorm:"column:rating_code"`
	Synopsis     *string       `json:"synopsis" gorm:"column:synopsis"`
	CreatedAt    time.Time     `json:"created_at" gorm:"column:created_at;autoCreateTime"`
	UpdatedAt    time.Time     `json:"updated_at" gorm:"column:updated_at;autoUpdateTime"`
}

// TableName overrides default pluralization.
func (AgeRating) TableName() string { return "age_rating" }
