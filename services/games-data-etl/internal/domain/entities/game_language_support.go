package entities

import (
	"github.com/google/uuid"
	"time"
)

// GameLanguageSupport indicates localized support type for
// a specific language.
// language_code & support_type_code store numeric codes
// referencing enumeration_lookup.
// Convenience decoded fields (LanguageISO, SupportType) are
// ignored by GORM.
type GameLanguageSupport struct {
	ID uuid.UUID `json:"game_language_support_id" gorm:"column:game_language_support_id;type:uuid;primaryKey;default:gen_random_uuid()"`
	SourceMeta
	GameID          uuid.UUID `json:"game_id" gorm:"column:game_id;type:uuid;not null"`
	LanguageCode    int       `json:"language_code" gorm:"column:language_code;not null"`
	SupportTypeCode int       `json:"support_type_code" gorm:"column:support_type_code;not null"`
	CreatedAt       time.Time `json:"created_at" gorm:"column:created_at;autoCreateTime"`
	UpdatedAt       time.Time `json:"updated_at" gorm:"column:updated_at;autoUpdateTime"`
	// Decoded convenience fields (populate after query if
	// needed)
	LanguageISO string              `json:"language_iso,omitempty" gorm:"-"`
	Type        LanguageSupportType `json:"type,omitempty" gorm:"-"`
}

func (GameLanguageSupport) TableName() string { return "game_language_support" }
