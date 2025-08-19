package repositories

import (
	"errors"
	"game-data-etl/internal/domain/entities"
	"github.com/google/uuid"
	"gorm.io/gorm"
)

type GameRepository struct {
	db *gorm.DB
}

func NewGameRepository(db *gorm.DB) *GameRepository {
	return &GameRepository{db: db}
}

func (r *GameRepository) Save(game *entities.Game) error {
	return r.db.Create(game).Error
}

// SaveWithAssociations persists a Game and its many-to-many associations.
// Assumes dimension entities (genres, themes, etc.) already exist.
func (r *GameRepository) SaveWithAssociations(game *entities.Game) error {
	return r.db.Transaction(func(tx *gorm.DB) error {
		// Try to find existing by source_ref
		var existing entities.Game
		err := tx.Where("source_ref = ?", game.SourceMeta.SourceRef).First(&existing).Error
		if err != nil {
			if errors.Is(err, gorm.ErrRecordNotFound) {
				// Insert new
				if err := tx.Create(game).Error; err != nil {
					return err
				}
			} else {
				return err
			}
		} else {
			// Update existing: ensure we keep same ID
			game.ID = existing.ID
			if err := tx.Model(&existing).Updates(game).Error; err != nil {
				return err
			}
		}

		// Replace many-to-many associations (idempotent)
		assocNames := []string{"Genres", "Themes", "Keywords", "GameModes", "Perspectives", "Franchises"}
		for _, name := range assocNames {
			if err := tx.Model(game).Association(name).Replace(r.sliceForAssociation(game, name)); err != nil {
				return err
			}
		}
		return nil
	})
}

// sliceForAssociation returns the slice value for the given association name.
func (r *GameRepository) sliceForAssociation(game *entities.Game, name string) any {
	switch name {
	case "Genres":
		return game.Genres
	case "Themes":
		return game.Themes
	case "Keywords":
		return game.Keywords
	case "GameModes":
		return game.GameModes
	case "Perspectives":
		return game.Perspectives
	case "Franchises":
		return game.Franchises
	default:
		return nil
	}
}

// FindExistingGameIDs maps source_ref -> game_id for provided source refs.
func (r *GameRepository) FindExistingGameIDs(sourceRefs []int64) (map[int64]string, error) {
	if len(sourceRefs) == 0 {
		return map[int64]string{}, nil
	}
	var rows []struct {
		SourceRef int64  `gorm:"column:source_ref"`
		GameID    string `gorm:"column:game_id"`
	}
	if err := r.db.Table("game").Select("source_ref, game_id").Where("source_ref IN ?", sourceRefs).Scan(&rows).Error; err != nil {
		return nil, err
	}
	result := make(map[int64]string, len(rows))
	for _, rrow := range rows {
		result[rrow.SourceRef] = rrow.GameID
	}
	return result, nil
}

func (r *GameRepository) FindGameIDBySourceRef(sourceRef int64) (*uuid.UUID, error) {
	var row struct {
		ID uuid.UUID `gorm:"column:game_id"`
	}
	if err := r.db.Table("game").Select("game_id").Where("source_ref = ?", sourceRef).First(&row).Error; err != nil {
		return nil, err
	}
	return &row.ID, nil
}

func (r *GameRepository) ResolveParentBySourceRef(game *entities.Game, parentSourceRef int64) error {
	var parent entities.Game
	if err := r.db.Select("game_id").Where("source_ref = ?", parentSourceRef).First(&parent).Error; err != nil {
		return err
	}
	game.ParentGameID = &parent.ID
	return nil
}

func (r *GameRepository) ExistsBySourceRef(sourceRef int64) (bool, error) {
	var id uuid.UUID
	err := r.db.Table("game").Select("game_id").Where("source_ref = ?", sourceRef).Scan(&id).Error
	if errors.Is(err, gorm.ErrRecordNotFound) {
		return false, nil
	}
	if err != nil {
		return false, err
	}
	if id == uuid.Nil {
		return false, nil
	}
	return true, nil
}
