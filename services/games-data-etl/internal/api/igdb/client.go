package igdb

// Package igdb provides a thin, responsibility-focused HTTP
// client for the IGDB API.
// Design notes (SOLID):
// * SRP: Client only knows how to authenticate and issue
// queries; no transformation logic.
// * OCP: New endpoints added via small helper methods
// (e.g., GetEntitiesByIDs) without modifying core flow.
// * DIP: Higher layers (extractor) depend on this
// abstraction rather than raw http.
// The client intentionally exposes only the primitives the
// ETL layer needs while keeping
// request building explicit for observability and future
// rate limiting.

import (
	"encoding/json"
	"fmt"
	"io"
	"net/http"
	"strings"
	"time"

	"github.com/rs/zerolog"
)

const (
	defaultTimeout  = 10 * time.Second
	contentTypeJSON = "application/x-www-form-urlencoded"
	contentTypeText = "text/plain"
)

// Client implements a minimal IGDB API client with token
// acquisition and POST-based query helpers.
// It is concurrency-safe for read operations after token
// acquisition (mutable fields guarded by simple sequencing
// of calls in current usage).
type Client struct {
	httpClient  *http.Client
	loginURL    string
	baseURL     string
	clientID    string
	apiKey      string
	token       TokenResponse
	lastRequest time.Time
	logger      zerolog.Logger
}

// TokenResponse represents the OAuth-style token payload
// returned by IGDB.
type TokenResponse struct {
	AccessToken string `json:"access_token"`
	ExpiresIn   int    `json:"expires_in"`
	TokenType   string `json:"token_type"`
}

// GameQueryParams captures high-level filters supported by
// GetGames.
// Only the subset used internally is modeled; arbitrary
// WHERE clauses can be passed via Where.
type GameQueryParams struct {
	StartDate time.Time
	EndDate   time.Time
	Limit     int
	Offset    int
	Where     string
}

// NewClient constructs a new IGDB client. The caller must
// invoke GetToken() before data calls.
func NewClient(loginURL, baseURL, apiKey, clientID string,
	logger zerolog.Logger) *Client {
	return &Client{
		httpClient: &http.Client{Timeout: defaultTimeout},
		loginURL:   loginURL,
		baseURL:    baseURL,
		clientID:   clientID,
		apiKey:     apiKey,
		token:      TokenResponse{},
		logger:     logger,
	}
}

// GetGames fetches game data using a composed IGDB query
// string based on provided parameters.
// Responsibilities: build (fields/pagination/where/sort) ->
// POST -> basic error handling -> raw bytes.
func (c *Client) GetGames(params GameQueryParams) ([]byte,
	error) {
	c.logger.Debug().Msg("Starting GetGames request")

	if err := c.validateToken(); err != nil {
		return nil, err
	}

	if err := c.validateDateRange(params.StartDate,
		params.EndDate); err != nil {
		return nil, err
	}

	requestBody := c.buildGameQuery(params)
	return c.executeGameRequest(requestBody)
}

// GetToken obtains and stores an access token. It must be
// called before other data endpoints.
func (c *Client) GetToken() (string, error) {
	c.logger.Debug().Msg("Starting token request")

	endpoint := c.buildTokenEndpoint()
	req, err := c.createTokenRequest(endpoint)
	if err != nil {
		return "", err
	}

	resp, err := c.httpClient.Do(req)
	if err != nil {
		c.logger.Error().Err(err).Msg("Failed to execute token request")
		return "", fmt.Errorf("failed to execute request: %w", err)
	}
	defer func(Body io.ReadCloser) {
		err := Body.Close()
		if err != nil {
			c.logger.Error().Err(err).Msg("Failed to close token response body")
		}
	}(resp.Body)

	return c.processTokenResponse(resp)
}

// String implements fmt.Stringer for debug purposes
// (sanitizes token).
func (c *Client) String() string {
	return fmt.Sprintf("IGDB Client: %s (Client ID: %s)", c.baseURL, c.clientID)
}

// validateToken ensures a token was set prior to data
// calls.
func (c *Client) validateToken() error {
	if c.token.AccessToken == "" {
		c.logger.Error().Msg("No access token available")
		return fmt.Errorf("no access token available, call GetToken() first")
	}
	return nil
}

// validateDateRange guards against inverted date ranges;
// zero values are accepted.
func (c *Client) validateDateRange(
	startDate time.Time,
	endDate time.Time,
) error {
	if !startDate.IsZero() && !endDate.IsZero() &&
		startDate.After(endDate) {
		c.logger.Error().Msg("Start date cannot be after end date")
		return fmt.Errorf("start date cannot be after end date")
	}
	return nil
}

