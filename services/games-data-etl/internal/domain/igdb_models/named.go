package igdb_models

// IGDBNamedEntity represents a generic named dimension entity from IGDB (genre, theme, keyword, etc.)
type IGDBNamedEntity struct {
	ID        int64    `json:"id"`
	Name      string   `json:"name"`
	Slug      string   `json:"slug"`
	CreatedAt UnixTime `json:"created_at"`
	UpdatedAt UnixTime `json:"updated_at"`
}
