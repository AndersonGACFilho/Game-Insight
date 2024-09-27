package br.ufg.ceia.gameinsight.gameservice.etls.service;

import br.ufg.ceia.gameinsight.gameservice.domain.company.Company;
import br.ufg.ceia.gameinsight.gameservice.domain.company.company_game.CompanyGame;
import br.ufg.ceia.gameinsight.gameservice.domain.game.Game;
import br.ufg.ceia.gameinsight.gameservice.domain.game.age_rating.AgeRating;
import br.ufg.ceia.gameinsight.gameservice.domain.game.franchise.Franchise;
import br.ufg.ceia.gameinsight.gameservice.domain.game.game_mode.GameMode;
import br.ufg.ceia.gameinsight.gameservice.domain.game.game_theme.GameTheme;
import br.ufg.ceia.gameinsight.gameservice.domain.game.genre.Genre;
import br.ufg.ceia.gameinsight.gameservice.domain.game.localization.Localization;
import br.ufg.ceia.gameinsight.gameservice.domain.game.player_perspective.PlayerPerspective;
import br.ufg.ceia.gameinsight.gameservice.domain.game.region.Region;
import br.ufg.ceia.gameinsight.gameservice.domain.game.release_date.ReleaseDate;
import br.ufg.ceia.gameinsight.gameservice.domain.platform.Platform;
import br.ufg.ceia.gameinsight.gameservice.etls.dtos.*;
import br.ufg.ceia.gameinsight.gameservice.repository.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.GeneratedValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
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

    // Region repository
    @Autowired
    private RegionRepository regionRepository;

    // Genre repository
    @Autowired
    private GenreRepository genreRepository;

    // Age rating repository
    @Autowired
    private AgeRatingRepository ageRatingRepository;

    // Player perspective repository
    @Autowired
    private PlayerPerspectiveRepository playerPerspectiveRepository;

    // Game theme repository
    @Autowired
    private ThemeRepository themeRepository;

    // Game mode repository
    @Autowired
    private GameModeRepository gameModeRepository;

    // Franchise repository
    @Autowired
    private FranchiseRepository franchiseRepository;

    // Localization repository
    @Autowired
    private LocalizationRepository localizationRepository;

    // Company repository
    @Autowired
    private CompanyRepository companyRepository;

    // Company game repository
    @Autowired
    private CompanyGameRepository companyGameRepository;

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

    @Value("${igdb.regions-endpoint}")
    private String regionsEndpoint;

    @Value("${igdb.release-dates-endpoint}")
    private String releaseDatesEndpoint;

    @Value("${igdb.genres-endpoint}")
    private String genresEndpoint;

    @Value("${igdb.age-rating-endpoint}")
    private String ageRatingsEndpoint;

    @Value("${igdb.player-perspectives-endpoint}")
    private String playerPerspectivesEndpoint;

    @Value("${igdb.themes-endpoint}")
    private String gameThemesEndpoint;

    @Value("${igdb.game-modes-endpoint}")
    private String gameModesEndpoint;

    @Value("${igdb.franchises-endpoint}")
    private String franchisesEndpoint;

    @Value("${igdb.game-localizations-endpoint}")
    private String localizationsEndpoint;

    @Value("${igdb.involved-companies-endpoint}")
    private String involvedCompaniesEndpoint;

    @Value("${igdb.companies-endpoint}")
    private String companiesEndpoint;

    @Value("${igdb.company-logos-endpoint}")
    private String companyLogosEndpoint;

    @Value("${igdb.access-token}")
    private String accessToken;

    @Value("${igdb.token-expiration}")
    private Instant tokenExpiration;

    @Value("${igdb.max_requests}")
    private int maxRequests;

    private int requests = 0;

    @Value("${igdb.retry_time}")
    private int retryTime;

    private List<IgdbGameDto> games;

    /**
     * @return HttpHeaders - The headers for the request
     * @brief Method to get the headers for the request
     * @details Method to get the headers for the request
     */
    private HttpHeaders getHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.set("Client-ID", clientId);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    /**
     * @param dateToStart The date to start the ETL process
     * @param searchType  The type of search to be performed can be "updated_at" or "created_at"
     * @return String - Access token
     * @throws RuntimeException - If the access token is not obtained
     * @brief Method to get the access token
     * @details Method to get the access token, if the token does not exist or is expired,
     * it will be obtained again, doing the login again in the twitch API
     */
    @Transactional(rollbackFor = RuntimeException.class)
    public void RunETL(Instant dateToStart, String searchType) {
        // If the token does not exist or is expired, it will be obtained again
        if (accessToken == null || accessToken.isEmpty() || tokenIsExpired()) {
            // Call the login method
            login();
        }

        // Log the message to start the ETL process
        logger.info("Starting ETL process");
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
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
                try {
                    // Log the game name
                    logger.info("Gam: {}", game);
                    // Transform into a Game entity
                    Game gameEntity = new Game();

                    // Search for the game in the database
                    Game gameFound = gameRepository.findByTitle(game.getName());

                    // Set the IGDB ID
                    gameEntity.setIGDBId(game.getId());

                    // Set the updated at
                    gameEntity.setUpdatedAt(Instant.ofEpochSecond(game.getUpdatedAt()));

                    if (gameFound != null) {
                        logger.info("Game already exists: {}", game.getName());
                        // Check if the game has changed
                        if (!gameFound.getUpdatedAt().isBefore(gameEntity.getUpdatedAt())) {
                            logger.info("Game has changed: {}", game.getName());
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
                        Platform platform = SearchForPlatforms(platformIgdbId);
                        if (platform == null) {
                            return;
                        }
                        platformsToAdd.add(platform);
                    });
                    gameEntity.setPlatforms(platformsToAdd);

                    // Set the game cover
                    if (game.getCover() != null) {
                        // Set the game cover URL by the method getCoverUrl
                        gameEntity.setCover(GetCoverUrl(game.getCover()));
                    }

                    // Set the game release date by the method SearchForReleaseDates
                    List<ReleaseDate> releaseDatesToAdd = new ArrayList<>();
                    game.getReleaseDates().forEach(releaseDate -> {
                        ReleaseDate releaseDateEntity = SearchForReleaseDates(releaseDate, gameEntity);
                        if (releaseDateEntity == null) {
                            return;
                        }
                        releaseDatesToAdd.add(releaseDateEntity);
                    });
                    gameEntity.setReleaseDates(releaseDatesToAdd);

                    // Set the game rating
                    gameEntity.setRating(game.getTotalRating());
                    // Set the game releaseDateEntity count
                    gameEntity.setRatingCount(game.getTotalRatingCount());

                    // Set the game genres
                    List<Genre> genresToAdd = new ArrayList<>();
                    game.getGenres().forEach(genre -> {
                        Genre genreEntity = SearchForGenres(genre, gameEntity);
                        if (genreEntity == null) {
                            return;
                        }
                        genresToAdd.add(genreEntity);
                    });
                    gameEntity.setGenres(genresToAdd);


                    // Set the game age rating
                    List<AgeRating> ageRatingsToAdd = new ArrayList<>();
                    if (game.getAgeRatings() != null) {
                        game.getAgeRatings().forEach(ageRating -> {
                            AgeRating ageRatingEntity = SearchForAgeRatings(Math.toIntExact(ageRating), gameEntity);
                            if (ageRatingEntity == null) {
                                return;
                            }
                            ageRatingsToAdd.add(ageRatingEntity);
                        });
                    }
                    gameEntity.setAgeRatings(ageRatingsToAdd);

                    // Set Similar games
                    List<Game> similarGames = new ArrayList<>();
                    if (game.getSimilarGames() != null){
                        game.getSimilarGames().forEach(similarGame -> {
                            Game similarGameEntity = gameRepository.findByIGDBId(similarGame);
                            if (similarGameEntity == null) {
                                return;
                            }
                            similarGames.add(similarGameEntity);
                        });
                    }
                    gameEntity.setSimilarGames(similarGames);

                    // Set the player perspectives
                    List<PlayerPerspective> playerPerspectivesToAdd = new ArrayList<>();
                    if (game.getPlayerPerspectives() != null) {
                        game.getPlayerPerspectives().forEach(playerPerspective -> {
                            PlayerPerspective playerPerspectiveEntity = SearchForPlayerPerspectives(playerPerspective);
                            if (playerPerspectiveEntity == null) {
                                return;
                            }
                            playerPerspectivesToAdd.add(playerPerspectiveEntity);
                        });
                    }
                    gameEntity.setPlayerPerspectives(playerPerspectivesToAdd);

                    // Set the themes
                    List<GameTheme> themesToAdd = new ArrayList<>();
                    if (game.getThemes() != null) {
                        game.getThemes().forEach(theme -> {
                            GameTheme gameTheme = SearchForThemes(theme);
                            if (gameTheme == null) {
                                return;
                            }
                            themesToAdd.add(gameTheme);
                        });
                    }
                    gameEntity.setThemes(themesToAdd);

                    // Set the game modes
                    List<GameMode> gameModesToAdd = new ArrayList<>();
                    if (game.getGameModes() != null) {
                        game.getGameModes().forEach(gameMode -> {
                            GameMode gameModeEntity = SearchForGameModes(gameMode);
                            if (gameModeEntity == null) {
                                return;
                            }
                            gameModesToAdd.add(gameModeEntity);
                        });
                    }
                    gameEntity.setGameModes(gameModesToAdd);

                    // Set the game franchise
                    List<Franchise> franchisesToAdd = new ArrayList<>();
                    if (game.getFranchises() != null) {
                        game.getFranchises().forEach(franchise -> {
                            Franchise franchiseEntity = SearchForFranchises(Math.toIntExact(franchise), gameEntity);
                            if (franchiseEntity == null) {
                                return;
                            }
                            franchisesToAdd.add(franchiseEntity);
                        });
                    }
                    gameEntity.setFranchises(franchisesToAdd);

                    // Set the game localizations
                    List<Localization> localizationsToAdd = new ArrayList<>();
                    if (game.getGameLocalizations() != null) {
                        game.getGameLocalizations().forEach(localization -> {
                            Localization localizationEntity = SearchForLocalizations(
                                    Math.toIntExact(localization), gameEntity);
                            if (localizationEntity == null) {
                                return;
                            }
                            localizationsToAdd.add(localizationEntity);
                        });
                    }
                    gameEntity.setLocalizations(localizationsToAdd);

                    // Set the involved companies
                    List<CompanyGame> companiesToAdd = new ArrayList<>();
                    if (game.getInvolvedCompanies() != null) {
                        game.getInvolvedCompanies().forEach(company -> {
                            CompanyGame companyGame = SearchForInvolvedCompanies(Math.toIntExact(company), gameEntity);
                            if (companyGame == null) {
                                return;
                            }
                            companiesToAdd.add(companyGame);
                        });
                    }
                    gameEntity.setInvolvedCompanies(companiesToAdd);


                    // Save the game in the database
                    gameRepository.save(gameEntity);
                } catch (Exception e) {
                    // Log the error message
                    logger.error("Error while processing game: {}", game.getName(), e);
                    // Raise the exception
                    throw new RuntimeException("Error while processing game", e);
                }
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
     * @param dateToStart The date to start the search
     * @param limit       The limit of games to search
     * @param offset      The offset to start the search
     * @param searchType  The type of search to be performed can be "updated_at" or "created_at"
     * @return void
     * @brief Method to search for games
     * @details Method to search for games in the IGDB API
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
                + "sort updated_at asc;"
                + "where total_rating > 50;"
                + "where platforms != null;";

        logger.info("Request body: {}", requestBody);

        // Set the headers for the request
        HttpHeaders headers = getHttpHeaders();

        // Set the request entity
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        // Create a new RestTemplate to make the request
        RestTemplate restTemplate = new RestTemplate();


        if (requests >= maxRequests) {
            try {
                Thread.sleep(retryTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            requests = 0;
        }
        requests++;
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
     * @param platformIgdbId The ID of the platform in the IGDB API
     * @return Platform - The platform found
     * @brief Method to search for platforms
     * @details Method to search for platforms in the IGDB API
     */
    private Platform SearchForPlatforms(Integer platformIgdbId) {
        // Log the message to search for platforms
        logger.info("Searching for platforms with ID: {}", platformIgdbId);
        // Do a request to IGDB API to get the platform name
        ResponseEntity<String> response = SendRequest(platformIgdbId, platformEndpoint);
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
            logger.info("Platforms obtained: {}", platforms);
            if (platforms.isEmpty()) {
                return null;
            }
            // Get the platform from the list
            Platform platform = platforms.get(0);
            platform.setId(null);
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
            throw new RuntimeException("Error while parsing the platform data" + e.getMessage(), e);
        }
    }

    /**
     * @param cover The cover object from the IGDB API
     * @return Game - The game with the cover URL
     * @brief Method to get the cover URL
     * @details Method to get the cover URL from the IGDB API
     */
    private String GetCoverUrl(Long cover) {
        // Log the message to get the cover URL
        logger.info("Getting cover URL with ID: {}", cover);
        // Do a request to IGDB API to get the cover URL
        String url = etlUrl + coverEndpoint;
        String requestBody = "fields *; where id=" + cover + ";";
        HttpHeaders headers = getHttpHeaders();
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
        RestTemplate restTemplate = new RestTemplate();
        if (requests >= maxRequests) {
            try {
                Thread.sleep(retryTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            requests = 0;
        }
        requests++;
        ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity,
                String.class);

        // Log the url
        logger.info("URL: {}", url);
        // Log the request
        logger.info("Request: {}", requestEntity);
        // Log the response
        logger.info("Response: {}", response);

        if (!response.getStatusCode().is2xxSuccessful()) {
            // Log the error message
            logger.error("Failed to obtain cover");
            // If the request isn't successful, throw an exception
            throw new RuntimeException("Failed to obtain cover");
        }
        try {
            String jsonResponse = response.getBody();
            List<CoverIgdbDto> foundCovers = objectMapper.readValue(jsonResponse,
                    new TypeReference<List<CoverIgdbDto>>() {
                    });
            if (foundCovers.isEmpty()) {
                return null;
            }
            // Get the cover from the list
            CoverIgdbDto foundCover = foundCovers.get(0);
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
     * @param releaseDate The release date object from the IGDB API
     * @param game        The game associated with the release date
     * @return ReleaseDate - The release date found
     * @brief Method to search for release dates
     * @details Method to search for release dates in the IGDB API
     */
    private ReleaseDate SearchForReleaseDates(Long releaseDate, Game game) {
        // Log the message to search for release dates
        logger.info("Searching for release dates with ID: {}", releaseDate);
        // Do a request to IGDB API to get the release date
        ResponseEntity<String> response = SendRequest(Math.toIntExact(releaseDate), releaseDatesEndpoint, "date");
        if (!response.getStatusCode().is2xxSuccessful()) {
            // Log the error message
            logger.error("Failed to obtain release date");
            // If the request isn't successful, throw an exception
            throw new RuntimeException("Failed to obtain release date");
        }
        try {
            String jsonResponse = response.getBody();
            if (Objects.equals(jsonResponse, "[]")) {
                return null;
            }
            List<ReleaseDateIgdbDto> releaseDates = objectMapper.readValue(jsonResponse,
                    new TypeReference<List<ReleaseDateIgdbDto>>() {
                    });
            if (releaseDates.isEmpty()) {
                return null;
            }
            // Get the release date from the list
            ReleaseDateIgdbDto foundIgdbReleaseDate = releaseDates.get(0);

            if (foundIgdbReleaseDate.getDate()  == null||
                foundIgdbReleaseDate.getPlatform()  == null||
                foundIgdbReleaseDate.getRegion() == null) {
                return null;
            }

            // Create a new ReleaseDate object
            ReleaseDate foundReleaseDate = new ReleaseDate();
            // Set the release date
            foundReleaseDate.setDate(foundIgdbReleaseDate.getDate());
            // Set the game for the release date
            foundReleaseDate.setGame(game);
            // Set the platform for the release date
            foundReleaseDate.setPlatform(SearchForPlatforms(foundIgdbReleaseDate.getPlatform()));
            // Set the region for the release date
            foundReleaseDate.setRegion(SearchForRegions(foundIgdbReleaseDate.getRegion()));


            // Log the release date obtained
            logger.info("Release date obtained: {}", foundReleaseDate);
            // Search for the release date in the database
            List<ReleaseDate> releaseDatesAtDb = releaseDateRepository.findAllByGame(game);
            // For each release date in the database
            for (ReleaseDate releaseDateAtDb : releaseDatesAtDb) {
                // If the release date exists, return it
                if (releaseDateAtDb.equals(foundReleaseDate)) {
                    return releaseDateAtDb;
                }
                // If the release date is not equal but the platform and region are equal, update the date
                if (releaseDateAtDb.getPlatform().equals(foundReleaseDate.getPlatform())
                        && releaseDateAtDb.getRegion().equals(foundReleaseDate.getRegion())) {
                    // Update the date
                    releaseDateAtDb.setDate(foundReleaseDate.getDate());
                    break;
                }
            }
            // If the release date does not exist, create a new one
            releaseDateRepository.save(foundReleaseDate);
            return foundReleaseDate;
        } catch (Exception e) {
            throw new RuntimeException("Error while parsing the release date data", e);
        }
    }

    /**
     * @param regionIgdbId The ID of the region in the IGDB API
     * @return Region - The region found
     * @brief Method to search for regions
     * @details Method to search for regions in the IGDB API
     */
    private Region SearchForRegions(Integer regionIgdbId) {
        // Log the message to search for regions
        logger.info("Searching for regions with ID: {}", regionIgdbId);
        // Do a request to IGDB API to get the region name
        ResponseEntity<String> response = SendRequest(regionIgdbId, regionsEndpoint);
        if (!response.getStatusCode().is2xxSuccessful()) {
            // Log the error message
            logger.error("Failed to obtain region");
            // If the request isn't successful, throw an exception
            throw new RuntimeException("Failed to obtain region");
        }
        try {
            String jsonResponse = response.getBody();
            if (Objects.equals(jsonResponse, "[]")) {
                return null;
            }
            List<Region> regions = objectMapper.readValue(jsonResponse, new TypeReference<List<Region>>() {
            });
            if (regions.isEmpty()) {
                return null;
            }
            // Get the region from the list
            Region region = regions.get(0);
            // Log the region obtained
            logger.info("Region obtained: {}", regions.get(0).getName());
            // Search for the region in the database
            Region regionAtDb = regionRepository.findByName(region.getName());
            // If the region does not exist, create a new one
            if (regionAtDb == null) {
                return region;
            }
            // If the region exists, return it
            return regionAtDb;
        } catch (Exception e) {
            throw new RuntimeException("Error while parsing the region data", e);
        }
    }

    /**
     * @param IgdbId   The ID of the entity in the IGDB API
     * @param Endpoint The endpoint to search for the entity
     * @return ResponseEntity<String> - The response entity
     * @brief Method to send a request to the IGDB API
     * @details Method to send a request to the IGDB API
     */
    private ResponseEntity<String> SendRequest(Integer IgdbId, String Endpoint, String... fields) {
        String url = etlUrl + Endpoint;
        logger.info("Sending request to: {}", url);
        // Proper IGDB query format
        // If there are fields, add them to the request, concatenate them with a comma
        String requestBody = "fields *" + (fields.length > 0 ? ", " + String.join(", ", fields) : "")+ ";"
                +" where id = " + IgdbId + ";";
        HttpHeaders headers = getHttpHeaders();
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
        RestTemplate restTemplate = new RestTemplate();
        if (requests >= maxRequests) {
            try {
                logger.info("Waiting for retry time");
                Thread.sleep(retryTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            requests = 0;
        }
        requests++;
        ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
        logger.info("Request sent successfully");
        logger.info("Response: {}", response);
        logger.info("Response body: {}", response.getBody());
        logger.info("Response status: {}", response.getStatusCode());
        return response;
    }

    /**
     * @param genre      The ID of the genre in the IGDB API
     * @param gameEntity The game associated with the genre
     * @return Genre - The genre found
     * @brief Method to search for genres
     * @details Method to search for genres in the IGDB API
     */
    private Genre SearchForGenres(Integer genre, Game gameEntity) {
        // Log the message to search for genres
        logger.info("Searching for genres with ID: {}", genre);
        // Do a request to IGDB API to get the genre name
        ResponseEntity<String> response = SendRequest(genre, genresEndpoint);
        if (!response.getStatusCode().is2xxSuccessful()) {
            // Log the error message
            logger.error("Failed to obtain genre");
            // If the request isn't successful, throw an exception
            throw new RuntimeException("Failed to obtain genre");
        }
        try {
            String jsonResponse = response.getBody();
            List<Genre> genres = objectMapper.readValue(jsonResponse, new TypeReference<List<Genre>>() {
            });
            if (genres.isEmpty()) {
                return null;
            }
            // Get the genre from the list
            Genre genreFound = genres.get(0);
            genreFound.setId(null);
            // Log the genre obtained
            logger.info("Genre obtained: {}", genres.get(0).getName());
            // Search for the genre in the database
            Genre genreAtDb = genreRepository.findByName(genreFound.getName());
            // If the genre does not exist, create a new one
            if (genreAtDb == null) {
                genreRepository.save(genreFound);
                return genreFound;
            }
            // If the genre exists, return it
            return genreAtDb;
        } catch (Exception e) {
            throw new RuntimeException("Error while parsing the genre data", e);
        }
    }

    /**
     * @param ageRating  The ID of the age rating in the IGDB API
     * @param gameEntity The game associated with the age rating
     * @return AgeRating - The age rating found
     * @brief Method to search for age ratings
     * @details Method to search for age ratings in the IGDB API
     */
    private AgeRating SearchForAgeRatings(Integer ageRating, Game gameEntity) {
        // Log the message to search for age ratings
        logger.info("Searching for age ratings with ID: {}", ageRating);
        // Do a request to IGDB API to get the age rating name
        ResponseEntity<String> response = SendRequest(ageRating, ageRatingsEndpoint);
        if (!response.getStatusCode().is2xxSuccessful()) {
            // Log the error message
            logger.error("Failed to obtain age rating");
            // If the request isn't successful, throw an exception
            throw new RuntimeException("Failed to obtain age rating");
        }
        try {
            String jsonResponse = response.getBody();
            List<AgeRating> ageRatings = objectMapper.readValue(jsonResponse, new TypeReference<List<AgeRating>>() {
            });
            if (ageRatings.isEmpty()) {
                return null;
            }
            // Get the age rating from the list
            AgeRating ageRatingFound = ageRatings.get(0);
            ageRatingFound.setId(null);
            // Log the age rating obtained
            logger.info("Age rating obtained: {}", ageRatings.get(0).getName());
            // Search for the age rating in the database
            AgeRating ageRatingAtDb = ageRatingRepository.findByName(ageRatingFound.getName());
            // If the age rating does not exist, create a new one
            if (ageRatingAtDb == null) {
                ageRatingRepository.save(ageRatingFound);
                return ageRatingFound;
            }
            // If the age rating exists, return it
            return ageRatingAtDb;
        } catch (Exception e) {
            throw new RuntimeException("Error while parsing the age rating data", e);
        }
    }

    /**
     * @param playerPerspective The ID of the player perspective in the IGDB API
     * @return PlayerPerspective - The player perspective found
     * @brief Method to search for player perspectives
     * @details Method to search for player perspectives in the IGDB API
     */
    private PlayerPerspective SearchForPlayerPerspectives(Integer playerPerspective) {
        // Log the message to search for player perspectives
        logger.info("Searching for player perspectives with ID: {}", playerPerspective);
        // Do a request to IGDB API to get the player perspective name
        ResponseEntity<String> response = SendRequest(playerPerspective, playerPerspectivesEndpoint);
        if (!response.getStatusCode().is2xxSuccessful()) {
            // Log the error message
            logger.error("Failed to obtain player perspective");
            // If the request isn't successful, throw an exception
            throw new RuntimeException("Failed to obtain player perspective");
        }
        try {
            String jsonResponse = response.getBody();
            List<PlayerPerspective> playerPerspectives = objectMapper.readValue(jsonResponse, new TypeReference<List<PlayerPerspective>>() {
            });
            if (playerPerspectives.isEmpty()) {
                return null;
            }
            // Get the player perspective from the list
            PlayerPerspective playerPerspectiveFound = playerPerspectives.get(0);
            playerPerspectiveFound.setId(null);
            // Log the player perspective obtained
            logger.info("Player perspective obtained: {}", playerPerspectives.get(0).getName());
            // Search for the player perspective in the database
            PlayerPerspective playerPerspectiveAtDb = playerPerspectiveRepository.findByName(playerPerspectiveFound.getName());
            // If the player perspective does not exist, create a new one
            if (playerPerspectiveAtDb == null) {
                playerPerspectiveRepository.save(playerPerspectiveFound);
                return playerPerspectiveFound;
            }
            // If the player perspective exists, return it
            return playerPerspectiveAtDb;
        } catch (Exception e) {
            throw new RuntimeException("Error while parsing the player perspective data", e);
        }
    }

    /**
     * @param theme The ID of the theme in the IGDB API
     * @return GameTheme - The theme found
     * @brief Method to search for themes
     * @details Method to search for themes in the IGDB API
     */
    private GameTheme SearchForThemes(Integer theme) {
        // Log the message to search for themes
        logger.info("Searching for themes with ID: {}", theme);
        // Do a request to IGDB API to get the theme name
        ResponseEntity<String> response = SendRequest(theme, gameThemesEndpoint);
        if (!response.getStatusCode().is2xxSuccessful()) {
            // Log the error message
            logger.error("Failed to obtain theme");
            // If the request isn't successful, throw an exception
            throw new RuntimeException("Failed to obtain theme");
        }
        try {
            String jsonResponse = response.getBody();
            List<GameTheme> themes = objectMapper.readValue(jsonResponse, new TypeReference<List<GameTheme>>() {
            });
            if (themes.isEmpty()) {
                return null;
            }
            // Get the theme from the list
            GameTheme themeFound = themes.get(0);
            themeFound.setId(null);
            // Log the theme obtained
            logger.info("Theme obtained: {}", themes.get(0).getName());
            // Search for the theme in the database
            GameTheme themeAtDb = themeRepository.findByName(themeFound.getName());
            // If the theme does not exist, create a new one
            if (themeAtDb == null) {
                themeRepository.save(themeFound);
                return themeFound;
            }
            // If the theme exists, return it
            return themeAtDb;
        } catch (Exception e) {
            throw new RuntimeException("Error while parsing the theme data", e);
        }
    }

    /**
     * @param gameMode The ID of the game mode in the IGDB API
     * @return GameMode - The game mode found
     * @brief Method to search for game modes
     * @details Method to search for game modes in the IGDB API
     */
    private GameMode SearchForGameModes(Integer gameMode) {
        // Log the message to search for game modes
        logger.info("Searching for game modes with ID: {}", gameMode);
        // Do a request to IGDB API to get the game mode name
        ResponseEntity<String> response = SendRequest(gameMode, gameModesEndpoint);
        if (!response.getStatusCode().is2xxSuccessful()) {
            // Log the error message
            logger.error("Failed to obtain game mode");
            // If the request isn't successful, throw an exception
            throw new RuntimeException("Failed to obtain game mode");
        }
        try {
            String jsonResponse = response.getBody();
            List<GameMode> gameModes = objectMapper.readValue(jsonResponse, new TypeReference<List<GameMode>>() {
            });
            if (gameModes.isEmpty()) {
                return null;
            }
            // Get the game mode from the list
            GameMode gameModeFound = gameModes.get(0);
            gameModeFound.setId(null);
            // Log the game mode obtained
            logger.info("Game mode obtained: {}", gameModes.get(0).getName());
            // Search for the game mode in the database
            GameMode gameModeAtDb = gameModeRepository.findByName(gameModeFound.getName());
            // If the game mode does not exist, create a new one
            if (gameModeAtDb == null) {
                gameModeRepository.save(gameModeFound);
                return gameModeFound;
            }
            // If the game mode exists, return it
            return gameModeAtDb;
        } catch (Exception e) {
            throw new RuntimeException("Error while parsing the game mode data", e);
        }
    }

    /**
     * @param franchise  The ID of the franchise in the IGDB API
     * @param gameEntity The game associated with the franchise
     * @return Franchise - The franchise found
     * @brief Method to search for franchises
     * @details Method to search for franchises in the IGDB API
     */
    private Franchise SearchForFranchises(Integer franchise, Game gameEntity) {
        // Log the message to search for franchises
        logger.info("Searching for franchises with ID: {}", franchise);
        // Do a request to IGDB API to get the franchise name
        ResponseEntity<String> response = SendRequest(franchise, franchisesEndpoint);
        if (!response.getStatusCode().is2xxSuccessful()) {
            // Log the error message
            logger.error("Failed to obtain franchise");
            // If the request isn't successful, throw an exception
            throw new RuntimeException("Failed to obtain franchise");
        }
        try {
            String jsonResponse = response.getBody();
            List<Franchise> franchises = objectMapper.readValue(jsonResponse, new TypeReference<List<Franchise>>() {
            });
            if (franchises.isEmpty()) {
                return null;
            }
            // Get the franchise from the list
            Franchise franchiseFound = franchises.get(0);
            franchiseFound.setId(null);
            // Log the franchise obtained
            logger.info("Franchise obtained: {}", franchises.get(0).getName());
            // Search for the franchise in the database
            Franchise franchiseAtDb = franchiseRepository.findByName(franchiseFound.getName());
            franchiseFound.addGame(gameEntity);
            if (franchiseAtDb == null) {
                franchiseRepository.save(franchiseFound);
                return franchiseFound;
            }
            // If the franchise does not exist, create a new one
            if (franchiseAtDb.getUpdatedAt().isBefore(franchiseFound.getUpdatedAt()) ||
                    !franchiseAtDb.equals(franchiseFound)) {
                franchiseFound.addGame(gameEntity);
                franchiseFound.setId(franchiseAtDb.getId());
                franchiseRepository.save(franchiseFound);
                return franchiseFound;
            }

            // If the franchise exists, return it
            franchiseAtDb.addGame(gameEntity);
            return franchiseAtDb;
        } catch (Exception e) {
            throw new RuntimeException("Error while parsing the franchise data", e);
        }
    }

    /**
     * @param localization The localization object from the IGDB API
     * @param game         The game associated with the localization
     * @return Localization - The localization found
     * @brief Method to search for localizations
     * @details Method to search for localizations in the IGDB API
     */
    private Localization SearchForLocalizations(Integer localization, Game game) {
        // Log the message to search for localizations
        logger.info("Searching for localizations with ID: {}", localization);
        // Do a request to IGDB API to get the localization name
        ResponseEntity<String> response = SendRequest(localization, localizationsEndpoint);
        if (!response.getStatusCode().is2xxSuccessful()) {
            // Log the error message
            logger.error("Failed to obtain localization");
            // If the request isn't successful, throw an exception
            throw new RuntimeException("Failed to obtain localization");
        }
        try {
            String jsonResponse = response.getBody();
            List<IgdbGameLocalizationDto> localizations = objectMapper.readValue(jsonResponse,
                    new TypeReference<List<IgdbGameLocalizationDto>>() {
                    });
            if (localizations.isEmpty()) {
                return null;
            }
            // Get the localization from the list
            IgdbGameLocalizationDto localizationFound = localizations.get(0);
            // Set the game for the new localization
            Localization newLocalization = new Localization();
            newLocalization.setName(localizationFound.getName());
            newLocalization.setGame(game);
            // Log the localization obtained
            logger.info("Localization obtained: {}", localizationFound.getName());
            // Search for regions in the database
            Region regions = regionRepository.findByIgdbId(localizationFound.getRegion());
            // If the region does not exist, create a new one
            if (regions == null) {
                regions = SearchForRegions(Math.toIntExact(localizationFound.getRegion()));
            }
            newLocalization.setRegion(regions);

            // Search for the localization in the database
            Localization localizationAtDb = localizationRepository.findByName(localizationFound.getName());
            // If the localization does not exist, create a new one
            if (localizationAtDb == null) {
                localizationRepository.save(newLocalization);
                return newLocalization;
            }
            // If the localization exists, return it
            return localizationAtDb;
        } catch (Exception e) {
            throw new RuntimeException("Error while parsing the localization data", e);
        }
    }

    /**
     * @param company The ID of the company in the IGDB API
     * @param game    The game associated with the company
     * @return CompanyGame - The company found
     * @brief Method to search for involved companies
     * @details Method to search for involved companies in the IGDB API
     */
    private CompanyGame SearchForInvolvedCompanies(Integer company, Game game) {
        // Log the message to search for involved companies
        logger.info("Searching for involved companies with ID: {}", company);
        // Do a request to IGDB API to get the company name
        ResponseEntity<String> response = SendRequest(company, involvedCompaniesEndpoint);
        if (!response.getStatusCode().is2xxSuccessful()) {
            // Log the error message
            logger.error("Failed to obtain involved company");
            // If the request isn't successful, throw an exception
            throw new RuntimeException("Failed to obtain involved company");
        }
        try {
            String jsonResponse = response.getBody();
            List<IgbdCompanyGameDto> companies = objectMapper.readValue(jsonResponse,
                    new TypeReference<List<IgbdCompanyGameDto>>() {
                    });
            if (companies.isEmpty()) {
                return null;
            }
            // Get the company from the list
            IgbdCompanyGameDto companyFound = companies.get(0);
            // Set the company for the new company
            CompanyGame newCompanyGame = new CompanyGame();

            CompanyGame companyAtDb = companyGameRepository.findByIgdbId(companyFound.getId());
            if (companyAtDb != null) {
                if (!companyAtDb.getUpdatedAt().isBefore(companyFound.getUpdatedAt())) {
                    return companyAtDb;
                }
                else{
                    newCompanyGame.setId(companyAtDb.getId());
                }
            }
            // set the company data
            newCompanyGame.setCompany(SearchForCompanies(Math.toIntExact(companyFound.getCompany()),newCompanyGame));
            newCompanyGame.setUpdatedAt(companyFound.getUpdatedAt());
            newCompanyGame.setGame(game);
            newCompanyGame.setDeveloper(companyFound.isDeveloper());
            newCompanyGame.setPublisher(companyFound.isPublisher());
            newCompanyGame.setSupporter(companyFound.isSupporter());
            newCompanyGame.setPorter(companyFound.isPorter());

            // Log the company obtained
            logger.info("Company game obtained: {}", newCompanyGame);
            // If the company exists, return it
            return companyAtDb;
        } catch (Exception e) {
            throw new RuntimeException("Error while parsing the company data", e);
        }
    }

    /**
     * @param company The ID of the company in the IGDB API
     * @param companyGame The company game associated with the company
     * @return Company - The company found
     * @brief Method to search for companies
     * @details Method to search for companies in the IGDB API
     */
    private Company SearchForCompanies(Integer company, CompanyGame companyGame) {
        // Log the message to search for companies
        logger.info("Searching for companies with ID: {}", company);
        // Do a request to IGDB API to get the company name
        ResponseEntity<String> response = SendRequest(company, companiesEndpoint);
        if (!response.getStatusCode().is2xxSuccessful()) {
            // Log the error message
            logger.error("Failed to obtain company");
            // If the request isn't successful, throw an exception
            throw new RuntimeException("Failed to obtain company");
        }
        try {
            String jsonResponse = response.getBody();
            List<IgbdCompanyDto> companies = objectMapper.readValue(jsonResponse, new TypeReference<List<IgbdCompanyDto>>() {
            });
            if (companies.isEmpty()) {
                throw new RuntimeException("Company not found");
            }
            // Get the company from the list
            IgbdCompanyDto companyFound = companies.get(0);
            // if the company is not in the database, create a new one
            Company companyAtDb = companyRepository.findByIgdbId(companyFound.getId());
            Company newCompany = new Company();
            if (companyAtDb != null) {
                if (!companyAtDb.getUpdatedAt().isBefore(companyFound.getUpdatedAt())) {
                    return companyAtDb;
                }
                else{
                    newCompany.setId(companyAtDb.getId());
                }
            }
            newCompany.setName(companyFound.getName());
            newCompany.setIgdbId(companyFound.getId());
            newCompany.setUpdatedAt(companyFound.getUpdatedAt());
            newCompany.addCompanyGame(companyGame);
            newCompany.setDescription(companyFound.getDescription());
            newCompany.setLogoUrl(SearchForCompanyLogo(companyFound.getLogo()));
            newCompany.addCompanyGame(companyGame);

            // Log the company obtained
            logger.info("Company obtained: {}", newCompany);

            // save the company in the database
            companyRepository.save(newCompany);
            return newCompany;
        } catch (Exception e) {
            throw new RuntimeException("Error while parsing the company data", e);
        }
    }

    /**
     * @param logo The ID of the logo in the IGDB API
     * @return String - The logo URL
     * @brief Method to search for company logos
     * @details Method to search for company logos in the IGDB API
     */
    private String SearchForCompanyLogo(Long logo) {
        // Log the message to search for company logos
        logger.info("Searching for company logos with ID: {}", logo);
        // Do a request to IGDB API to get the company logo URL
        ResponseEntity<String> response = SendRequest(Math.toIntExact(logo), companyLogosEndpoint);
        if (!response.getStatusCode().is2xxSuccessful()) {
            // Log the error message
            logger.error("Failed to obtain company logo");
            // If the request isn't successful, throw an exception
            throw new RuntimeException("Failed to obtain company logo");
        }
        try {
            String jsonResponse = response.getBody();
            List<IgdbCompanyLogoDto> logos = objectMapper.readValue(jsonResponse, new TypeReference<List<IgdbCompanyLogoDto>>() {
            });
            if (logos.isEmpty()) {
                return "";
            }
            // Get the company logo from the list
            IgdbCompanyLogoDto logoFound = logos.get(0);
            // Log the company logo obtained
            logger.info("Company logo obtained: {}", logos.get(0).getUrl());
            // Return the company logo URL
            return logoFound.getUrl();
        } catch (Exception e) {
            throw new RuntimeException("Error while parsing the company logo data", e);
        }
    }
}