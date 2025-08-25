package db

import (
	"fmt"
	"os"
	"time"

	"gorm.io/driver/postgres"
	"gorm.io/gorm"
	"gorm.io/gorm/logger"
	"gorm.io/gorm/schema"
)

// Config holds database configuration loaded from
// environment.
type Config struct {
	Host     string
	Port     string
	User     string
	Password string
	Name     string
	SSLMode  string
	Timezone string
}

// LoadConfig builds Config from environment with sensible
// defaults.
func LoadConfig() Config {
	return Config{
		Host:     Getenv("DB_HOST", "localhost"),
		Port:     Getenv("DB_PORT", "5432"),
		User:     Getenv("DB_USER", "user"),
		Password: Getenv("DB_PASSWORD", "pass"),
		Name:     Getenv("DB_NAME", "game_insight"),
		SSLMode:  Getenv("DB_SSLMODE", "disable"),
		Timezone: Getenv("DB_TIMEZONE", "UTC"),
	}
}

func Getenv(k, def string) string {
	v := os.Getenv(k)
	if v == "" {
		return def
	}
	return v
}

// Open opens a *gorm.DB with tuned settings (no
// pluralization, structured logger).
func Open(cfg Config) (*gorm.DB, error) {
	dsn := fmt.Sprintf(
		"host=%s port=%s user=%s password=%s dbname=%s sslmode=%s TimeZone=%s",
		cfg.Host,
		cfg.Port,
		cfg.User,
		cfg.Password,
		cfg.Name,
		cfg.SSLMode,
		cfg.Timezone,
	)
	gormLogger := logger.New(
		logWriter{},
		logger.Config{
			SlowThreshold:             500 * time.Millisecond,
			LogLevel:                  parseLogLevel(os.Getenv("GORM_LOG_LEVEL")),
			IgnoreRecordNotFoundError: true,
			Colorful:                  false,
		},
	)
	return gorm.Open(postgres.Open(dsn), &gorm.Config{
		SkipDefaultTransaction: true,
		PrepareStmt:            true,
		Logger:                 gormLogger,
		NamingStrategy: schema.NamingStrategy{
			SingularTable: true, // table names already singular
		},
	})
}

// logWriter implements logger.Writer using standard output.
type logWriter struct{}

func (logWriter) Printf(format string, args ...any) {
	fmt.Printf(format+"\n", args...)
}

func parseLogLevel(lvl string) logger.LogLevel {
	switch lvl {
	case "silent":
		return logger.Silent
	case "error":
		return logger.Error
	case "warn":
		return logger.Warn
	case "info":
		return logger.Info
	default:
		return logger.Warn
	}
}
