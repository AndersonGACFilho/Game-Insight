package igdb_models

import (
	"encoding/json"
	"strconv"
	"time"
)

// UnixTime handles IGDB's Unix timestamp format
type UnixTime struct {
	time.Time
}

// UnmarshalJSON converts Unix epoch integer to time.Time
func (ut *UnixTime) UnmarshalJSON(data []byte) error {
	// Try to parse as integer first
	if timestamp, err := strconv.ParseInt(string(data), 10,
		64); err == nil {
		ut.Time = time.Unix(timestamp, 0).UTC()
		return nil
	}

	// Fallback to standard time parsing if it's a string
	return ut.Time.UnmarshalJSON(data)
}

// MarshalJSON converts time.Time back to Unix epoch
func (ut UnixTime) MarshalJSON() ([]byte, error) {
	return json.Marshal(ut.Unix())
}
