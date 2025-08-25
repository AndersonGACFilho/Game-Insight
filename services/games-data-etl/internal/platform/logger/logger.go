package logger

import (
	"encoding/json"
	"io"
	"net/http"
	"os"
	"strconv"
	"strings"
	"sync"
	"time"

	"github.com/rs/zerolog"
)

// Config holds logging configuration.
type Config struct {
	Format      string     // console or json
	Level       string     // trace, debug, info, warn, error
	Loki        LokiConfig // optional Loki push
	ServiceName string
	Environment string
}

type LokiConfig struct {
	Enabled       bool
	URL           string
	Labels        map[string]string
	BatchSize     int
	BatchInterval time.Duration
	Client        *http.Client
}

// New returns a default console logger (backwards
// compatibility).
func New() zerolog.Logger {
	return NewFromConfig(loadEnvConfig())
}

// NewFromConfig builds a zerolog.Logger from provided
// config.
func NewFromConfig(cfg Config) zerolog.Logger {
	level := parseLevel(cfg.Level)
	var out io.Writer
	if strings.ToLower(cfg.Format) == "console" {
		out = zerolog.ConsoleWriter{Out: os.Stderr, TimeFormat: time.RFC3339}
	} else {
		out = os.Stdout
	}
	logger := zerolog.New(out).Level(level).With().Timestamp().Str("service", cfg.ServiceName).Str("env", cfg.Environment).Logger()
	if cfg.Loki.Enabled && cfg.Loki.URL != "" {
		lw := newLokiWriter(cfg.Loki)
		multi := io.MultiWriter(out, lw)
		logger = zerolog.New(multi).Level(level).With().Timestamp().Str("service", cfg.ServiceName).Str("env", cfg.Environment).Logger()
	}
	return logger
}

func loadEnvConfig() Config {
	format := getenv("LOG_FORMAT", "console") // changed default from json to console for readability
	lvl := getenv("LOG_LEVEL", "debug")
	service := getenv("SERVICE_NAME", "games-data-etl")
	env := getenv("ENVIRONMENT", "dev")
	lc := LokiConfig{}
	if u := os.Getenv("LOKI_URL"); u != "" {
		lc.Enabled = true
		lc.URL = u
		lc.BatchSize = atoiDefault(os.Getenv("LOKI_BATCH_SIZE"), 100)
		lc.BatchInterval = durationDefault(os.Getenv("LOKI_BATCH_INTERVAL"), 5*time.Second)
		lc.Labels = parseLabels(getenv("LOKI_LABELS", "service="+service+",env="+env))
		lc.Client = &http.Client{Timeout: 10 * time.Second}
	}
	return Config{Format: format, Level: lvl, Loki: lc,
		ServiceName: service, Environment: env}
}

func parseLevel(l string) zerolog.Level {
	switch strings.ToLower(l) {
	case "trace":
		return zerolog.TraceLevel
	case "debug":
		return zerolog.DebugLevel
	case "info":
		return zerolog.InfoLevel
	case "warn", "warning":
		return zerolog.WarnLevel
	case "error":
		return zerolog.ErrorLevel
	default:
		return zerolog.InfoLevel
	}
}

// lokiWriter implements io.Writer pushing logs to Loki in
// batches.
type lokiWriter struct {
	cfg     LokiConfig
	mu      sync.Mutex
	buffer  []lokiEntry
	closing chan struct{}
}

type lokiEntry struct {
	ts   time.Time
	line string
}

func newLokiWriter(cfg LokiConfig) *lokiWriter {
	lw := &lokiWriter{cfg: cfg, buffer: make([]lokiEntry, 0,
		cfg.BatchSize), closing: make(chan struct{})}
	go lw.loop()
	return lw
}

func (l *lokiWriter) Write(p []byte) (int, error) {
	line := strings.TrimRight(string(p), "\n")
	l.mu.Lock()
	l.buffer = append(l.buffer, lokiEntry{ts: time.Now(), line: line})
	flushNow := len(l.buffer) >= l.cfg.BatchSize
	var flushEntries []lokiEntry
	if flushNow {
		flushEntries = make([]lokiEntry, len(l.buffer))
		copy(flushEntries, l.buffer)
		l.buffer = l.buffer[:0]
	}
	l.mu.Unlock()
	if flushNow {
		l.flush(flushEntries)
	}
	return len(p), nil
}

func (l *lokiWriter) loop() {
	ticker := time.NewTicker(l.cfg.BatchInterval)
	defer ticker.Stop()
	for {
		select {
		case <-ticker.C:
			l.mu.Lock()
			if len(l.buffer) == 0 {
				l.mu.Unlock()
				continue
			}
			bufCopy := make([]lokiEntry, len(l.buffer))
			copy(bufCopy, l.buffer)
			l.buffer = l.buffer[:0]
			l.mu.Unlock()
			l.flush(bufCopy)
		case <-l.closing:
			return
		}
	}
}

func (l *lokiWriter) flush(entries []lokiEntry) {
	if len(entries) == 0 {
		return
	}
	streams := []map[string]any{}
	vals := make([][2]string, 0, len(entries))
	for _, e := range entries {
		vals = append(vals,
			[2]string{strconv.FormatInt(e.ts.UnixNano(), 10), e.line})
	}
	labelSet := map[string]string{}
	for k, v := range l.cfg.Labels {
		labelSet[k] = v
	}
	streams = append(streams, map[string]any{"stream": labelSet, "values": vals})
	body := map[string]any{"streams": streams}
	b, _ := json.Marshal(body)
	req, err := http.NewRequest(
		"POST",
		l.cfg.URL+"/loki/api/v1/push",
		strings.NewReader(string(b)),
	)
	if err != nil {
		return
	}
	req.Header.Set("Content-Type", "application/json")
	resp, err := l.cfg.Client.Do(req)
	if err == nil {
		_ = resp.Body.Close()
	}
}

func getenv(k, def string) string {
	if v := os.Getenv(k); v != "" {
		return v
	}
	return def
}
func atoiDefault(s string, d int) int {
	if s == "" {
		return d
	}
	if v, err := strconv.Atoi(s); err == nil {
		return v
	}
	return d
}
func durationDefault(
	s string,
	d time.Duration,
) time.Duration {
	if s == "" {
		return d
	}
	if v, err := time.ParseDuration(s); err == nil {
		return v
	}
	return d
}
func parseLabels(s string) map[string]string {
	m := map[string]string{}
	parts := strings.Split(s, ",")
	for _, p := range parts {
		p = strings.TrimSpace(p)
		if p == "" || !strings.Contains(p, "=") {
			continue
		}
		kv := strings.SplitN(p, "=", 2)
		m[strings.TrimSpace(kv[0])] = strings.TrimSpace(kv[1])
	}
	return m
}
