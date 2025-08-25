package etl

// metrics.go
// SRP: Encapsulates creation and recording of Prometheus
// instruments for ETL steps.
// OCP: New metrics can be added without changing pipeline
// logic; only this struct evolves.

import (
	"github.com/prometheus/client_golang/prometheus"
	"github.com/prometheus/client_golang/prometheus/promauto"
	"time"
)

// Metrics holds Prometheus instruments for the ETL
// pipeline.
// Only additive fields should be introduced to preserve
// backward compatibility with dashboards.
type Metrics struct {
	ExtractedGames   prometheus.Counter
	ProcessedGames   prometheus.Counter
	DeferredGames    prometheus.Counter
	TransformErrors  prometheus.Counter
	EnrichErrors     prometheus.Counter
	LoadErrors       prometheus.Counter
	StepDurationSecs *prometheus.HistogramVec
}

// NewMetrics registers instruments on the provided
// registry.
func NewMetrics(reg prometheus.Registerer) *Metrics {
	m := &Metrics{
		ExtractedGames: promauto.With(reg).NewCounter(
			prometheus.CounterOpts{
				Name: "etl_games_extracted_total",
				Help: "Total number of games extracted " +
					"from source API",
			},
		),
		ProcessedGames: promauto.With(reg).NewCounter(
			prometheus.CounterOpts{
				Name: "etl_games_processed_total",
				Help: "Total number of games successfully " +
					"processed end-to-end",
			},
		),
		DeferredGames: promauto.With(reg).NewCounter(
			prometheus.CounterOpts{
				Name: "etl_games_deferred_total",
				Help: "Total number of games deferred due " +
					"to missing prerequisites (e.g., parent)",
			},
		),
		TransformErrors: promauto.With(reg).NewCounter(
			prometheus.CounterOpts{
				Name: "etl_transform_errors_total",
				Help: "Total number of transformation " +
					"failures",
			},
		),
		EnrichErrors: promauto.With(reg).NewCounter(
			prometheus.CounterOpts{
				Name: "etl_enrich_errors_total",
				Help: "Total number of enrichment (non-" +
					"fatal) errors",
			},
		),
		LoadErrors: promauto.With(reg).NewCounter(
			prometheus.CounterOpts{
				Name: "etl_load_errors_total",
				Help: "Total number of load/persistence " +
					"errors",
			},
		),
		StepDurationSecs: promauto.With(reg).NewHistogramVec(
			prometheus.HistogramOpts{
				Name: "etl_step_duration_seconds",
				Help: "Duration of ETL steps in seconds",
				Buckets: prometheus.
					ExponentialBuckets(0.01, 2, 12),
			},
			[]string{"step"},
		),
	}
	return m
}

// ObserveStep records a duration for a named step. Safe to
// call with nil receiver.
func (m *Metrics) ObserveStep(
	step string,
	start time.Time,
) {
	if m == nil {
		return
	}
	m.StepDurationSecs.WithLabelValues(step).
		Observe(time.Since(start).Seconds())
}
