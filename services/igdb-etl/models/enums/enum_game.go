package enums

// GameCategory represents the IGDB games.category enumeration.
// Values mirror IGDB numeric codes for direct mapping.
type GameCategory int

const (
	GameCategoryMainGame      GameCategory = 0  // MAIN_GAME
	GameCategoryDLCAddon      GameCategory = 1  // DLC_ADDON
	GameCategoryExpansion     GameCategory = 2  // EXPANSION
	GameCategoryBundle        GameCategory = 3  // BUNDLE
	GameCategoryStandaloneExp GameCategory = 4  // STANDALONE_EXPANSION
	GameCategoryMod           GameCategory = 5  // MOD
	GameCategoryRemake        GameCategory = 8  // REMAKE
	GameCategoryRemaster      GameCategory = 9  // REMASTER
	GameCategoryExpandedGame  GameCategory = 10 // EXPANDED_GAME
	GameCategoryPort          GameCategory = 11 // PORT
	GameCategoryFork          GameCategory = 12 // FORK
	GameCategoryPack          GameCategory = 13 // PACK
	GameCategoryUpdate        GameCategory = 14 // UPDATE
	GameCategoryEpisode       GameCategory = 15 // EPISODE
	GameCategoryUnknown       GameCategory = -1 // UNKNOWN (fallback)
)

// ReleaseStatus represents IGDB games.status values.
type ReleaseStatus int

const (
	ReleaseStatusReleased    ReleaseStatus = 0
	ReleaseStatusAlpha       ReleaseStatus = 2
	ReleaseStatusBeta        ReleaseStatus = 3
	ReleaseStatusEarlyAccess ReleaseStatus = 4
	ReleaseStatusOffline     ReleaseStatus = 5
	ReleaseStatusCancelled   ReleaseStatus = 6
	ReleaseStatusRumored     ReleaseStatus = 7
	ReleaseStatusDelisted    ReleaseStatus = 8
	ReleaseStatusUnknown     ReleaseStatus = -1
)
