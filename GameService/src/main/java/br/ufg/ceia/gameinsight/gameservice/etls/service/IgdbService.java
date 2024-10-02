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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

    // Number of threads for processing games
    @Value("${game.processing.threads}")
    private int numberOfThreads;

    // List to hold games retrieved from IGDB
    private List<IgdbGameDto> games = new ArrayList<>(50);

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
     * @param minRating   The minimum rating for games to be fetched.
     * @param minVotes The minimum number of votes for the games to be included in the ETL process.
     */
    public void RunETL(Instant dateToStart, String searchType, int minRating, int minVotes) {
        if (accessToken == null || accessToken.isEmpty() || tokenIsExpired()) {
            login();
        }

        logger.info("Starting ETL process");

        int limit = 50; // Number of games per request
        int offset = 0; // Offset for pagination

        try {
            // Create a custom ExecutorService with a configurable number of threads
            ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
            do {
                boolean occurredError = false;
                games.clear();
                while (games.isEmpty() && !occurredError) {
                    occurredError = false;
                    try {
                        login();
                        SearchForGames(dateToStart, limit, offset, searchType, minRating, minVotes);
                    } catch (Exception e) {
                        logger.error("Error while fetching games", e);
                        occurredError = true;
                    }
                }
                if (games.isEmpty()) {
                    logger.info("No games found");
                    break;
                }

                // Process each game concurrently using CompletableFuture and the custom ExecutorService
                List<CompletableFuture<Void>> futures = new ArrayList<>();
                for (IgdbGameDto game : games) {
                    CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                        try {
                            gameProcessingService.processGame(game, accessToken);
                        } catch (Exception e) {
                            logger.error("Error while processing game: {}", game.getName(), e);
                        }
                    }, executorService);  // Pass the custom ExecutorService here
                    futures.add(future);
                }

                // Wait for all tasks to complete
                CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

                offset += games.size();
                logger.info("Processed {} games", offset);

            } while (games.size() == limit);

            // Shutdown the ExecutorService after all tasks are done
            executorService.shutdown();
        } catch (IllegalArgumentException e) {
            logger.error("Error when setting the executor service", e);
        }
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
     * @param minRating   The minimum rating for games to be fetched.
     */
    private void SearchForGames(Instant dateToStart, int limit, int offset,
                                String searchType, int minRating, int minVotes) {
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
                + "total_rating > "+ minRating +" & "
                + "total_rating_count > "+ minVotes +";"
                + "limit " + limit
                + "; offset " + offset + ";"
                + "sort total_rating_count desc;";

        logger.info("Request body: {}", requestBody);

        HttpHeaders headers = getHttpHeaders();
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
        RestTemplate restTemplate = new RestTemplate();

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
}
