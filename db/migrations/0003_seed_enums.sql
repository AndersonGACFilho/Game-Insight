-- 0003_seed_enums.sql
-- Seed initial enumeration values based on IGDB / internal mappings.
-- Idempotent: uses ON CONFLICT DO NOTHING.
BEGIN;

-- GameCategory (games.category)
INSERT INTO enumeration_lookup(enum_name, code, symbol, description) VALUES
 ('GameCategory',0,'MAIN_GAME','Main Game'),
 ('GameCategory',1,'DLC_ADDON','DLC / Add-on'),
 ('GameCategory',2,'EXPANSION','Expansion'),
 ('GameCategory',3,'BUNDLE','Bundle'),
 ('GameCategory',4,'STANDALONE_EXPANSION','Standalone Expansion'),
 ('GameCategory',5,'MOD','Modification'),
 ('GameCategory',8,'REMAKE','Remake'),
 ('GameCategory',9,'REMASTER','Remaster'),
 ('GameCategory',10,'EXPANDED_GAME','Expanded Game'),
 ('GameCategory',11,'PORT','Port'),
 ('GameCategory',12,'FORK','Fork'),
 ('GameCategory',13,'PACK','Pack'),
 ('GameCategory',14,'UPDATE','Update'),
 ('GameCategory',15,'EPISODE','Episode')
ON CONFLICT DO NOTHING;

-- ReleaseStatus (games.status)
INSERT INTO enumeration_lookup(enum_name, code, symbol, description) VALUES
 ('ReleaseStatus',0,'RELEASED','Released'),
 ('ReleaseStatus',2,'ALPHA','Alpha'),
 ('ReleaseStatus',3,'BETA','Beta'),
 ('ReleaseStatus',4,'EARLY_ACCESS','Early Access'),
 ('ReleaseStatus',5,'OFFLINE','Offline / Shutdown'),
 ('ReleaseStatus',6,'CANCELLED','Cancelled'),
 ('ReleaseStatus',7,'RUMORED','Rumored'),
 ('ReleaseStatus',8,'DELISTED','Delisted')
ON CONFLICT DO NOTHING;

-- PlatformCategory (platforms.category)
INSERT INTO enumeration_lookup(enum_name, code, symbol, description) VALUES
 ('PlatformCategory',1,'CONSOLE','Console'),
 ('PlatformCategory',2,'ARCADE','Arcade'),
 ('PlatformCategory',3,'PLATFORM','Generic Platform'),
 ('PlatformCategory',4,'OPERATING_SYSTEM','Operating System'),
 ('PlatformCategory',5,'PORTABLE_CONSOLE','Portable Console'),
 ('PlatformCategory',6,'COMPUTER','Computer'),
 ('PlatformCategory',7,'DEVICE','Device'),
 ('PlatformCategory',8,'VIRTUAL_REALITY','Virtual Reality'),
 ('PlatformCategory',9,'EMULATOR','Emulator'),
 ('PlatformCategory',10,'BROWSER','Browser'),
 ('PlatformCategory',11,'INTERNET_OF_THINGS','IoT'),
 ('PlatformCategory',12,'STREAMING','Streaming Platform'),
 ('PlatformCategory',13,'WALKTHROUGH','Walkthrough (Deprecated)')
ON CONFLICT DO NOTHING;

-- Region (release_dates.region)
INSERT INTO enumeration_lookup(enum_name, code, symbol, description) VALUES
 ('Region',1,'EUROPE','Europe'),
 ('Region',2,'NORTH_AMERICA','North America'),
 ('Region',3,'AUSTRALIA','Australia'),
 ('Region',4,'NEW_ZEALAND','New Zealand'),
 ('Region',5,'JAPAN','Japan'),
 ('Region',6,'CHINA','China'),
 ('Region',7,'ASIA','Rest of Asia'),
 ('Region',8,'WORLDWIDE','Worldwide'),
 ('Region',9,'KOREA','Korea'),
 ('Region',10,'BRAZIL','Brazil'),
 ('Region',11,'OTHER','Other')
ON CONFLICT DO NOTHING;

-- AgeRatingOrg (age_ratings.category)
INSERT INTO enumeration_lookup(enum_name, code, symbol, description) VALUES
 ('AgeRatingOrg',1,'ESRB','ESRB'),
 ('AgeRatingOrg',2,'PEGI','PEGI'),
 ('AgeRatingOrg',3,'CERO','CERO'),
 ('AgeRatingOrg',4,'USK','USK'),
 ('AgeRatingOrg',5,'GRAC','GRAC'),
 ('AgeRatingOrg',6,'CLASS_IND','ClassInd Brazil'),
 ('AgeRatingOrg',7,'ACB','Australian Classification Board'),
 ('AgeRatingOrg',8,'OFLC_NZ','OFLC New Zealand'),
 ('AgeRatingOrg',9,'RUSSIA','Russian Age Rating'),
 ('AgeRatingOrg',10,'MDA_SG','Singapore MDA'),
 ('AgeRatingOrg',11,'IARC','IARC')
ON CONFLICT DO NOTHING;

-- AchievementCategory (placeholder)
INSERT INTO enumeration_lookup(enum_name, code, symbol, description) VALUES
 ('AchievementCategory',0,'CORE','Core / Base Game'),
 ('AchievementCategory',1,'DLC','DLC Achievement'),
 ('AchievementCategory',2,'EXPANSION','Expansion Achievement'),
 ('AchievementCategory',99,'UNKNOWN','Unclassified')
ON CONFLICT DO NOTHING;

-- WebsiteCategory (subset example)
INSERT INTO enumeration_lookup(enum_name, code, symbol, description) VALUES
 ('WebsiteCategory',1,'OFFICIAL','Official Site'),
 ('WebsiteCategory',3,'WIKIPEDIA','Wikipedia'),
 ('WebsiteCategory',9,'YOUTUBE','YouTube'),
 ('WebsiteCategory',13,'STEAM','Steam Store'),
 ('WebsiteCategory',16,'EPIC_GAMES','Epic Games Store'),
 ('WebsiteCategory',17,'GOG','GOG Store'),
 ('WebsiteCategory',18,'DISCORD','Discord')
ON CONFLICT DO NOTHING;

-- ExternalRefCategory (external_games.category) subset
INSERT INTO enumeration_lookup(enum_name, code, symbol, description) VALUES
 ('ExternalRefCategory',1,'STEAM','Steam AppID'),
 ('ExternalRefCategory',5,'GOG','GOG ID'),
 ('ExternalRefCategory',10,'EPIC','Epic Games ID'),
 ('ExternalRefCategory',13,'DISCORD','Discord SKU'),
 ('ExternalRefCategory',14,'GEARBOX','Gearbox ID'),
 ('ExternalRefCategory',15,'GOOGLE_PLAY','Google Play Package'),
 ('ExternalRefCategory',20,'HUMBLE','Humble ID')
ON CONFLICT DO NOTHING;

-- LanguageSupportType examples
INSERT INTO enumeration_lookup(enum_name, code, symbol, description) VALUES
 ('LanguageSupportType',1,'AUDIO','Full Audio'),
 ('LanguageSupportType',2,'SUBTITLES','Subtitles'),
 ('LanguageSupportType',3,'INTERFACE','Interface/Text'),
 ('LanguageSupportType',4,'AUDIO_PARTIAL','Partial Audio')
ON CONFLICT DO NOTHING;

COMMIT;

