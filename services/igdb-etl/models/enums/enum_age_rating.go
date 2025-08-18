package enums

// AgeRatingOrg represents IGDB age_ratings.category classification organizations.
type AgeRatingOrg int

const (
	AgeRatingOrgESRB     AgeRatingOrg = 1
	AgeRatingOrgPEGI     AgeRatingOrg = 2
	AgeRatingOrgCERO     AgeRatingOrg = 3
	AgeRatingOrgUSK      AgeRatingOrg = 4
	AgeRatingOrgGRAC     AgeRatingOrg = 5
	AgeRatingOrgClassInd AgeRatingOrg = 6
	AgeRatingOrgACB      AgeRatingOrg = 7
	AgeRatingOrgOFLCAU   AgeRatingOrg = 8
	AgeRatingOrgRussia   AgeRatingOrg = 9
	AgeRatingOrgMDASG    AgeRatingOrg = 10
	AgeRatingOrgIARC     AgeRatingOrg = 11
	AgeRatingOrgUnknown  AgeRatingOrg = -1
)
