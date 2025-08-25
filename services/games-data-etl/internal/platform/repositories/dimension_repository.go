package repositories

import (
	"game-data-etl/internal/domain/entities"
	"game-data-etl/internal/domain/igdb_models"
	"github.com/google/uuid"
	"gorm.io/gorm"
	"gorm.io/gorm/clause"
)

// DimensionRepository provides lookup helpers for dimension
// entities by source_ref.
type DimensionRepository struct {
	db *gorm.DB
}

func NewDimensionRepository(
	db *gorm.DB,
) *DimensionRepository {
	return &DimensionRepository{db: db}
}

// Fetch helpers
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
func (r *DimensionRepository) PlatformsBySourceRefs(
	refs []int32,
) ([]entities.Platform, error) {
	return fetch[entities.Platform](r.db, refs)
}
func (r *DimensionRepository) CollectionsBySourceRefs(
	refs []int32,
) ([]entities.Collection, error) {
	return fetch[entities.Collection](r.db, refs)
}
func (r *DimensionRepository) AgeRatingsBySourceRefs(
	refs []int32,
) ([]entities.AgeRating, error) {
	return fetch[entities.AgeRating](r.db, refs)
}

// Fetch helpers additions
func (r *DimensionRepository) AgeRatingOrganizationsBySourceRefs(refs []int32) ([]entities.AgeRatingOrganization, error) {
	return fetch[entities.AgeRatingOrganization](r.db, refs)
}
func (r *DimensionRepository) AgeRatingContentDescriptionsBySourceRefs(refs []int32) ([]entities.AgeRatingContentDescription, error) {
	return fetch[entities.AgeRatingContentDescription](r.db, refs)
}

// UpsertGenres Upsert helpers (create if missing, update
// mutable fields if existing and newer)
func (r *DimensionRepository) UpsertGenres(
	items []igdb_models.IGDBNamedEntity,
) error {
	return r.upsertNamed("genre", "genre_id", items)
}
func (r *DimensionRepository) UpsertThemes(
	items []igdb_models.IGDBNamedEntity,
) error {
	return r.upsertNamed("theme", "theme_id", items)
}
func (r *DimensionRepository) UpsertKeywords(
	items []igdb_models.IGDBNamedEntity,
) error {
	return r.upsertNamed("keyword", "keyword_id", items)
}
func (r *DimensionRepository) UpsertGameModes(
	items []igdb_models.IGDBNamedEntity,
) error {
	return r.upsertNamed("game_mode", "game_mode_id", items)
}
func (r *DimensionRepository) UpsertPerspectives(
	items []igdb_models.IGDBNamedEntity,
) error {
	return r.upsertNamed("player_perspective", "player_perspective_id", items)
}
func (r *DimensionRepository) UpsertFranchises(
	items []igdb_models.IGDBNamedEntity,
) error {
	return r.upsertNamed("franchise", "franchise_id", items)
}
func (r *DimensionRepository) UpsertCollections(
	items []igdb_models.IGDBNamedEntity,
) error {
	return r.upsertNamed("collection", "collection_id", items)
}
func (r *DimensionRepository) UpsertPlatforms(
	items []igdb_models.IGDBPlatform,
) error {
	return r.db.Transaction(func(tx *gorm.DB) error {
		for _, it := range items {
			res := tx.Table("platform").Where("source_ref = ?", it.ID).Updates(map[string]any{
				"name":              it.Name,
				"abbreviation":      nullIfEmpty(it.Abbrev),
				"generation":        it.Generation,
				"category_code":     it.Category,
				"updated_at_source": it.UpdatedAt.Time,
			})
			if res.Error != nil {
				return res.Error
			}
			if res.RowsAffected == 0 {
				insert := map[string]any{
					"platform_id":       uuid.New(),
					"source_ref":        it.ID,
					"name":              it.Name,
					"abbreviation":      nullIfEmpty(it.Abbrev),
					"generation":        it.Generation,
					"category_code":     it.Category,
					"created_at_source": it.CreatedAt.Time,
					"updated_at_source": it.UpdatedAt.Time,
				}
				if err := tx.Table("platform").Create(insert).Error; err != nil {
					return err
				}
			}
		}
		return nil
	})
}
func (r *DimensionRepository) UpsertCompanies(
	items []igdb_models.IGDBCompany,
) error {
	return r.db.Transaction(func(tx *gorm.DB) error {
		for _, it := range items {
			res := tx.Table("company").Where("source_ref = ?", it.ID).Updates(map[string]any{
				"name":              it.Name,
				"country":           it.Country,
				"description":       nullIfEmpty(it.Description),
				"updated_at_source": it.UpdatedAt.Time,
			})
			if res.Error != nil {
				return res.Error
			}
			if res.RowsAffected == 0 {
				insert := map[string]any{
					"company_id":        uuid.New(),
					"source_ref":        it.ID,
					"name":              it.Name,
					"country":           it.Country,
					"description":       nullIfEmpty(it.Description),
					"created_at_source": it.CreatedAt.Time,
					"updated_at_source": it.UpdatedAt.Time,
				}
				if err := tx.Table("company").Create(insert).Error; err != nil {
					return err
				}
			}
		}
		return nil
	})
}

