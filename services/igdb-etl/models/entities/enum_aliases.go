package entities

import "igdbetl/models/enums"

// Type aliases bridging enums submodule into entities for ergonomic field usage.
type (
	AgeRatingOrg        = enums.AgeRatingOrg
	ExternalRefCategory = enums.ExternalRefCategory
	GameCategory        = enums.GameCategory
	ReleaseStatus       = enums.ReleaseStatus
	AchievementCategory = enums.AchievementCategory
	LanguageSupportType = enums.LanguageSupportType
	WebsiteCategory     = enums.WebsiteCategory
	MediaAssetType      = enums.MediaAssetType
	PlatformCategory    = enums.PlatformCategory
	Region              = enums.Region
)
