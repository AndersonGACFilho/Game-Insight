package repositories

import (
	"game-data-etl/internal/domain/entities"
	"gorm.io/gorm"
)

// DimensionRepository provides lookup helpers for dimension entities by source_ref.
type DimensionRepository struct {
	db *gorm.DB
}

func NewDimensionRepository(
	db *gorm.DB,
) *DimensionRepository {
	return &DimensionRepository{db: db}
}

func (r *DimensionRepository) GenresBySourceRefs(
	refs []int32,
) ([]entities.Genre, error) {
	return fetch[entities.Genre](r.db, refs)
}
func (r *DimensionRepository) ThemesBySourceRefs(
	refs []int32,
) ([]entities.Theme, error) {
	return fetch[entities.Theme](r.db, refs)
}
func (r *DimensionRepository) KeywordsBySourceRefs(
	refs []int32,
) ([]entities.Keyword, error) {
	return fetch[entities.Keyword](r.db, refs)
}
func (r *DimensionRepository) GameModesBySourceRefs(
	refs []int32,
) ([]entities.GameMode, error) {
	return fetch[entities.GameMode](r.db, refs)
}
func (r *DimensionRepository) PerspectivesBySourceRefs(
	refs []int32,
) ([]entities.PlayerPerspective, error) {
	return fetch[entities.PlayerPerspective](r.db, refs)
}
func (r *DimensionRepository) FranchisesBySourceRefs(
	refs []int32,
) ([]entities.Franchise, error) {
	return fetch[entities.Franchise](r.db, refs)
}

func (r *DimensionRepository) CollectionBySourceRef(
	ref *int32,
) (*entities.Collection, error) {
	if ref == nil {
		return nil, nil
	}
	var c entities.Collection
	if err := r.db.Where(
		"source_ref = ?", *ref,
	).First(&c).Error; err != nil {
		return nil, err
	}
	return &c, nil
}

// Generic fetch helper using Go 1.18+ generics.
func fetch[T any](db *gorm.DB, refs []int32) ([]T, error) {
	var result []T
	if len(refs) == 0 {
		return result, nil
	}
	int64s := make([]int64, 0, len(refs))
	for _, r := range refs {
		int64s = append(int64s, int64(r))
	}
	if err := db.Where(
		"source_ref IN ?", int64s,
	).Find(&result).Error; err != nil {
		return nil, err
	}
	return result, nil
}