func (r *DimensionRepository) CompanyIDsBySourceRefs(
	refs []int64,
) (map[int64]uuid.UUID, error) {
	result := map[int64]uuid.UUID{}
	if len(refs) == 0 {
		return result, nil
	}
	var rows []struct {
		SourceRef int64
		CompanyID uuid.UUID
	}
	if err := r.db.Table("company").Select("source_ref, company_id").Where("source_ref IN ?", refs).Scan(&rows).Error; err != nil {
		return nil, err
	}
	for _, rw := range rows {
		result[rw.SourceRef] = rw.CompanyID
	}
	return result, nil
}

func (r *DimensionRepository) PlatformIDsBySourceRefs(
	refs []int64,
) (map[int64]uuid.UUID, error) {
	result := map[int64]uuid.UUID{}
	if len(refs) == 0 {
		return result, nil
	}
	var rows []struct {
		SourceRef  int64
		PlatformID uuid.UUID
	}
	if err := r.db.Table("platform").Select("source_ref, platform_id").Where("source_ref IN ?", refs).Scan(&rows).Error; err != nil {
		return nil, err
	}
	for _, rw := range rows {
		result[rw.SourceRef] = rw.PlatformID
	}
	return result, nil
}

// ID mapping helpers
func (r *DimensionRepository) AgeRatingIDsBySourceRefs(refs []int64) (map[int64]uuid.UUID, error) {
	result := map[int64]uuid.UUID{}
	if len(refs) == 0 {
		return result, nil
	}
	var rows []struct {
		SourceRef   int64
		AgeRatingID uuid.UUID
	}
	if err := r.db.Table("age_rating").Select("source_ref, age_rating_id").Where("source_ref IN ?", refs).Scan(&rows).Error; err != nil {
		return nil, err
	}
	for _, rw := range rows {
		result[rw.SourceRef] = rw.AgeRatingID
	}
	return result, nil
}
func (r *DimensionRepository) AgeRatingOrganizationIDsBySourceRefs(refs []int64) (map[int64]uuid.UUID, error) {
	result := map[int64]uuid.UUID{}
	if len(refs) == 0 {
		return result, nil
	}
	var rows []struct {
		SourceRef               int64
		AgeRatingOrganizationID uuid.UUID
	}
	if err := r.db.Table("age_rating_organization").Select("source_ref, age_rating_organization_id").Where("source_ref IN ?", refs).Scan(&rows).Error; err != nil {
		return nil, err
	}
	for _, rw := range rows {
		if rw.AgeRatingOrganizationID != uuid.Nil {
			result[rw.SourceRef] = rw.AgeRatingOrganizationID
		}
	}
	return result, nil
}
func (r *DimensionRepository) AgeRatingContentDescriptionIDsBySourceRefs(refs []int64) (map[int64]uuid.UUID, error) {
	result := map[int64]uuid.UUID{}
	if len(refs) == 0 {
		return result, nil
	}
	var rows []struct {
		SourceRef                     int64
		AgeRatingContentDescriptionID uuid.UUID
	}
	if err := r.db.Table("age_rating_content_description").Select("source_ref, age_rating_content_description_id").Where("source_ref IN ?", refs).Scan(&rows).Error; err != nil {
		return nil, err
	}
	for _, rw := range rows {
		if rw.AgeRatingContentDescriptionID != uuid.Nil {
			result[rw.SourceRef] = rw.AgeRatingContentDescriptionID
		}
	}
	return result, nil
}

