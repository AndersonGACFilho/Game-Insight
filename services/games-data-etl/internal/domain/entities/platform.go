package entities

import (
	"github.com/google/uuid"
	"time"
)

// Platform represents a hardware platform or operating
// system on which games run.
type Platform struct {
	ID uuid.UUID `json:"platform_id" gorm:"column:platform_id;type:uuid;primaryKey;default:gen_random_uuid()"`
	SourceMeta
	Name         string            `json:"name" gorm:"column:name"`
	Abbreviation *string           `json:"abbreviation" gorm:"column:abbreviation"`
	Generation   *int16            `json:"generation" gorm:"column:generation"`
	Category     *PlatformCategory `json:"category" gorm:"column:category_code"`
	CreatedAt    time.Time         `json:"created_at" gorm:"column:created_at;autoCreateTime"`
	UpdatedAt    time.Time         `json:"updated_at" gorm:"column:updated_at;autoUpdateTime"`
}

func (Platform) TableName() string { return "platform" }