// buildGameQuery composes required IGDB query fields and
// clauses for the games endpoint.
func (c *Client) buildGameQuery(
	params GameQueryParams,
) string {
	fields := "fields id,name,summary,storyline,category,status,first_release_date," +
		"total_rating,total_rating_count,aggregated_rating,aggregated_rating_count," +
		"keywords,genres,themes,game_modes,player_perspectives,collections," +
		"franchises,parent_game,platforms,involved_companies,alternative_names" +
		",release_dates,screenshots,artworks,cover,videos,websites,multiplayer_modes," +
		"language_supports,age_ratings,created_at,updated_at;"

	pagination := fmt.Sprintf("limit %d; offset %d; ", params.Limit, params.Offset)
	sortClause := "sort total_rating desc; "

	whereClause := c.buildWhereClause(params)

	return fields + pagination + whereClause + sortClause
}

// buildWhereClause constructs a conditional clause based on
// provided filters.
func (c *Client) buildWhereClause(
	params GameQueryParams,
) string {
	var conditions []string

	// Add custom where condition
	if params.Where != "" {
		conditions = append(conditions, params.Where)
	}

	// Add date range conditions
	if !params.StartDate.IsZero() && !params.EndDate.IsZero() {
		conditions = append(conditions,
			fmt.Sprintf("first_release_date >= %d & first_release_date <= %d",
				params.StartDate.Unix(), params.EndDate.Unix()))
	} else if !params.StartDate.IsZero() {
		conditions = append(conditions,
			fmt.Sprintf("first_release_date >= %d", params.StartDate.Unix()))
	} else if !params.EndDate.IsZero() {
		conditions = append(conditions,
			fmt.Sprintf("first_release_date <= %d", params.EndDate.Unix()))
	}

	if len(conditions) == 0 {
		return ""
	}

	return fmt.Sprintf("where %s; ", strings.Join(conditions, " & "))
}

// executeGameRequest performs the POST for games endpoint
// and handles response boilerplate.
func (c *Client) executeGameRequest(
	requestBody string,
) ([]byte, error) {
	endpoint := c.baseURL + "games"
	body := strings.NewReader(requestBody)

	req, err := http.NewRequest("POST", endpoint, body)
	if err != nil {
		c.logger.Error().Err(err).Msg("Failed to create games request")
		return nil, fmt.Errorf("failed to create request: %w", err)
	}

	c.setGameRequestHeaders(req)
	c.logGameRequest(endpoint)

	resp, err := c.httpClient.Do(req)
	if err != nil {
		c.logger.Error().Err(err).Msg("Failed to execute games request")
		return nil, fmt.Errorf("failed to execute request: %w", err)
	}
	defer func(Body io.ReadCloser) {
		err := Body.Close()
		if err != nil {
			c.logger.Error().Err(err).Msg("Failed to close games response body")
		}
	}(resp.Body)

	return c.processGameResponse(resp)
}

// setGameRequestHeaders applies required auth & content
// headers to a games request.
func (c *Client) setGameRequestHeaders(req *http.Request) {
	req.Header.Set("Client-ID", c.clientID)
	req.Header.Set("Authorization", "Bearer "+c.token.AccessToken)
	req.Header.Set("Content-Type", contentTypeText)
}

// logGameRequest emits a debug entry; token truncated for
// safety.
func (c *Client) logGameRequest(endpoint string) {
	c.logger.Debug().
		Str("endpoint", endpoint).
		Str("token", c.token.AccessToken[:10]+"...").
		Msg("Making games request")
}

// processGameResponse validates status and returns raw
// body.
func (c *Client) processGameResponse(
	resp *http.Response,
) ([]byte, error) {
	responseBody, err := io.ReadAll(resp.Body)
	if err != nil {
		c.logger.Error().Err(err).Msg("Failed to read response body")
		return nil, fmt.Errorf("failed to read response body: %w", err)
	}

	if resp.StatusCode != http.StatusOK {
		c.logger.Error().
			Int("status_code", resp.StatusCode).
			Str("response_body", string(responseBody)).
			Msg("Games request failed")
		return nil, fmt.Errorf("request failed with status %d: %s", resp.StatusCode, string(responseBody))
	}

	c.logger.Info().
		Int("response_size", len(responseBody)).
		Msg("Games request successful")

	return responseBody, nil
}

// buildTokenEndpoint creates the OAuth token URL with
// credentials.
func (c *Client) buildTokenEndpoint() string {
	return fmt.Sprintf("%stoken?client_id=%s&client_secret=%s&grant_type=client_credentials",
		c.loginURL, c.clientID, c.apiKey)
}

// createTokenRequest builds the HTTP request for token
// retrieval.
func (c *Client) createTokenRequest(
	endpoint string,
) (*http.Request, error) {
	req, err := http.NewRequest("POST", endpoint, nil)
	if err != nil {
		c.logger.Error().Err(err).Msg("Failed to create token request")
		return nil, fmt.Errorf("failed to create request: %w", err)
	}

	req.Header.Set("Content-Type", contentTypeJSON)
	c.logger.Debug().Str("endpoint", endpoint).Msg("Requesting IGDB token")

	return req, nil
}

