package enums

// WebsiteCategory represents IGDB websites.category codes
// (subset currently used).
type WebsiteCategory int

const (
	WebsiteOfficial  WebsiteCategory = 1
	WebsiteWikia     WebsiteCategory = 2
	WebsiteWikipedia WebsiteCategory = 3
	WebsiteFacebook  WebsiteCategory = 4
	WebsiteTwitter   WebsiteCategory = 5
	WebsiteTwitch    WebsiteCategory = 6
	WebsiteInstagram WebsiteCategory = 8
	WebsiteYouTube   WebsiteCategory = 9
	WebsiteIPhone    WebsiteCategory = 10
	WebsiteIPad      WebsiteCategory = 11
	WebsiteAndroid   WebsiteCategory = 12
	WebsiteSteam     WebsiteCategory = 13
	WebsiteReddit    WebsiteCategory = 14
	WebsiteItch      WebsiteCategory = 15
	WebsiteEpic      WebsiteCategory = 16
	WebsiteGOG       WebsiteCategory = 17
	WebsiteDiscord   WebsiteCategory = 18
	WebsiteUnknown   WebsiteCategory = -1
)