// Upserts for new age rating metadata entities
func (r *DimensionRepository) UpsertAgeRatingOrganizations(items []igdb_models.IGDBAgeRatingOrganization) error {
	return r.db.Transaction(func(tx *gorm.DB) error {
		for _, it := range items {
			upd := map[string]any{"name": it.Name, "checksum": nullIfEmpty(it.Checksum), "updated_at_source": it.UpdatedAt.Time}
			res := tx.Table("age_rating_organization").Where("source_ref = ?", it.ID).Updates(upd)
			if res.Error != nil {
				return res.Error
			}
			if res.RowsAffected == 0 {
				ins := map[string]any{"age_rating_organization_id": uuid.New(), "source_ref": it.ID, "name": it.Name, "checksum": nullIfEmpty(it.Checksum), "created_at_source": it.CreatedAt.Time, "updated_at_source": it.UpdatedAt.Time}
				if err := tx.Table("age_rating_organization").Create(ins).Error; err != nil {
					return err
				}
			}
		}
		return nil
	})
}

func (r *DimensionRepository) UpsertAgeRatingContentDescriptions(items []igdb_models.IGDBAgeRatingContentDescriptionV2) error {
	// ensure organizations exist first
	orgRefsSet := map[int64]struct{}{}
	for _, it := range items {
		if it.Organization > 0 {
			orgRefsSet[it.Organization] = struct{}{}
		}
	}
	if len(orgRefsSet) > 0 {
		refs := make([]int32, 0, len(orgRefsSet))
		for k := range orgRefsSet {
			refs = append(refs, int32(k))
		}
		existing, _ := r.AgeRatingOrganizationsBySourceRefs(refs)
		present := map[int64]struct{}{}
		for _, e := range existing {
			present[e.SourceRef] = struct{}{}
		}
		missing := make([]int32, 0)
		for k := range orgRefsSet {
			if _, ok := present[k]; !ok {
				missing = append(missing, int32(k))
			}
		}
		// caller responsible for fetching & passing missing organizations if needed; skip here
	}
	return r.db.Transaction(func(tx *gorm.DB) error {
		// map organization source_ref to uuid
		orgRefs := make([]int64, 0)
		for _, it := range items {
			if it.Organization > 0 {
				orgRefs = append(orgRefs, it.Organization)
			}
		}
		orgMap, _ := r.AgeRatingOrganizationIDsBySourceRefs(orgRefs)
		for _, it := range items {
			var orgID any
			if id, ok := orgMap[it.Organization]; ok && id != uuid.Nil {
				orgID = id
			}
			upd := map[string]any{"organization_id": orgID, "description": nullIfEmpty(it.Description), "description_type": it.DescriptionType, "checksum": nullIfEmpty(it.Checksum), "updated_at_source": it.UpdatedAt.Time}
			res := tx.Table("age_rating_content_description").Where("source_ref = ?", it.ID).Updates(upd)
			if res.Error != nil {
				return res.Error
			}
			if res.RowsAffected == 0 {
				ins := map[string]any{"age_rating_content_description_id": uuid.New(), "source_ref": it.ID, "organization_id": orgID, "description": nullIfEmpty(it.Description), "description_type": it.DescriptionType, "checksum": nullIfEmpty(it.Checksum), "created_at_source": it.CreatedAt.Time, "updated_at_source": it.UpdatedAt.Time}
				if err := tx.Table("age_rating_content_description").Create(ins).Error; err != nil {
					return err
				}
			}
		}
		return nil
	})
}

