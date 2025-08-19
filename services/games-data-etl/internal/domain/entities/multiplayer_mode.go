package entities

import (
	"github.com/google/uuid"
	"time"
)

// MultiplayerMode details multiplayer capabilities for a game.
type MultiplayerMode struct {
	ID uuid.UUID `json:"multiplayer_mode_id" gorm:"column:multiplayer_mode_id;type:uuid;primaryKey;default:gen_random_uuid()"`
	SourceMeta
	GameID            uuid.UUID `json:"game_id" gorm:"column:game_id;type:uuid;not null"`
	CampaignCoop      *bool     `json:"campaign_coop" gorm:"column:campaign_coop"`
	DropIn            *bool     `json:"drop_in" gorm:"column:drop_in"`
	LANCoop           *bool     `json:"lan_coop" gorm:"column:lan_coop"`
	OfflineCoop       *bool     `json:"offline_coop" gorm:"column:offline_coop"`
	OfflineCoopMax    *int16    `json:"offline_coop_max" gorm:"column:offline_coop_max"`
	OfflineMax        *int16    `json:"offline_max" gorm:"column:offline_max"`
	OnlineCoop        *bool     `json:"online_coop" gorm:"column:online_coop"`
	OnlineCoopMax     *int16    `json:"online_coop_max" gorm:"column:online_coop_max"`
	OnlineMax         *int16    `json:"online_max" gorm:"column:online_max"`
	Splitscreen       *bool     `json:"splitscreen" gorm:"column:splitscreen"`
	SplitscreenOnline *bool     `json:"splitscreen_online" gorm:"column:splitscreen_online"`
	CreatedAt         time.Time `json:"created_at" gorm:"column:created_at;autoCreateTime"`
	UpdatedAt         time.Time `json:"updated_at" gorm:"column:updated_at;autoUpdateTime"`
}

func (MultiplayerMode) TableName() string { return "multiplayer_mode" }
