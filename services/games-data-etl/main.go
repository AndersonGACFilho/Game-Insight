package main

import (
	"game-data-etl/internal/api/igdb"
	"game-data-etl/internal/db"
	"game-data-etl/internal/etl"
	"game-data-etl/internal/platform/logger"
	"game-data-etl/internal/platform/repositories"
	"github.com/prometheus/client_golang/prometheus/collectors"
	"net/http"
	"os"
	"strconv"
	"time"

	"github.com/prometheus/client_golang/prometheus"
	"github.com/prometheus/client_golang/prometheus/promhttp"
)

func main() {
	log := logger.New()
	log.Info().Msg("Starting Games ETL Service...")

	// Configuration
	cfg := db.LoadConfig()
	igdbSecret := db.Getenv("IGDB_SECRET", "bfuou8knx9hzqrx57x8kyr832ryz62")
	clientID := db.Getenv("IGDB_CLIENT_ID", "dqo79q2m2xekhsb38anwwa9fodctel")
	metricsPort := db.Getenv("METRICS_PORT", "9108")

	// Database
	dbConn, err := db.Open(cfg)
	if err != nil {
		log.Fatal().Err(err).Msg("Failed opening database")
	}
	log.Info().Str("db_name", dbConn.Name()).Msg("Database connection established")

	// Repositories
	gameRepository := repositories.NewGameRepository(dbConn)
	dimensionRepo :=
		repositories.NewDimensionRepository(dbConn)

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

	// Prometheus metrics registry and instruments
	reg := prometheus.NewRegistry()
	etlMetrics := etl.NewMetrics(reg)
	// Expose build info / Go collector
	reg.MustRegister(collectors.NewGoCollector())
	reg.MustRegister(collectors.NewProcessCollector(collectors.ProcessCollectorOpts{}))

	// Metrics HTTP server (non-blocking)
	mux := http.NewServeMux()
	mux.Handle("/metrics", promhttp.HandlerFor(reg, promhttp.HandlerOpts{}))
	go func() {
		addr := ":" + metricsPort
		log.Info().Str("addr", addr).Msg("Metrics server listening")
		if err := http.ListenAndServe(addr, mux); err != nil {
			log.Error().Err(err).Msg("Metrics server error")
		}
	}()

	// SRP Components
	extractor := etl.NewIGDBExtractor(
		igdbClient,
		log,
		"rating > 60 & aggregated_rating_count > 10",
	)
	transformer := etl.NewGameTransformer(log)
	enricher := etl.NewCompositeEnricher(dimensionRepo,
		igdbClient, log)
	loader := etl.NewGameLoader(gameRepository)

	batchLimit := 500
	if v := os.Getenv("BATCH_LIMIT"); v != "" {
		// naive parse
		if n, _ := strconv.Atoi(v); n > 0 {
			batchLimit = n
		}
	}

	// Pipeline
	pipeline := etl.NewPipeline(log, extractor, transformer,
		enricher, loader, batchLimit, etlMetrics)
	start := time.Now()
	pipeline.Run()
	log.Info().Dur("total_runtime", time.Since(start)).Msg("Pipeline run completed")
}
