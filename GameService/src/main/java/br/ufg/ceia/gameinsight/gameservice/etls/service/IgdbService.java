package br.ufg.ceia.gameinsight.gameservice.etls.service;

import br.ufg.ceia.gameinsight.gameservice.domain.game.Game;
import br.ufg.ceia.gameinsight.gameservice.domain.game.release_date.ReleaseDate;
import br.ufg.ceia.gameinsight.gameservice.domain.platform.Platform;
import br.ufg.ceia.gameinsight.gameservice.etls.dtos.CoverIgdbDto;
import br.ufg.ceia.gameinsight.gameservice.etls.dtos.IgdbGameDto;
import br.ufg.ceia.gameinsight.gameservice.etls.dtos.IgdbPlatformDto;
import br.ufg.ceia.gameinsight.gameservice.etls.dtos.TwitchAccessToken;
import br.ufg.ceia.gameinsight.gameservice.repository.GameRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.GeneratedValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class IgdbService {

    // Log SYSTEM
    private static final Logger logger =
            LoggerFactory.getLogger(IgdbService.class);

    // Jackson OBJECT MAPPER
    @Autowired
    private ObjectMapper objectMapper;

    // Game repository
    @Autowired
    private GameRepository gameRepository;

    // Platform repository
    @Autowired
    private PlatformRepository platformRepository;

    // Release date repository
    @Autowired
    private ReleaseDateRepository releaseDateRepository;

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

    @Value("${igdb.platforms-endpoint}")
    private String platformEndpoint;

    @Value("${igdb.covers-endpoint}")
    private String coverEndpoint;

    @Value("${igdb.release-dates-endpoint}")
    private String releaseDatesEndpoint;

    @Value("${igdb.access-token}")
    private String accessToken;

    @Value("${igdb.token-expiration}")
    private Instant tokenExpiration;

    private List<IgdbGameDto> games;

    /**
     * @brief Method to get the headers for the request
     * @details Method to get the headers for the request
     * @return HttpHeaders - The headers for the request
     */
    private HttpHeaders getHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.set("Client-ID", clientId);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    /**
     * @brief Method to get the access token
     * @details Method to get the access token, if the token does not exist or is expired,
     * it will be obtained again, doing the login again in the twitch API
     * @param dateToStart The date to start the ETL process
     * @param searchType  The type of search to be performed can be "updated_at" or "created_at"
     * @return String - Access token
     * @throws RuntimeException - If the access token is not obtained
     */
    public void RunETL(Instant dateToStart, String searchType) {
        // If the token does not exist or is expired, it will be obtained again
        if (accessToken == null || accessToken.isEmpty() || tokenIsExpired()) {
            // Call the login method
            login();
        }

        // Limit of games to search
        int limit = 50;
        // Offset to start the search
        int offset = 0;

        do {
            // Search for games in the IGDB API
            SearchForGames(dateToStart, limit, offset, searchType);
            // If there are no games, log the message and break out of the loop
            if (games.isEmpty()) {
                logger.info("No games found");
                break;
            }
            // Update the offset for the next page of results
            offset += limit;

            // For each game, log the message
            games.forEach(game -> {
                // Log the game name
                logger.info("Game name: {}", game.getName());
                // Transform into a Game entity
                Game gameEntity = new Game();

                Game gameFound = gameRepository.findByTitle(game.getName());
                if (gameFound != null) {
                    logger.info("Game already exists: {}", game.getName());
                    if (!gameFound.equals(game)) {
                        logger.info("Game has changed: {}", game.getName());
                        gameEntity.setId(gameFound.getId());
                    } else {
                        logger.info("Game has not changed: {}", game.getName());
                        return;
                    }
                }

                // Set the game title
                gameEntity.setTitle(game.getName());
                // Set the game story line
                gameEntity.setSummary(game.getSummary());

                // Set the game platform
                List<Platform> platformsToAdd = new ArrayList<>();
                game.getPlatforms().forEach(platformIgdbId -> {
                    platformsToAdd.add(SearchForPlatforms(platformIgdbId));
                });
                gameEntity.setPlatforms(platformsToAdd);

                // Set the game cover
                if (game.getCover()!=null) {
                    // Set the game cover URL by the method getCoverUrl
                    gameEntity.setCover(GetCoverUrl(game.getCover()));
                }

                // Set the game release date by the method SearchForReleaseDates
                List<ReleaseDate> releaseDatesToAdd = new ArrayList<>();
                game.getReleaseDates().forEach(releaseDate -> {
                    releaseDatesToAdd.add(SearchForReleaseDates(releaseDate));
                });
                gameEntity.setReleaseDates(releaseDatesToAdd);


                // Save the game in the database
                gameRepository.save(gameEntity);
            });
        } while (games.size() == limit);
    }

    /**
     * @return boolean - If the token is expired
     * @brief Method to check if the token is expired
     * @details Method to check if the token is expired
     * by comparing the current time with the expiration time
     */
    private boolean tokenIsExpired() {
        return tokenExpiration == null || Instant.now().isAfter(tokenExpiration);
    }

    /**
     * @return void
     * @throws RuntimeException - If the access token is not obtained
     * @brief Method to login
     * @details Method to login, getting the access token from the twitch API
     */
    private void login() {
        // Create a new RestTemplate to make the request
        RestTemplate restTemplate = new RestTemplate();

        // Create the URL to make the request
        String url = authUrl
                .replace("${IGDB_CLIENT_ID}", clientId)
                .replace("${IGDB_CLIENT_SECRET}", clientSecret);

        //Make the request to the API
        ResponseEntity<TwitchAccessToken> response = restTemplate.postForEntity(url, null, TwitchAccessToken.class);

        // If the request isn't successful, throw an exception
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Failed to obtain access token");
        }

        // Deserializes the response body
        TwitchAccessToken tokenResponse = Objects.requireNonNull(response.getBody());
        // Set the access token and expiration time
        accessToken = tokenResponse.getAccess_token();
        tokenExpiration = Instant.now().plusSeconds(tokenResponse.getExpires_in());

        // Show the access token and expiration time in the log
        logger.info("Access token: {}", accessToken);
        logger.info("Token expiration: {}", tokenExpiration);
        // Log the success message
        logger.info("Access token obtained successfully");
    }

    /**
     * @brief Method to search for games
     * @details Method to search for games in the IGDB API
     * @param dateToStart The date to start the search
     * @param limit       The limit of games to search
     * @param offset      The offset to start the search
     * @param searchType The type of search to be performed can be "updated_at" or "created_at"
     * @return void
     */
    private void SearchForGames(Instant dateToStart, int limit, int offset, String searchType) {
        // Log the message to search for games
        logger.info("Searching for games");
        // Log the date to start, limit, offset and search type
        logger.info("Date to start: {}", dateToStart);
        logger.info("Limit: {}", limit);
        logger.info("Offset: {}", offset);
        logger.info("Search type: {}", searchType);

        // Create the URL to make the request
        String url = etlUrl + gameEndpoint;

        // Proper IGDB query format
        String requestBody = "fields *; where " + searchType + " >= "
                + dateToStart.getEpochSecond() + ";"
                + "limit " + limit + ";" + "offset " + offset + ";"
                + "sort updated_at asc;";

        logger.info("Request body: {}", requestBody);

        // Set the headers for the request
        HttpHeaders headers = getHttpHeaders();

        // Set the request entity
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        // Create a new RestTemplate to make the request
        RestTemplate restTemplate = new RestTemplate();


        // Make the request to the API
        ResponseEntity<String> response = restTemplate.postForEntity(url,
                requestEntity, String.class);

        // If the request isn't successful, throw an exception
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Failed to obtain games");
        }

        // Deserialize the response body to List<IgdbGameDto>
        try {
            String jsonResponse = response.getBody();
            // Log the games obtained
            logger.info("Games obtained: {}", jsonResponse);

            // Transform the games to a List<IgdbGameDto>
            games = objectMapper.readValue(jsonResponse,
                    new TypeReference<List<IgdbGameDto>>() {
                    });
        } catch (Exception e) {
            throw new RuntimeException("Error while parsing the game data", e);
        }
    }

    /**
     * @brief Method to search for platforms
     * @details Method to search for platforms in the IGDB API
     * @param platformIgdbId The ID of the platform in the IGDB API
     * @return Platform - The platform found
     */
    private Platform SearchForPlatforms(Integer platformIgdbId) {
        // Log the message to search for platforms
        logger.info("Searching for platforms with ID: {}", platformIgdbId);
        // Do a request to IGDB API to get the platform name
        String url = etlUrl + platformEndpoint;
        String requestBody = "fields name; where id = " + platformIgdbId + ";";
        HttpHeaders headers = getHttpHeaders();
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
        if (!response.getStatusCode().is2xxSuccessful()) {
            // Log the error message
            logger.error("Failed to obtain platform");
            // If the request isn't successful, throw an exception
            throw new RuntimeException("Failed to obtain platform");
        }
        try {
            String jsonResponse = response.getBody();
            List<Platform> platforms = objectMapper.readValue(jsonResponse, new TypeReference<List<Platform>>() {
            });
            if (platforms.isEmpty()) {
                throw new RuntimeException("Platform not found");
            }
            // Get the platform from the list
            Platform platform = platforms.get(0);
            // Log the platform obtained
            logger.info("Platform obtained: {}", platforms.get(0).getName());
            // Search for the platform in the database
            Platform platformAtDb = platformRepository.findByName(platform.getName());
            // If the platform does not exist, create a new one
            if (platformAtDb == null) {
                platformRepository.save(platform);
                return platform;
            }
            // If the platform exists, return it
            return platformAtDb;
        } catch (Exception e) {
            throw new RuntimeException("Error while parsing the platform data", e);
        }
    }


    /**
     * @brief Method to get the cover URL
     * @details Method to get the cover URL from the IGDB API
     * @param cover The cover object from the IGDB API
     * @return Game - The game with the cover URL
     */
    private String GetCoverUrl(Long cover) {
        // Log the message to get the cover URL
        logger.info("Getting cover URL with ID: {}", cover);
        // Do a request to IGDB API to get the cover URL
        String url = etlUrl + coverEndpoint;
        String requestBody = "fields url; where id = " + cover + ";";
        HttpHeaders headers = getHttpHeaders();
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity,
                String.class);
        if (!response.getStatusCode().is2xxSuccessful()) {
            // Log the error message
            logger.error("Failed to obtain cover");
            // If the request isn't successful, throw an exception
            throw new RuntimeException("Failed to obtain cover");
        }
        try {
            String jsonResponse = response.getBody();
            CoverIgdbDto foundCover = objectMapper.readValue(jsonResponse,
                    CoverIgdbDto.class);
            if (foundCover == null) {
                throw new RuntimeException("Cover not found");
            }
            // Get the cover URL from the list
            String coverUrl = foundCover.getUrl();
            // Log the cover URL obtained
            logger.info("Cover URL obtained: {}", coverUrl);
            // Return the cover URL
            return coverUrl;
        } catch (Exception e) {
            throw new RuntimeException("Error while parsing the cover data", e);
        }
    }

    /**
     * @brief Method to search for release dates
     * @details Method to search for release dates in the IGDB API
     * @param releaseDate The release date object from the IGDB API
     * @param gameId      The ID of the game
     * @return ReleaseDate - The release date found
     */
    private ReleaseDate SearchForReleaseDates(Long releaseDate, Long gameId) {
        // Log the message to search for release dates
        logger.info("Searching for release dates with ID: {}", releaseDate);
        // Do a request to IGDB API to get the release date
        String url = etlUrl + releaseDatesEndpoint;
        String requestBody = "fields *; where id = " + releaseDate + ";";
        HttpHeaders headers = getHttpHeaders();
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
        if (!response.getStatusCode().is2xxSuccessful()) {
            // Log the error message
            logger.error("Failed to obtain release date");
            // If the request isn't successful, throw an exception
            throw new RuntimeException("Failed to obtain release date");
        }
        try {
            String jsonResponse = response.getBody();
            List<ReleaseDate> releaseDates = objectMapper.readValue(jsonResponse, new TypeReference<List<ReleaseDate>>() {
            });
            if (releaseDates.isEmpty()) {
                throw new RuntimeException("Release date not found");
            }
            // Get the release date from the list
            ReleaseDate foundReleaseDate = releaseDates.get(0);
            // Log the release date obtained
            logger.info("Release date obtained: {}", foundReleaseDate);
            // Search for the release date in the database
            ReleaseDate releaseDateAtDb = releaseDateRepository.findByDate(foundReleaseDate.getDate());
            // Find the game in the database
            Game game = gameRepository.findById(gameId)
                    .orElseThrow(() -> new RuntimeException("Game not found"));
            // If the release date does not exist, create a new one
            if (releaseDateAtDb == null) {
                foundReleaseDate.setGame(game);
                releaseDateRepository.save(foundReleaseDate);
                return foundReleaseDate;
            }
            // If the release date exists, return it
            // Return the release date
            return foundReleaseDate;
        } catch (Exception e) {
            throw new RuntimeException("Error while parsing the release date data", e);
        }
    }
}