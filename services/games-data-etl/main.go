package main

import (
	"game-data-etl/internal/api/igdb"
	"game-data-etl/internal/db"
	"game-data-etl/internal/etl"
	"game-data-etl/internal/platform/logger"
	"game-data-etl/internal/platform/repositories"
)

func main() {
	log := logger.New()
	log.Info().Msg("Starting Games ETL Service...")

	// Configuration
	cfg := db.LoadConfig()
	// You'll want to add an IGDB API key and other configs here
	igdbSecret := db.Getenv("IGDB_SECRET", "bfuou8knx9hzqrx57x8kyr832ryz62")
	clientID := db.Getenv("IGDB_CLIENT_ID", "dqo79q2m2xekhsb38anwwa9fodctel")

	// Database
	dbConn, err := db.Open(cfg)
	if err != nil {
		log.Fatal().Err(err).Msg("Failed opening database")
	}
	log.Info().Str("db_name", dbConn.Name()).Msg("Database connection established")

	// Repositories
	gameRepository := repositories.NewGameRepository(dbConn)
	dimensionRepo := repositories.NewDimensionRepository(dbConn)

	// API Client
	igdbClient := igdb.NewClient(
		"https://id.twitch.tv/oauth2/",
		"https://api.igdb.com/v4/",
		igdbSecret,
		clientID,
		log,
	)
	log.Info().Str("igdb_base_url", igdbClient.String()).Msg("IGDB API client initialized")

	// Acquire token (required before extraction)
	if _, err := igdbClient.GetToken(); err != nil {
		log.Fatal().Err(err).Msg("Failed to get IGDB token")
	}
	log.Info().Msg("IGDB token retrieved successfully")

	// SRP Components
	extractor := etl.NewIGDBExtractor(igdbClient, log, "")
	transformer := etl.NewGameTransformer(log)
	enricher := etl.NewDimensionEnricher(dimensionRepo)
	loader := etl.NewGameLoader(gameRepository)

	// Pipeline
	pipeline := etl.NewPipeline(log, extractor, transformer, enricher, loader, 500)
	pipeline.Run()
}
