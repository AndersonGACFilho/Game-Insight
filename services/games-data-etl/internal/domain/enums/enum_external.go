package enums

// ExternalRefCategory represents external_games.category
// codes (subset in use).
type ExternalRefCategory int

const (
	ExternalRefSteam      ExternalRefCategory = 1
	ExternalRefGOG        ExternalRefCategory = 5
	ExternalRefEpic       ExternalRefCategory = 10
	ExternalRefDiscord    ExternalRefCategory = 13
	ExternalRefGearbox    ExternalRefCategory = 14
	ExternalRefGooglePlay ExternalRefCategory = 15
	ExternalRefHumble     ExternalRefCategory = 20
	ExternalRefUnknown    ExternalRefCategory = -1
)

// LanguageSupportType enumerates language_support_types
// (subset / planned).
type LanguageSupportType int

const (
	LanguageSupportAudio        LanguageSupportType = 1
	LanguageSupportSubtitles    LanguageSupportType = 2
	LanguageSupportInterface    LanguageSupportType = 3
	LanguageSupportAudioPartial LanguageSupportType = 4
	LanguageSupportUnknown      LanguageSupportType = -1
)

// ReviewSource enumerates external review sources.
type ReviewSource string

const (
	ReviewSourceMetacritic ReviewSource = "METACRITIC"
	ReviewSourceOpenCritic ReviewSource = "OPENCRITIC"
	ReviewSourceIGN        ReviewSource = "IGN"
	ReviewSourceGameSpot   ReviewSource = "GAMESPOT"
	ReviewSourcePCGamer    ReviewSource = "PCGAMER"
	ReviewSourceEurogamer  ReviewSource = "EUROGAMER"
)

// AchievementCategory groups achievements by origin
// (placeholder codes).
type AchievementCategory int

const (
	AchievementCategoryCore      AchievementCategory = 0
	AchievementCategoryDLC       AchievementCategory = 1
	AchievementCategoryExpansion AchievementCategory = 2
	AchievementCategoryUnknown   AchievementCategory = 99
)

// MediaAssetType distinguishes asset purposes including
// achievement icons.
type MediaAssetType string

const (
	MediaAssetCover               MediaAssetType = "COVER"
	MediaAssetScreenshot          MediaAssetType = "SCREENSHOT"
	MediaAssetArtwork             MediaAssetType = "ARTWORK"
	MediaAssetCompanyLogo         MediaAssetType = "COMPANY_LOGO"
	MediaAssetEngineLogo          MediaAssetType = "ENGINE_LOGO"
	MediaAssetPlatformLogo        MediaAssetType = "PLATFORM_LOGO"
	MediaAssetAchievementLocked   MediaAssetType = "ACHIEVEMENT_ICON_LOCKED"
	MediaAssetAchievementUnlocked MediaAssetType = "ACHIEVEMENT_ICON_UNLOCKED"
)
