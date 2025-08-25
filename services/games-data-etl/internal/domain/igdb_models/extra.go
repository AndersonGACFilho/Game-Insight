package igdb_models

// Additional IGDB lightweight models for related entities
// needed for enrichment.

type IGDBPlatform struct {
	ID         int64    `json:"id"`
	Name       string   `json:"name"`
	Abbrev     string   `json:"abbreviation"`
	Generation int16    `json:"generation"`
	Category   int16    `json:"category"`
	CreatedAt  UnixTime `json:"created_at"`
	UpdatedAt  UnixTime `json:"updated_at"`
}

type IGDBCompany struct {
	ID          int64    `json:"id"`
	Name        string   `json:"name"`
	Country     int16    `json:"country"`
	Description string   `json:"description"`
	CreatedAt   UnixTime `json:"created_at"`
	UpdatedAt   UnixTime `json:"updated_at"`
}

type IGDBInvolvedCompany struct {
	ID         int64    `json:"id"`
	Company    int64    `json:"company"`
	Game       int64    `json:"game"`
	Developer  bool     `json:"developer"`
	Publisher  bool     `json:"publisher"`
	Porting    bool     `json:"porting"`
	Supporting bool     `json:"supporting"`
	CreatedAt  UnixTime `json:"created_at"`
	UpdatedAt  UnixTime `json:"updated_at"`
}

type IGDBAlternativeName struct {
	ID        int64    `json:"id"`
	Game      int64    `json:"game"`
	Name      string   `json:"name"`
	Comment   string   `json:"comment"`
	CreatedAt UnixTime `json:"created_at"`
	UpdatedAt UnixTime `json:"updated_at"`
}

type IGDBReleaseDate struct {
	ID        int64    `json:"id"`
	Game      int64    `json:"game"`
	Platform  int64    `json:"platform"`
	Date      int64    `json:"date"`
	Region    int16    `json:"region"`
	Category  int16    `json:"category"`
	Status    int16    `json:"status"`
	CreatedAt UnixTime `json:"created_at"`
	UpdatedAt UnixTime `json:"updated_at"`
}

type IGDBMediaAsset struct {
	ID        int64    `json:"id"`
	Game      int64    `json:"game"`
	Width     int32    `json:"width"`
	Height    int32    `json:"height"`
	URL       string   `json:"url"`
	Checksum  string   `json:"checksum"`
	CreatedAt UnixTime `json:"created_at"`
	UpdatedAt UnixTime `json:"updated_at"`
}

type IGDBMultiplayerMode struct {
	ID                int64    `json:"id"`
	Game              int64    `json:"game"`
	CampaignCoop      bool     `json:"campaigncoop"`
	DropIn            bool     `json:"dropin"`
	LanCoop           bool     `json:"lancoop"`
	OfflineCoop       bool     `json:"offlinecoop"`
	OfflineCoopMax    int16    `json:"offlinecoopmax"`
	OfflineMax        int16    `json:"offlinemax"`
	OnlineCoop        bool     `json:"onlinecoop"`
	OnlineCoopMax     int16    `json:"onlinecoopmax"`
	OnlineMax         int16    `json:"onlinemax"`
	Splitscreen       bool     `json:"splitscreen"`
	SplitscreenOnline bool     `json:"splitscreenonline"`
	CreatedAt         UnixTime `json:"created_at"`
	UpdatedAt         UnixTime `json:"updated_at"`
}

type IGDBLanguageSupport struct {
	ID        int64    `json:"id"`
	Game      int64    `json:"game"`
	Language  int16    `json:"language"`
	Type      int16    `json:"language_support_type"`
	CreatedAt UnixTime `json:"created_at"`
	UpdatedAt UnixTime `json:"updated_at"`
}

// IGDBAgeRating represents a single age rating issued by a
// regional organization.
type IGDBAgeRating struct {
	ID                  int64    `json:"id"`
	Organization        int64    `json:"organization"`
	RatingCategory      int64    `json:"rating_category"`
	RatingCoverURL      string   `json:"rating_cover_url"`
	Synopsis            string   `json:"synopsis"`
	ContentDescriptions []int64  `json:"rating_content_descriptions"`
	CreatedAt           UnixTime `json:"created_at"`
	UpdatedAt           UnixTime `json:"updated_at"`
}

// IGDBAgeRatingOrganization represents rating
// organizations (ESRB, PEGI, etc.)
type IGDBAgeRatingOrganization struct {
	ID        int64    `json:"id"`
	Name      string   `json:"name"`
	Checksum  string   `json:"checksum"`
	CreatedAt UnixTime `json:"created_at"`
	UpdatedAt UnixTime `json:"updated_at"`
}

// IGDBAgeRatingContentDescriptionV2 represents descriptors (violence, etc.) v2 endpoint.
type IGDBAgeRatingContentDescriptionV2 struct {
	ID              int64    `json:"id"`
	Organization    int64    `json:"organization"`
	Description     string   `json:"description"`
	DescriptionType int64    `json:"description_type"`
	CreatedAt       UnixTime `json:"created_at"`
	UpdatedAt       UnixTime `json:"updated_at"`
	Checksum        string   `json:"checksum"`
}

type IGDBAchievement struct {
	ID           int64    `json:"id"`
	Game         int64    `json:"game"`
	Name         string   `json:"name"`
	Slug         string   `json:"slug"`
	Description  string   `json:"description"`
	Category     int16    `json:"category"`
	Order        int32    `json:"order"`
	Points       int32    `json:"points"`
	Hidden       bool     `json:"hidden"`
	UnlockedIcon int64    `json:"unlocked_icon"`
	LockedIcon   int64    `json:"locked_icon"`
	Checksum     string   `json:"checksum"`
	CreatedAt    UnixTime `json:"created_at"`
	UpdatedAt    UnixTime `json:"updated_at"`
}

type IGDBWebsite struct {
	ID        int64    `json:"id"`
	Game      int64    `json:"game"`
	Category  int32    `json:"category"`
	URL       string   `json:"url"`
	Trusted   bool     `json:"trusted"`
	Checksum  string   `json:"checksum"`
	CreatedAt UnixTime `json:"created_at"`
	UpdatedAt UnixTime `json:"updated_at"`
}

type IGDBGameVideo struct {
	ID        int64    `json:"id"`
	Game      int64    `json:"game"`
	Name      string   `json:"name"`
	VideoID   string   `json:"video_id"`
	Checksum  string   `json:"checksum"`
	CreatedAt UnixTime `json:"created_at"`
	UpdatedAt UnixTime `json:"updated_at"`
}
