package igdb_models

// IGDBGame represents the structure of a game from the IGDB
// API
// Note: IGDB uses singular 'cover' field (single image id).
// We capture it separately.
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
	Collections           []int32  `json:"collections"`
	Frachises             []int32  `json:"franchises"`
	ParentGame            *int32   `json:"parent_game"`
	Platforms             []int32  `json:"platforms"`
	InvolvedCompanies     []int32  `json:"involved_companies"`
	AlternativeNames      []int32  `json:"alternative_names"`
	ReleaseDates          []int32  `json:"release_dates"`
	Screenshots           []int32  `json:"screenshots"`
	Artworks              []int32  `json:"artworks"`
	Cover                 *int32   `json:"cover"`
	MultiplayerModes      []int32  `json:"multiplayer_modes"`
	LanguageSupports      []int32  `json:"language_supports"`
	AgeRatings            []int32  `json:"age_ratings"`
	Achievements          []int32  `json:"achievements"`
	Websites              []int32  `json:"websites"`
	Videos                []int32  `json:"videos"`
	CreatedAt             UnixTime `json:"created_at"`
	UpdatedAt             UnixTime `json:"updated_at"`
}
