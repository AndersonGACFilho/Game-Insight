package main

import (
	"fmt"
	"igdbetl/internal/db"
	game_model "igdbetl/models/entities"
	"log"
)

func main() {
	fmt.Printf("Starting IGDB ETL Service...\n")

	cfg := db.LoadConfig()
	dbConn, err := db.Open(cfg)
	if err != nil {
		log.Fatalf("failed opening database: %v", err)
	}
	// Simple connectivity check
	sqlDB, err := dbConn.DB()
	if err != nil {
		log.Fatalf("unwrap sql db: %v", err)
	}
	if err := sqlDB.Ping(); err != nil {
		log.Fatalf("db ping failed: %v", err)
	}
	fmt.Println("Database connection established")

	game := new(game_model.Game)

	game.Slug = "example-game"
	game.Title = "Example Game Title"
	game.Active = true
	game.Summary = new(string)
	*game.Summary = "This is an example summary of the game."
	game.Category = new(game_model.GameCategory)
	game.TotalRating = new(float64)
	*game.TotalRating = 85.5
	game.IngestionTimestamp = dbConn.NowFunc()

	// Print the initialized game model

	fmt.Printf("Game model initialized: %+v\n", game)
}
