package repositories

import (
	"game-data-etl/internal/domain/entities"
	"game-data-etl/internal/domain/igdb_models"
	"github.com/google/uuid"
	"gorm.io/gorm"
)

// DimensionRepository provides lookup helpers for dimension entities by source_ref.
type DimensionRepository struct {
	db *gorm.DB
}

func NewDimensionRepository(db *gorm.DB) *DimensionRepository { return &DimensionRepository{db: db} }

// Fetch helpers
func (r *DimensionRepository) GenresBySourceRefs(refs []int32) ([]entities.Genre, error) {
	return fetch[entities.Genre](r.db, refs)
}
func (r *DimensionRepository) ThemesBySourceRefs(refs []int32) ([]entities.Theme, error) {
	return fetch[entities.Theme](r.db, refs)
}
func (r *DimensionRepository) KeywordsBySourceRefs(refs []int32) ([]entities.Keyword, error) {
	return fetch[entities.Keyword](r.db, refs)
}
func (r *DimensionRepository) GameModesBySourceRefs(refs []int32) ([]entities.GameMode, error) {
	return fetch[entities.GameMode](r.db, refs)
}
func (r *DimensionRepository) PerspectivesBySourceRefs(refs []int32) ([]entities.PlayerPerspective, error) {
	return fetch[entities.PlayerPerspective](r.db, refs)
}
func (r *DimensionRepository) FranchisesBySourceRefs(refs []int32) ([]entities.Franchise, error) {
	return fetch[entities.Franchise](r.db, refs)
}
func (r *DimensionRepository) CollectionBySourceRef(ref *int32) (*entities.Collection, error) {
	if ref == nil {
		return nil, nil
	}
	var c entities.Collection
	if err := r.db.Where("source_ref = ?", *ref).First(&c).Error; err != nil {
		return nil, err
	}
	return &c, nil
}

// Upsert helpers (create if missing, update mutable fields if existing and newer)
func (r *DimensionRepository) UpsertGenres(items []igdb_models.IGDBNamedEntity) error {
	return r.upsertNamed("genre", "genre_id", items)
}
func (r *DimensionRepository) UpsertThemes(items []igdb_models.IGDBNamedEntity) error {
	return r.upsertNamed("theme", "theme_id", items)
}
func (r *DimensionRepository) UpsertKeywords(items []igdb_models.IGDBNamedEntity) error {
	return r.upsertNamed("keyword", "keyword_id", items)
}
func (r *DimensionRepository) UpsertGameModes(items []igdb_models.IGDBNamedEntity) error {
	return r.upsertNamed("game_mode", "game_mode_id", items)
}
func (r *DimensionRepository) UpsertPerspectives(items []igdb_models.IGDBNamedEntity) error {
	return r.upsertNamed("player_perspective", "player_perspective_id", items)
}
func (r *DimensionRepository) UpsertFranchises(items []igdb_models.IGDBNamedEntity) error {
	return r.upsertNamed("franchise", "franchise_id", items)
}
func (r *DimensionRepository) UpsertCollections(items []igdb_models.IGDBNamedEntity) error {
	return r.upsertNamed("collection", "collection_id", items)
}

func (r *DimensionRepository) upsertNamed(table, idCol string, items []igdb_models.IGDBNamedEntity) error {
	return r.db.Transaction(func(tx *gorm.DB) error {
		for _, it := range items {
			// Try update first if newer
			res := tx.Table(table).Where("source_ref = ?", it.ID).Updates(map[string]any{
				"name":              it.Name,
				"slug":              it.Slug,
				"updated_at_source": it.UpdatedAt.Time,
			})
			if res.Error != nil {
				return res.Error
			}
			if res.RowsAffected == 0 { // insert
				insert := map[string]any{
					idCol:               uuid.New(),
					"source_ref":        it.ID,
					"name":              it.Name,
					"slug":              it.Slug,
					"created_at_source": it.CreatedAt.Time,
					"updated_at_source": it.UpdatedAt.Time,
				}
				if err := tx.Table(table).Create(insert).Error; err != nil {
					return err
				}
			}
		}
		return nil
	})
}

// Generic fetch helper using Go generics.
func fetch[T any](db *gorm.DB, refs []int32) ([]T, error) {
	var result []T
	if len(refs) == 0 {
		return result, nil
	}
	int64s := make([]int64, 0, len(refs))
	for _, r := range refs {
		int64s = append(int64s, int64(r))
	}
	if err := db.Where("source_ref IN ?", int64s).Find(&result).Error; err != nil {
		return nil, err
	}
	return result, nil
}