// processTokenResponse reads, validates and stores a token
// payload.
func (c *Client) processTokenResponse(
	resp *http.Response,
) (string, error) {
	body, err := io.ReadAll(resp.Body)
	if err != nil {
		c.logger.Error().Err(err).Msg("Failed to read token response body")
		return "", fmt.Errorf("failed to read response body: %w", err)
	}

	if resp.StatusCode != http.StatusOK {
		c.logger.Error().
			Int("status_code", resp.StatusCode).
			Str("response_body", string(body)).
			Msg("Token request failed")
		return "", fmt.Errorf("token request failed with status %d: %s", resp.StatusCode, string(body))
	}

	var tokenResp TokenResponse
	if err := json.Unmarshal(body, &tokenResp); err != nil {
		c.logger.Error().
			Err(err).
			Str("response_body", string(body)).
			Msg("Failed to parse token response")
		return "", fmt.Errorf("failed to parse token response: %w", err)
	}

	c.token = tokenResp
	c.lastRequest = time.Now()

	c.logger.Info().
		Str("token_type", tokenResp.TokenType).
		Int("expires_in", tokenResp.ExpiresIn).
		Msg("Token retrieved successfully")

	return tokenResp.AccessToken, nil
}

// GetNamedEntities fetches arbitrary named dimension
// entities (genres, themes, keywords, etc.) by their
// numeric IGDB IDs.
// endpoint examples: "genres", "themes", "keywords",
// "game_modes", "player_perspectives", "collections",
// "franchises".
func (c *Client) GetNamedEntities(
	endpoint string,
	ids []int32,
) ([]byte, error) {
	if err := c.validateToken(); err != nil {
		return nil, err
	}
	if len(ids) == 0 {
		return []byte("[]"), nil
	}
	parts := make([]string, 0, len(ids))
	for _, id := range ids {
		parts = append(parts, fmt.Sprintf("%d", id))
	}
	where := fmt.Sprintf("where id = (%s);", strings.Join(parts, ","))
	// first attempt with timestamps
	query := "fields id,name,slug,created_at,updated_at; " + where + fmt.Sprintf(" limit %d;", len(ids))
	data, err := c.executeGenericPOST(endpoint, query)
	if err != nil && strings.Contains(err.Error(), "Invalid Field") {
		// retry with minimal fields
		c.logger.Debug().Str("endpoint", endpoint).Msg("Retrying named entity fetch without created_at/updated_at")
		query2 := "fields id,name,slug; " + where + fmt.Sprintf(" limit %d;", len(ids))
		return c.executeGenericPOST(endpoint, query2)
	}
	return data, err
}

// GetEntitiesByIDs fetches arbitrary endpoint entities with
// selected fields for given ids.
func (c *Client) GetEntitiesByIDs(
	endpoint string,
	ids []int32,
	fields string,
) ([]byte, error) {
	if err := c.validateToken(); err != nil {
		return nil, err
	}
	if len(ids) == 0 {
		return []byte("[]"), nil
	}
	parts := make([]string, 0, len(ids))
	for _, id := range ids {
		parts = append(parts, fmt.Sprintf("%d", id))
	}
	where := fmt.Sprintf("where id = (%s);", strings.Join(parts, ","))
	query := "fields " + fields + "; " + where + fmt.Sprintf(" limit %d;", len(ids))
	return c.executeGenericPOST(endpoint, query)
}

// executeGenericPOST executes a POST against an IGDB
// endpoint with a plain-text body.
func (c *Client) executeGenericPOST(
	endpoint,
	bodyStr string,
) ([]byte, error) {
	url := c.baseURL + endpoint
	req, err := http.NewRequest(
		"POST",
		url,
		strings.NewReader(bodyStr),
	)
	if err != nil {
		return nil, fmt.Errorf(
			"failed creating request: %w",
			err,
		)
	}
	c.setGameRequestHeaders(req) // same headers
	resp, err := c.httpClient.Do(req)
	if err != nil {
		return nil, fmt.Errorf("request failed: %w", err)
	}
	defer func(Body io.ReadCloser) {
		err := Body.Close()
		if err != nil {
			c.logger.Error().Err(err).Msg(
				"Failed to close generic POST response body",
			)
		}
	}(resp.Body)

	data, err := io.ReadAll(resp.Body)
	if err != nil {
		return nil, fmt.Errorf("read body: %w", err)
	}
	if resp.StatusCode != http.StatusOK {
		return nil, fmt.Errorf(
			"endpoint %s failed (%d): %s",
			endpoint,
			resp.StatusCode,
			string(data),
		)
	}
	return data, nil
}

// GetEntitiesByWhere fetches entities via an arbitrary
// where clause (used for achievements by game).
func (c *Client) GetEntitiesByWhere(
	endpoint string,
	where string,
	fields string,
	limit int,
) ([]byte, error) {
	if err := c.validateToken(); err != nil {
		return nil, err
	}
	query := fmt.Sprintf("fields %s; where %s; limit %d;", fields, where, limit)
	return c.executeGenericPOST(endpoint, query)
}
