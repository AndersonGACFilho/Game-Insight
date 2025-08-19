package entities

import (
	enums2 "game-data-etl/internal/domain/enums"
)

// Type aliases bridging enums submodule into entities for ergonomic field usage.
type (
	AgeRatingOrg        = enums2.AgeRatingOrg
	ExternalRefCategory = enums2.ExternalRefCategory
	GameCategory        = enums2.GameCategory
	ReleaseStatus       = enums2.ReleaseStatus
	AchievementCategory = enums2.AchievementCategory
	LanguageSupportType = enums2.LanguageSupportType
	WebsiteCategory     = enums2.WebsiteCategory
	MediaAssetType      = enums2.MediaAssetType
	PlatformCategory    = enums2.PlatformCategory
	Region              = enums2.Region
)
