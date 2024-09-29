package br.ufg.ceia.gameinsight.gameservice.etls.service;

import br.ufg.ceia.gameinsight.gameservice.etls.dtos.IgdbGameDto;
import br.ufg.ceia.gameinsight.gameservice.etls.dtos.TwitchAccessToken;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
// Import statements
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

/**
 * Service class for handling IGDB API interactions and ETL processes.
 */
@Service
public class IgdbService {

    // Logger for logging messages
    private static final Logger logger = LoggerFactory.getLogger(IgdbService.class);

    // Jackson ObjectMapper for JSON parsing
    @Autowired
    private ObjectMapper objectMapper;

    // IGDB API configuration properties
    @Value("${igdb.client-id}")
    private String clientId;

    @Value("${igdb.client-secret}")
    private String clientSecret;

    @Value("${igdb.auth-url}")
    private String authUrl;

    @Value("${igdb.url}")
    private String etlUrl;

    @Value("${igdb.game-endpoint}")
    private String gameEndpoint;

    // Access token and expiration for IGDB API
    private String accessToken;

    private Instant tokenExpiration;

    // IGDB API request limits
    @Value("${igdb.max_requests}")
    private int maxRequests;

    private int requests = 0;

    @Value("${igdb.retry_time}")
    private int retryTime;

    // List to hold games retrieved from IGDB
    private List<IgdbGameDto> games;

    // Inject the GameProcessingService
    @Autowired
    private GameProcessingService gameProcessingService;

    /**
     * Constructs the HTTP headers required for IGDB API requests.
     *
     * @return HttpHeaders containing an authorization and content type.
     */
    private HttpHeaders getHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.set("Client-ID", clientId);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    /**
     * Runs the ETL process to fetch and store game data from IGDB.
     *
     * @param dateToStart The date to start fetching games from.
     * @param searchType  The type of search ("updated_at" or "created_at").
     */
    public void RunETL(Instant dateToStart, String searchType) {
        // Refresh the access token if expired
        if (accessToken == null || accessToken.isEmpty() || tokenIsExpired()) {
            login();
        }

        logger.info("Starting ETL process");

        int limit = 50; // Number of games per request
        int offset = 0; // Offset for pagination

        do {
            // Fetch games from IGDB API
            SearchForGames(dateToStart, limit, offset, searchType);

            if (games.isEmpty()) {
                logger.info("No games found");
                break;
            }

            offset += limit;

            // Process each game
            for (IgdbGameDto game : games) {
                // Delegate processing to GameProcessingService
                gameProcessingService.processGame(game,accessToken);
            }
        } while (games.size() == limit);
    }

    /**
     * Checks if the access token has expired.
     *
     * @return True if the token is expired, false otherwise.
     */
    private boolean tokenIsExpired() {
        return tokenExpiration == null || Instant.now().isAfter(tokenExpiration);
    }

    /**
     * Authenticates with the IGDB API to obtain a new access token.
     */
    private void login() {
        RestTemplate restTemplate = new RestTemplate();

        // Construct the authentication URL
        String url = authUrl
                .replace("${IGDB_CLIENT_ID}", clientId)
                .replace("${IGDB_CLIENT_SECRET}", clientSecret);

        // Make the authentication request
        ResponseEntity<TwitchAccessToken> response = restTemplate.postForEntity(url, null, TwitchAccessToken.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Failed to obtain access token");
        }

        // Extract and store the access token and expiration time
        TwitchAccessToken tokenResponse = Objects.requireNonNull(response.getBody());
        accessToken = tokenResponse.getAccess_token();
        tokenExpiration = Instant.now().plusSeconds(tokenResponse.getExpires_in());

        logger.info("Access token obtained successfully");
    }

    /**
     * Searches for games updated after the specified date.
     *
     * @param dateToStart The date to start the search.
     * @param limit       The maximum number of games to retrieve.
     * @param offset      The offset for pagination.
     * @param searchType  The type of search ("updated_at" or "created_at").
     */
    private void SearchForGames(Instant dateToStart, int limit, int offset, String searchType) {
        logger.info("Searching for games");

        String url = etlUrl + gameEndpoint;

        // Construct the request body
        String requestBody = "fields *; "
                + "where " + searchType + " >= " + dateToStart.getEpochSecond() + "&"
                + "platforms != null & "
                + "cover != null & "
                + "summary != null & "
                + "storyline != null & "
                // It is not dlcs or expansions or demos
                + "version_parent = null & "
                + "total_rating > 0 & "
                + "total_rating_count > 0;"
                + "limit " + limit + "; offset " + offset + ";"
                + "sort updated_at asc;";

        logger.debug("Request body: {}", requestBody);

        HttpHeaders headers = getHttpHeaders();
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
        RestTemplate restTemplate = new RestTemplate();

        // Respect the request rate limit
        manageRequestRate();

        ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Failed to obtain games");
        }

        try {
            String jsonResponse = response.getBody();
            logger.debug("Games response: {}", jsonResponse);

            // Parse the JSON response into game DTOs
            games = objectMapper.readValue(jsonResponse, new TypeReference<List<IgdbGameDto>>() {
            });
        } catch (Exception e) {
            throw new RuntimeException("Error while parsing the game data", e);
        }
    }

    /**
     * Manages the request rate to comply with IGDB API limits.
     */
    private void manageRequestRate() {
        if (requests >= maxRequests) {
            try {
                logger.info("Rate limit reached, waiting for {} milliseconds", retryTime);
                Thread.sleep(retryTime);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            requests = 0;
        }
        requests++;
    }
}
