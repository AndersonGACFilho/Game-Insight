package entities

import (
	"time"

	"github.com/google/uuid"
)

// AgeRating dimension (no direct game FK). Linked via
// game_age_rating junction.
type AgeRating struct {
	ID uuid.UUID `json:"age_rating_id" gorm:"column:age_rating_id;type:uuid;primaryKey;default:gen_random_uuid()"`
	SourceMeta
	Organization *AgeRatingOrg `json:"organization" gorm:"column:organization_code"`
	RatingCode   *int16        `json:"rating_code" gorm:"column:rating_code"`
	Synopsis     *string       `json:"synopsis" gorm:"column:synopsis"`
	CreatedAt    time.Time     `json:"created_at" gorm:"column:created_at;autoCreateTime"`
	UpdatedAt    time.Time     `json:"updated_at" gorm:"column:updated_at;autoUpdateTime"`
}

// TableName overrides default pluralization.
func (AgeRating) TableName() string { return "age_rating" }

// AgeRatingOrganization dimension.
type AgeRatingOrganization struct {
	ID uuid.UUID `gorm:"column:age_rating_organization_id;type:uuid;primaryKey;default:gen_random_uuid()"`
	SourceMeta
	Name      string    `gorm:"column:name"`
	Checksum  *string   `gorm:"column:checksum"`
	CreatedAt time.Time `gorm:"column:created_at;autoCreateTime"`
	UpdatedAt time.Time `gorm:"column:updated_at;autoUpdateTime"`
}

func (AgeRatingOrganization) TableName() string { return "age_rating_organization" }

// AgeRatingContentDescription V2 dimension.
type AgeRatingContentDescription struct {
	ID uuid.UUID `gorm:"column:age_rating_content_description_id;type:uuid;primaryKey;default:gen_random_uuid()"`
	SourceMeta
	OrganizationID  *uuid.UUID `gorm:"column:organization_id"`
	Description     *string    `gorm:"column:description"`
	DescriptionType *int64     `gorm:"column:description_type"`
	Checksum        *string    `gorm:"column:checksum"`
	CreatedAt       time.Time  `gorm:"column:created_at;autoCreateTime"`
	UpdatedAt       time.Time  `gorm:"column:updated_at;autoUpdateTime"`
}

func (AgeRatingContentDescription) TableName() string { return "age_rating_content_description" }

// Link table between age_rating and content description.
type AgeRatingContentDescriptionLink struct {
	AgeRatingID                   uuid.UUID `gorm:"column:age_rating_id;type:uuid;primaryKey"`
	AgeRatingContentDescriptionID uuid.UUID `gorm:"column:age_rating_content_description_id;type:uuid;primaryKey"`
	CreatedAt                     time.Time `gorm:"column:created_at;autoCreateTime"`
}

func (AgeRatingContentDescriptionLink) TableName() string {
	return "age_rating_content_description_link"
}
