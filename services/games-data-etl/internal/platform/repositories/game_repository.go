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

// SaveWithAssociations is deprecated; use UpsertGraph. Left
// for backward compatibility.
func (r *GameRepository) SaveWithAssociations(
	game *entities.Game,
) error {
	return r.UpsertGraph(game)
}

// UpsertGraph persists a Game and its associations
// (many-to-many + one-to-many) idempotently.
func (r *GameRepository) UpsertGraph(
	game *entities.Game,
) error {
	return r.db.Transaction(func(tx *gorm.DB) error {
		var existing entities.Game
		err := tx.Where("source_ref = ?", game.SourceMeta.SourceRef).First(&existing).Error
		if err != nil {
			if errors.Is(err, gorm.ErrRecordNotFound) {
				if err := tx.Create(game).Error; err != nil {
					return err
				}
			} else {
				return err
			}
		} else {
			oldID := game.ID
			game.ID = existing.ID
			propagateChildIDs(game, existing.ID)
			if err := tx.Model(&existing).Updates(game).Error; err !=
				nil {
				return err
			}
			_ = oldID
		}

		assocNames := []string{"Genres", "Themes", "Keywords", "GameModes", "Perspectives", "Franchises", "AgeRatings", "Collections", "Platforms"}
		for _, name := range assocNames {
			if err :=
				tx.Model(game).Association(name).Replace(r.sliceForAssociation(game,
					name)); err != nil {
				return err
			}
		}

		gid := game.ID
		replaceChildren := func(model any, rows any) error {
			if err := tx.Where("game_id = ?", gid).Delete(model).Error; err != nil {
				return err
			}
			switch v := rows.(type) {
			case []entities.GameAltName:
				if len(v) > 0 {
					return tx.Create(&v).Error
				}
			case []entities.InvolvedCompany:
				if len(v) > 0 {
					return tx.Create(&v).Error
				}
			case []entities.ReleaseDate:
				if len(v) > 0 {
					return tx.Create(&v).Error
				}
			case []entities.MediaAsset:
				if len(v) > 0 {
					return tx.Create(&v).Error
				}
			case []entities.MultiplayerMode:
				if len(v) > 0 {
					return tx.Create(&v).Error
				}
			case []entities.GameLanguageSupport:
				if len(v) > 0 {
					return tx.Create(&v).Error
				}
			case []entities.GameWebsite:
				if len(v) > 0 {
					return tx.Create(&v).Error
				}
			case []entities.GameVideo:
				if len(v) > 0 {
					return tx.Create(&v).Error
				}
			case []entities.GameAchievement:
				if len(v) > 0 {
					return tx.Create(&v).Error
				}
			}
			return nil
		}

		if err := replaceChildren(&entities.GameAltName{},
			game.GameAltNames); err != nil {
			return err
		}
		if err := replaceChildren(&entities.InvolvedCompany{},
			game.Companies); err != nil {
			return err
		}
		if err := replaceChildren(&entities.ReleaseDate{},
			game.ReleaseDates); err != nil {
			return err
		}
		if err := replaceChildren(&entities.MediaAsset{},
			game.MediaAssets); err != nil {
			return err
		}
		if err := replaceChildren(&entities.MultiplayerMode{},
			game.MultiplayerModes); err != nil {
			return err
		}
		if err := replaceChildren(&entities.GameLanguageSupport{},
			game.LanguageSupports); err != nil {
			return err
		}
		if err := replaceChildren(&entities.GameAchievement{},
			game.Achievements); err != nil {
			return err
		}
		if err := replaceChildren(&entities.GameWebsite{},
			game.Websites); err != nil {
			return err
		}
		if err := replaceChildren(&entities.GameVideo{},
			game.Videos); err != nil {
			return err
		}

		return nil
	})
}

// propagateChildIDs updates one-to-many child slices to use
// the persisted gameID (AgeRatings removed as m2m).
func propagateChildIDs(g *entities.Game, gid uuid.UUID) {
	for i := range g.GameAltNames {
		g.GameAltNames[i].GameID = gid
	}
	for i := range g.Companies {
		g.Companies[i].GameID = gid
	}
	for i := range g.ReleaseDates {
		g.ReleaseDates[i].GameID = gid
	}
	for i := range g.MediaAssets {
		if g.MediaAssets[i].GameID != nil {
			*g.MediaAssets[i].GameID = gid
		} else {
			g.MediaAssets[i].GameID = &gid
		}
	}
	for i := range g.MultiplayerModes {
		g.MultiplayerModes[i].GameID = gid
	}
	for i := range g.LanguageSupports {
		g.LanguageSupports[i].GameID = gid
	}
	for i := range g.Achievements {
		g.Achievements[i].GameID = gid
	}
	for i := range g.Websites {
		g.Websites[i].GameID = gid
	}
	for i := range g.Videos {
		g.Videos[i].GameID = gid
	}
}

// sliceForAssociation returns the slice value for the given
// association name.
func (r *GameRepository) sliceForAssociation(
	game *entities.Game,
	name string,
) any {
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
	case "AgeRatings":
		return game.AgeRatings
	case "Collections":
		return game.Collections
	case "Platforms":
		return game.Platforms
	default:
		return nil
	}
}

// FindExistingGameIDs maps source_ref -> game_id for
// provided source refs.
func (r *GameRepository) FindExistingGameIDs(
	sourceRefs []int64,
) (map[int64]string, error) {
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

func (r *GameRepository) FindGameIDBySourceRef(
	sourceRef int64,
) (*uuid.UUID, error) {
	var row struct {
		ID string `gorm:"column:game_id"`
	}
	if err := r.db.Table("game").Select("game_id").Where("source_ref = ?", sourceRef).First(&row).Error; err != nil {
		return nil, err
	}
	uid, err := uuid.Parse(row.ID)
	if err != nil {
		return nil, err
	}
	return &uid, nil
}

func (r *GameRepository) ResolveParentBySourceRef(
	game *entities.Game,
	parentSourceRef int64,
) error {
	var row struct {
		ID string `gorm:"column:game_id"`
	}
	if err := r.db.Table("game").Select("game_id").Where("source_ref = ?", parentSourceRef).First(&row).Error; err != nil {
		return err
	}
	pid, err := uuid.Parse(row.ID)
	if err != nil {
		return err
	}
	game.ParentGameID = &pid
	return nil
}

func (r *GameRepository) ExistsBySourceRef(
	sourceRef int64,
) (bool, error) {
	var count int64
	if err := r.db.Table("game").Where("source_ref = ?", sourceRef).Count(&count).Error; err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			return false, nil
		}
		return false, err
	}
	return count > 0, nil
}