func (r *DimensionRepository) UpsertAgeRatingContentDescriptionLinks(ageRatings []igdb_models.IGDBAgeRating) error {
	return r.db.Transaction(func(tx *gorm.DB) error {
		// collect unique age rating and description refs
		arRefs := make([]int64, 0, len(ageRatings))
		descRefSet := map[int64]struct{}{}
		for _, ar := range ageRatings {
			arRefs = append(arRefs, ar.ID)
			for _, d := range ar.ContentDescriptions {
				descRefSet[d] = struct{}{}
			}
		}
		// maps
		arMap, err := r.AgeRatingIDsBySourceRefs(arRefs)
		if err != nil {
			return err
		}
		descRefs := make([]int64, 0, len(descRefSet))
		for k := range descRefSet {
			descRefs = append(descRefs, k)
		}
		descMap, err := r.AgeRatingContentDescriptionIDsBySourceRefs(descRefs)
		if err != nil {
			return err
		}
		for _, ar := range ageRatings {
			arID, ok := arMap[ar.ID]
			if !ok {
				continue
			}
			for _, d := range ar.ContentDescriptions {
				dID, ok := descMap[d]
				if !ok {
					continue
				}
				// insert ignore if exists
				ln := map[string]any{"age_rating_id": arID, "age_rating_content_description_id": dID}
				if err := tx.Clauses(clause.OnConflict{DoNothing: true}).Table("age_rating_content_description_link").Create(ln).Error; err != nil {
					return err
				}
			}
		}
		return nil
	})
}

func nullIfEmpty(s string) any {
	if s == "" {
		return nil
	}
	return s
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

func (r *DimensionRepository) upsertNamed(
	table,
	idCol string,
	items []igdb_models.IGDBNamedEntity,
) error {
	return r.db.Transaction(func(tx *gorm.DB) error {
		for _, it := range items {
			res := tx.Table(table).Where("source_ref = ?", it.ID).Updates(map[string]any{
				"name":              it.Name,
				"slug":              it.Slug,
				"updated_at_source": it.UpdatedAt.Time,
			})
			if res.Error != nil {
				return res.Error
			}
			if res.RowsAffected == 0 {
				insert := map[string]any{
					idCol:               uuid.New(),
					"source_ref":        it.ID,
					"name":              it.Name,
					"slug":              it.Slug,
					"created_at_source": it.CreatedAt.Time,
					"updated_at_source": it.UpdatedAt.Time,
				}
				if err := tx.Table(table).Create(insert).Error; err !=
					nil {
					return err
				}
			}
		}
		return nil
	})
}

func (r *DimensionRepository) UpsertAgeRatings(
	items []igdb_models.IGDBAgeRating,
) error {
	if len(items) == 0 {
		return nil
	}
	return r.db.Transaction(func(tx *gorm.DB) error {
		for _, it := range items {
			var orgVal any
			if it.Organization > 0 {
				orgVal = it.Organization
			}
			var ratingVal any
			if it.RatingCategory > 0 {
				ratingVal = it.RatingCategory
			}
			upd := map[string]any{
				"organization_code": orgVal,
				"rating_code":       ratingVal,
				"synopsis":          nullIfEmpty(it.Synopsis),
				"updated_at_source": it.UpdatedAt.Time,
			}
			res := tx.Table("age_rating").Where("source_ref = ?", it.ID).Updates(upd)
			if res.Error != nil {
				return res.Error
			}
			if res.RowsAffected == 0 { // insert
				ins := map[string]any{
					"age_rating_id":     uuid.New(),
					"source_ref":        it.ID,
					"organization_code": orgVal,
					"rating_code":       ratingVal,
					"synopsis":          nullIfEmpty(it.Synopsis),
					"created_at_source": it.CreatedAt.Time,
					"updated_at_source": it.UpdatedAt.Time,
				}
				if err := tx.Table("age_rating").Create(ins).Error; err != nil {
					return err
				}
			}
		}
		return nil
	})
}
