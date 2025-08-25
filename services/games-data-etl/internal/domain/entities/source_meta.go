package entities

import "time"

// SourceMeta captures common source and temporal metadata
// embedded in most entities.
type SourceMeta struct {
	SourceRef       int64     `json:"source_ref" gorm:"column:source_ref" db:"source_ref"`
	CreatedAtSource time.Time `json:"created_at_source" gorm:"column:created_at_source" db:"created_at_source"`
	UpdatedAtSource time.Time `json:"updated_at_source" gorm:"column:updated_at_source" db:"updated_at_source"`
}
