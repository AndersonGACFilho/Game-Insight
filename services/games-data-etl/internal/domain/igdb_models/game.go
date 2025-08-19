package igdb_models

// IGDBGame represents the structure of a game from the IGDB API
type IGDBGame struct {
	ID                    int64    `json:"id"`
	Name                  string   `json:"name"`
	Summary               string   `json:"summary"`
	StoryLine             string   `json:"storyline"`
	Category              int16    `json:"category"`
	Status                int16    `json:"status"`
	FirstReleaseDate      UnixTime `json:"first_release_date"`
	TotalRating           float64  `json:"total_rating"`
	TotalRatingCount      int32    `json:"total_rating_count"`
	AggregatedRating      float64  `json:"aggregated_rating"`
	AggregatedRatingCount int32    `json:"aggregated_rating_count"`
	Popularity            float64  `json:"popularity"`
	Keywords              []int32  `json:"keywords"`
	Genres                []int32  `json:"genres"`
	Themes                []int32  `json:"themes"`
	GameModes             []int32  `json:"game_modes"`
	PlayersPerspectives   []int32  `json:"player_perspectives"`
	Collection            *int32   `json:"collection"`
	Frachises             []int32  `json:"franchises"`
	ParentGame            *int32   `json:"parent_game"`
	CreatedAt             UnixTime `json:"created_at"`
	UpdatedAt             UnixTime `json:"updated_at"`
}
