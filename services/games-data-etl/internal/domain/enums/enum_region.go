package enums

// Region represents IGDB release_dates.region codes.
type Region int

const (
	RegionEurope       Region = 1
	RegionNorthAmerica Region = 2
	RegionAustralia    Region = 3
	RegionNewZealand   Region = 4
	RegionJapan        Region = 5
	RegionChina        Region = 6
	RegionAsia         Region = 7
	RegionWorldwide    Region = 8
	RegionKorea        Region = 9
	RegionBrazil       Region = 10
	RegionOther        Region = 11
	RegionUnknown      Region = -1
)
