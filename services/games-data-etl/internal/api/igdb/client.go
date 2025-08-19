package igdb

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

type TokenResponse struct {
	AccessToken string `json:"access_token"`
	ExpiresIn   int    `json:"expires_in"`
	TokenType   string `json:"token_type"`
}

type GameQueryParams struct {
	StartDate time.Time
	EndDate   time.Time
	Limit     int
	Offset    int
	Where     string
}

func NewClient(loginURL, baseURL, apiKey, clientID string, logger zerolog.Logger) *Client {
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

func (c *Client) GetGames(params GameQueryParams) ([]byte, error) {
	c.logger.Debug().Msg("Starting GetGames request")

	if err := c.validateToken(); err != nil {
		return nil, err
	}

	if err := c.validateDateRange(params.StartDate, params.EndDate); err != nil {
		return nil, err
	}

	requestBody := c.buildGameQuery(params)
	return c.executeGameRequest(requestBody)
}

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
	defer resp.Body.Close()

	return c.processTokenResponse(resp)
}

func (c *Client) String() string {
	return fmt.Sprintf("IGDB Client: %s (Client ID: %s)", c.baseURL, c.clientID)
}

func (c *Client) validateToken() error {
	if c.token.AccessToken == "" {
		c.logger.Error().Msg("No access token available")
		return fmt.Errorf("no access token available, call GetToken() first")
	}
	return nil
}

func (c *Client) validateDateRange(startDate, endDate time.Time) error {
	if !startDate.IsZero() && !endDate.IsZero() && startDate.After(endDate) {
		c.logger.Error().Msg("Start date cannot be after end date")
		return fmt.Errorf("start date cannot be after end date")
	}
	return nil
}

func (c *Client) buildGameQuery(params GameQueryParams) string {
	fields := "fields id,name,summary,storyline,category,status,first_release_date,total_rating,total_rating_count,aggregated_rating,aggregated_rating_count,keywords,genres,themes,game_modes,player_perspectives,collection,franchises,parent_game,created_at,updated_at; "
	pagination := fmt.Sprintf("limit %d; offset %d; ", params.Limit, params.Offset)
	sortClause := "sort total_rating desc; "

	whereClause := c.buildWhereClause(params)

	return fields + pagination + whereClause + sortClause
}

func (c *Client) buildWhereClause(params GameQueryParams) string {
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

func (c *Client) executeGameRequest(requestBody string) ([]byte, error) {
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
	defer resp.Body.Close()

	return c.processGameResponse(resp)
}

func (c *Client) setGameRequestHeaders(req *http.Request) {
	req.Header.Set("Client-ID", c.clientID)
	req.Header.Set("Authorization", "Bearer "+c.token.AccessToken)
	req.Header.Set("Content-Type", contentTypeText)
}

func (c *Client) logGameRequest(endpoint string) {
	c.logger.Debug().
		Str("endpoint", endpoint).
		Str("token", c.token.AccessToken[:10]+"...").
		Msg("Making games request")
}

func (c *Client) processGameResponse(resp *http.Response) ([]byte, error) {
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

func (c *Client) buildTokenEndpoint() string {
	return fmt.Sprintf("%stoken?client_id=%s&client_secret=%s&grant_type=client_credentials",
		c.loginURL, c.clientID, c.apiKey)
}

func (c *Client) createTokenRequest(endpoint string) (*http.Request, error) {
	req, err := http.NewRequest("POST", endpoint, nil)
	if err != nil {
		c.logger.Error().Err(err).Msg("Failed to create token request")
		return nil, fmt.Errorf("failed to create request: %w", err)
	}

	req.Header.Set("Content-Type", contentTypeJSON)
	c.logger.Debug().Str("endpoint", endpoint).Msg("Requesting IGDB token")

	return req, nil
}

func (c *Client) processTokenResponse(resp *http.Response) (string, error) {
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
