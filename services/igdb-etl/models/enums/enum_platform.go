package enums

// PlatformCategory represents IGDB platforms.category numeric codes.
type PlatformCategory int

const (
	PlatformCategoryConsole         PlatformCategory = 1
	PlatformCategoryArcade          PlatformCategory = 2
	PlatformCategoryGeneric         PlatformCategory = 3 // Generic platform grouping
	PlatformCategoryOperatingSystem PlatformCategory = 4
	PlatformCategoryPortable        PlatformCategory = 5 // Portable console
	PlatformCategoryComputer        PlatformCategory = 6
	PlatformCategoryDevice          PlatformCategory = 7 // Peripheral / device
	PlatformCategoryVR              PlatformCategory = 8
	PlatformCategoryEmulator        PlatformCategory = 9
	PlatformCategoryBrowser         PlatformCategory = 10
	PlatformCategoryIoT             PlatformCategory = 11
	PlatformCategoryStreaming       PlatformCategory = 12
	PlatformCategoryWalkthrough     PlatformCategory = 13 // Deprecated in docs
	PlatformCategoryUnknown         PlatformCategory = -1
)
