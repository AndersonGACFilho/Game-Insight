package br.ufg.ceia.gameinsight.gameservice.etls.service;

import br.ufg.ceia.gameinsight.gameservice.domain.company.Company;
import br.ufg.ceia.gameinsight.gameservice.domain.company.company_game.CompanyGame;
import br.ufg.ceia.gameinsight.gameservice.domain.game.Game;
import br.ufg.ceia.gameinsight.gameservice.domain.game.age_rating.AgeRating;
import br.ufg.ceia.gameinsight.gameservice.domain.game.franchise.Franchise;
import br.ufg.ceia.gameinsight.gameservice.domain.game.game_mode.GameMode;
import br.ufg.ceia.gameinsight.gameservice.domain.game.game_theme.GameTheme;
import br.ufg.ceia.gameinsight.gameservice.domain.game.genre.Genre;
import br.ufg.ceia.gameinsight.gameservice.domain.game.languages.Language;
import br.ufg.ceia.gameinsight.gameservice.domain.game.languages.LanguageSupport;
import br.ufg.ceia.gameinsight.gameservice.domain.game.languages.LanguageSupportType;
import br.ufg.ceia.gameinsight.gameservice.domain.game.player_perspective.PlayerPerspective;
import br.ufg.ceia.gameinsight.gameservice.domain.game.region.Region;
import br.ufg.ceia.gameinsight.gameservice.domain.game.release_date.ReleaseDate;
import br.ufg.ceia.gameinsight.gameservice.domain.platform.Platform;
import br.ufg.ceia.gameinsight.gameservice.etls.dtos.*;
import br.ufg.ceia.gameinsight.gameservice.repository.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
// Import statements
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class for processing individual games.
 */
@Service
public class GameProcessingService {

    private static final Logger logger = LoggerFactory.getLogger(GameProcessingService.class);

    @Autowired
    private ObjectMapper objectMapper;

    // Repositories for database interactions
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private PlatformRepository platformRepository;
    @Autowired
    private ReleaseDateRepository releaseDateRepository;
    @Autowired
    private RegionRepository regionRepository;
    @Autowired
    private GenreRepository genreRepository;
    @Autowired
    private AgeRatingRepository ageRatingRepository;
    @Autowired
    private PlayerPerspectiveRepository playerPerspectiveRepository;
    @Autowired
    private ThemeRepository themeRepository;
    @Autowired
    private GameModeRepository gameModeRepository;
    @Autowired
    private FranchiseRepository franchiseRepository;
    @Autowired
    private LanguageSupportRepository languageSupportRepository;
    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private CompanyGameRepository companyGameRepository;
    @Autowired
    private LanguageRepository languageRepository;

    // IGDB API configuration properties
    @Value("${igdb.client-id}")
    private String clientId;

    @Value("${igdb.url}")
    private String etlUrl;

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

    @Value("${igdb.game-language-support-endpoint}")
    private String languageSupportEndpoint;

    @Value("${igdb.languages-endpoint}")
    private String languagesEndpoint;

    @Value("${igdb.involved-companies-endpoint}")
    private String involvedCompaniesEndpoint;

    @Value("${igdb.companies-endpoint}")
    private String companiesEndpoint;

    @Value("${igdb.company-logos-endpoint}")
    private String companyLogosEndpoint;

    // Access token and expiration for IGDB API
    private String accessToken;

    private Instant tokenExpiration;

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
     * Processes an individual game.
     *
     * @param game        The IgdbGameDto object containing game data.
     * @param accessToken The access token for the IGDB API.
     */
    @Transactional
    public void processGame(IgdbGameDto game, String accessToken) {
        try {
            logger.info("Processing game: {}", game.getName());
            // Set the access token
            this.accessToken = accessToken;
            // Initialize game entity
            Game gameEntity = new Game();

            // Check if game already exists in the database
            Game gameFound = gameRepository.findByIGDBId(game.getId());

            // Set IGDB ID and updated_at timestamp
            gameEntity.setIGDBId(game.getId());
            gameEntity.setUpdatedAt(Instant.ofEpochSecond(game.getUpdatedAt()));

            if (gameFound != null) {
                logger.info("Game already exists: {}", game.getName());
                gameEntity.setId(gameFound.getId());

                // Skip if the game hasn't been updated
                if (!gameFound.getUpdatedAt().isBefore(gameEntity.getUpdatedAt())) {
                    logger.info("Game has not changed: {}", game.getName());
                    return;
                }
            }

            // Set basic game information
            gameEntity.setTitle(game.getName());
            gameEntity.setSummary(game.getSummary());
            gameEntity.setStoryline(game.getStoryline());
            gameEntity.setRating(game.getTotalRating());
            gameEntity.setRatingCount(game.getTotalRatingCount());

            gameEntity = gameRepository.save(gameEntity);

            // Fetch and set platforms
            List<Platform> platformsToAdd = new ArrayList<>();
            if (game.getPlatforms() != null) {
                logger.debug("Game '{}' has platforms: {}", game.getName(), game.getPlatforms());
                for (Integer platformIgdbId : game.getPlatforms()) {
                    Platform platform = SearchForPlatforms(platformIgdbId, gameEntity);
                    if (platform != null) {
                        platformsToAdd.add(platform);
                    }
                }
            } else {
                logger.warn("Game '{}' has no platforms.", game.getName());
            }
            gameEntity.setPlatforms(platformsToAdd);

            // Fetch and set cover URL
            if (game.getCover() != null) {
                logger.debug("Game '{}' has cover ID: {}", game.getName(), game.getCover());
                gameEntity.setCover(GetCoverUrl(game.getCover()));
            } else {
                logger.warn("Game '{}' has no cover.", game.getName());
            }

            // Fetch and set release dates
            List<ReleaseDate> releaseDatesToAdd = new ArrayList<>();
            if (game.getReleaseDates() != null) {
                logger.debug("Game '{}' has release dates: {}", game.getName(), game.getReleaseDates());
                for (Integer releaseDateId : game.getReleaseDates()) {
                    ReleaseDate releaseDateEntity = SearchForReleaseDates(releaseDateId, gameEntity);
                    if (releaseDateEntity != null) {
                        releaseDatesToAdd.add(releaseDateEntity);
                    }
                }
            } else {
                logger.warn("Game '{}' has no release dates.", game.getName());
            }
            gameEntity.setReleaseDates(releaseDatesToAdd);

            // Fetch and set genres
            List<Genre> genresToAdd = new ArrayList<>();
            if (game.getGenres() != null) {
                logger.debug("Game '{}' has genres: {}", game.getName(), game.getGenres());
                for (Integer genreId : game.getGenres()) {
                    Genre genreEntity = SearchForGenres(genreId, gameEntity);
                    if (genreEntity != null) {
                        genresToAdd.add(genreEntity);
                    }
                }
            } else {
                logger.warn("Game '{}' has no genres.", game.getName());
            }
            gameEntity.setGenres(genresToAdd);

            // Fetch and set age ratings
            List<AgeRating> ageRatingsToAdd = new ArrayList<>();
            if (game.getAgeRatings() != null) {
                logger.debug("Game '{}' has age ratings: {}", game.getName(), game.getAgeRatings());
                for (Integer ageRatingId : game.getAgeRatings()) {
                    AgeRating ageRatingEntity = SearchForAgeRatings(ageRatingId);
                    if (ageRatingEntity != null) {
                        ageRatingsToAdd.add(ageRatingEntity);
                    }
                }
            } else {
                logger.warn("Game '{}' has no age ratings.", game.getName());
            }
            gameEntity.setAgeRatings(ageRatingsToAdd);

            // Fetch and set player perspectives
            List<PlayerPerspective> playerPerspectivesToAdd = new ArrayList<>();
            if (game.getPlayerPerspectives() != null) {
                logger.debug("Game '{}' has player perspectives: {}", game.getName(), game.getPlayerPerspectives());
                for (Integer playerPerspectiveId : game.getPlayerPerspectives()) {
                    PlayerPerspective playerPerspectiveEntity = SearchForPlayerPerspectives(playerPerspectiveId, gameEntity);
                    if (playerPerspectiveEntity != null) {
                        playerPerspectivesToAdd.add(playerPerspectiveEntity);
                    }
                }
            } else {
                logger.warn("Game '{}' has no player perspectives.", game.getName());
            }
            gameEntity.setPlayerPerspectives(playerPerspectivesToAdd);

            // Fetch and set themes
            List<GameTheme> themesToAdd = new ArrayList<>();
            if (game.getThemes() != null) {
                logger.debug("Game '{}' has themes: {}", game.getName(), game.getThemes());
                for (Integer themeId : game.getThemes()) {
                    GameTheme gameTheme = SearchForThemes(themeId, gameEntity);
                    if (gameTheme != null) {
                        themesToAdd.add(gameTheme);
                    }
                }
            } else {
                logger.warn("Game '{}' has no themes.", game.getName());
            }
            gameEntity.setThemes(themesToAdd);

            // Fetch and set game modes
            List<GameMode> gameModesToAdd = new ArrayList<>();
            if (game.getGameModes() != null) {
                logger.debug("Game '{}' has game modes: {}", game.getName(), game.getGameModes());
                for (Integer gameModeId : game.getGameModes()) {
                    GameMode gameModeEntity = SearchForGameModes(gameModeId, gameEntity);
                    if (gameModeEntity != null) {
                        gameModesToAdd.add(gameModeEntity);
                    }
                }
            } else {
                logger.warn("Game '{}' has no game modes.", game.getName());
            }
            gameEntity.setGameModes(gameModesToAdd);

            // Fetch and set franchises
            List<Franchise> franchisesToAdd = new ArrayList<>();
            if (game.getFranchises() != null) {
                logger.debug("Game '{}' has franchises: {}", game.getName(), game.getFranchises());
                for (Integer franchiseId : game.getFranchises()) {
                    Franchise franchiseEntity = SearchForFranchises(franchiseId, gameEntity);
                    if (franchiseEntity != null) {
                        franchisesToAdd.add(franchiseEntity);
                    }
                }
            } else {
                logger.warn("Game '{}' has no franchises.", game.getName());
            }
            gameEntity.setFranchises(franchisesToAdd);

            // Fetch and set language supports
            List<LanguageSupport> languageSupportsToAdd = new ArrayList<>();
            if (game.getLanguageSupports() != null) {
                logger.debug("Game '{}' has language supports: {}", game.getName(), game.getLanguageSupports());
                for (Integer languageSupportId : game.getLanguageSupports()) {
                    LanguageSupport languageSupportEntity = SearchForLanguageSupport(languageSupportId, gameEntity);
                    if (languageSupportEntity != null) {
                        languageSupportsToAdd.add(languageSupportEntity);
                    }
                }
            } else {
                logger.warn("Game '{}' has no language supports.", game.getName());
            }
            gameEntity.setLanguageSupports(languageSupportsToAdd);

            // Fetch and set involved companies
            List<CompanyGame> companiesToAdd = new ArrayList<>();
            if (game.getInvolvedCompanies() != null) {
                logger.debug("Game '{}' has involved companies: {}", game.getName(), game.getInvolvedCompanies());
                for (Integer companyId : game.getInvolvedCompanies()) {
                    CompanyGame companyGame = SearchForInvolvedCompanies(companyId, gameEntity);
                    if (companyGame != null) {
                        companiesToAdd.add(companyGame);
                    }
                }
            } else {
                logger.warn("Game '{}' has no involved companies.", game.getName());
            }
            gameEntity.setInvolvedCompanies(companiesToAdd);

            // Fetch and set similar games
            List<Game> similarGames = new ArrayList<>();
            if (game.getSimilarGames() != null) {
                logger.debug("Game '{}' has similar games: {}", game.getName(), game.getSimilarGames());
                for (Integer similarGameId : game.getSimilarGames()) {
                    Game similarGameEntity = gameRepository.findByIGDBId(similarGameId);
                    if (similarGameEntity != null) {
                        similarGames.add(similarGameEntity);
                        if (!similarGameEntity.getSimilarGames().contains(gameEntity)) {
                            similarGameEntity.addSimilarGame(gameEntity);
                            gameRepository.save(similarGameEntity);
                        }
                        break;
                    }
                }
            } else {
                logger.warn("Game '{}' has no similar games.", game.getName());
            }
            gameEntity.setSimilarGames(similarGames);

            gameRepository.save(gameEntity);

            // Save the game entity (cascading will save associated entities)
            logger.info("Game '{}' processed and saved successfully.", game.getName());
        } catch (Exception e) {
            logger.error("Error while processing game: {}", game.getName(), e);
            // Depending on your requirements, you may choose to continue processing other games
            // or rethrow the exception to halt the process.
            // For now, we'll continue with the next game.
        }
    }

    /**
     * Searches for a platform by its IGDB ID.
     *
     * @param platformIgdbId The IGDB ID of the platform.
     * @return The Platform entity.
     */
    private Platform SearchForPlatforms(Integer platformIgdbId, Game game) {
        logger.info("Searching for platform with ID: {}", platformIgdbId);

        // Check if the platform already exists in the database
        Platform platformAtDb = platformRepository.findByIgdbId(platformIgdbId);
        if (platformAtDb != null) {
            logger.debug("Platform with ID '{}' found in database.", platformIgdbId);
            return platformAtDb;
        }

        // Fetch platform data from IGDB API
        ResponseEntity<String> response = SendRequest(platformIgdbId, platformEndpoint);

        if (!response.getStatusCode().is2xxSuccessful()) {
            logger.error("Failed to obtain platform with ID: {}", platformIgdbId);
            throw new RuntimeException("Failed to obtain platform");
        }

        try {
            String jsonResponse = response.getBody();
            logger.debug("Platform response: {}", jsonResponse);
            List<Platform> platforms = objectMapper.readValue(jsonResponse, new TypeReference<List<Platform>>() {
            });

            if (platforms.isEmpty()) {
                logger.warn("No platform data found for ID: {}", platformIgdbId);
                return null;
            }

            Platform platform = platforms.get(0);
            platform.setId(null);
            platform.setIgdbId(platformIgdbId);
            platform.addGame(game);


            // Save and return the platform
            platform = platformRepository.save(platform);
            logger.info("Platform '{}' saved to database.", platform.getName());
            return platform;
        } catch (Exception e) {
            logger.error("Error while parsing the platform data for ID: {}", platformIgdbId, e);
            throw new RuntimeException("Error while parsing the platform data", e);
        }
    }

    // Include similar methods for other entities:
    // - SearchForGenres
    private Genre SearchForGenres(Integer genreId, Game game) {
        logger.info("Searching for genre with ID: {}", genreId);

        // Check if genre already exists
        Genre genreAtDb = genreRepository.findByIgdbId(genreId);
        if (genreAtDb != null) {
            logger.debug("Genre with ID '{}' found in database.", genreId);
            return genreAtDb;
        }

        // Fetch genre data from IGDB API
        ResponseEntity<String> response = SendRequest(genreId, genresEndpoint);

        if (!response.getStatusCode().is2xxSuccessful()) {
            logger.error("Failed to obtain genre with ID: {}", genreId);
            throw new RuntimeException("Failed to obtain genre");
        }

        try {
            String jsonResponse = response.getBody();
            logger.debug("Genre response: {}", jsonResponse);
            List<Genre> genres = objectMapper.readValue(jsonResponse, new TypeReference<List<Genre>>() {
            });

            if (genres.isEmpty()) {
                logger.warn("No genre data found for ID: {}", genreId);
                return null;
            }

            Genre genreFound = genres.get(0);
            genreFound.setId(null);
            genreFound.setIgdbId(genreId);
            genreFound.addGame(game);

            // Save and return the genre
            genreFound = genreRepository.save(genreFound);
            logger.info("Genre '{}' saved to database.", genreFound.getName());
            return genreFound;
        } catch (Exception e) {
            logger.error("Error while parsing the genre data for ID: {}", genreId, e);
            throw new RuntimeException("Error while parsing the genre data", e);
        }
    }

    // - SearchForGameModes
    private GameMode SearchForGameModes(Integer gameModeId, Game game) {
        logger.info("Searching for game mode with ID: {}", gameModeId);

        // Check if game mode already exists
        GameMode gameModeAtDb = gameModeRepository.findByIgdbId(gameModeId);
        if (gameModeAtDb != null) {
            logger.debug("Game mode with ID '{}' found in database.", gameModeId);
            return gameModeAtDb;
        }

        // Fetch game mode data from IGDB API
        ResponseEntity<String> response = SendRequest(gameModeId, gameModesEndpoint);

        if (!response.getStatusCode().is2xxSuccessful()) {
            logger.error("Failed to obtain game mode with ID: {}", gameModeId);
            throw new RuntimeException("Failed to obtain game mode");
        }

        try {
            String jsonResponse = response.getBody();
            logger.debug("Game mode response: {}", jsonResponse);
            List<GameMode> gameModes = objectMapper.readValue(jsonResponse, new TypeReference<List<GameMode>>() {
            });

            if (gameModes.isEmpty()) {
                logger.warn("No game mode data found for ID: {}", gameModeId);
                return null;
            }

            GameMode gameModeFound = gameModes.get(0);
            gameModeFound.setId(null);
            gameModeFound.setIgdbId(gameModeId);
            gameModeFound.addGame(game);


            // Save and return the game mode
            gameModeFound = gameModeRepository.save(gameModeFound);
            logger.info("Game mode '{}' saved to database.", gameModeFound.getName());
            return gameModeFound;
        } catch (Exception e) {
            logger.error("Error while parsing the game mode data for ID: {}", gameModeId, e);
            throw new RuntimeException("Error while parsing the game mode data", e);
        }
    }

    // - SearchForThemes
    private GameTheme SearchForThemes(Integer themeId, Game game) {
        logger.info("Searching for theme with ID: {}", themeId);

        // Check if theme already exists
        GameTheme themeAtDb = themeRepository.findByIgdbId(themeId);
        if (themeAtDb != null) {
            logger.debug("Theme with ID '{}' found in database.", themeId);
            return themeAtDb;
        }

        // Fetch theme data from IGDB API
        ResponseEntity<String> response = SendRequest(themeId, gameThemesEndpoint);

        if (!response.getStatusCode().is2xxSuccessful()) {
            logger.error("Failed to obtain theme with ID: {}", themeId);
            throw new RuntimeException("Failed to obtain theme");
        }

        try {
            String jsonResponse = response.getBody();
            logger.debug("Theme response: {}", jsonResponse);
            List<GameTheme> themes = objectMapper.readValue(jsonResponse, new TypeReference<List<GameTheme>>() {
            });

            if (themes.isEmpty()) {
                logger.warn("No theme data found for ID: {}", themeId);
                return null;
            }

            GameTheme themeFound = themes.get(0);
            themeFound.setId(null);
            themeFound.setIgdbId(themeId);
            themeFound.addGame(game);

            // Save and return the theme
            themeFound = themeRepository.save(themeFound);
            logger.info("Theme '{}' saved to database.", themeFound.getName());
            return themeFound;
        } catch (Exception e) {
            logger.error("Error while parsing the theme data for ID: {}", themeId, e);
            throw new RuntimeException("Error while parsing the theme data", e);
        }
    }

    // - SearchForPlayerPerspectives
    private PlayerPerspective SearchForPlayerPerspectives(Integer playerPerspectiveId, Game game) {
        logger.info("Searching for player perspective with ID: {}", playerPerspectiveId);

        // Check if player perspective already exists
        PlayerPerspective playerPerspectiveAtDb = playerPerspectiveRepository.findByIgdbId(playerPerspectiveId);
        if (playerPerspectiveAtDb != null) {
            logger.debug("Player perspective with ID '{}' found in database.", playerPerspectiveId);
            return playerPerspectiveAtDb;
        }

        // Fetch player perspective data from IGDB API
        ResponseEntity<String> response = SendRequest(playerPerspectiveId, playerPerspectivesEndpoint);

        if (!response.getStatusCode().is2xxSuccessful()) {
            logger.error("Failed to obtain player perspective with ID: {}", playerPerspectiveId);
            throw new RuntimeException("Failed to obtain player perspective");
        }

        try {
            String jsonResponse = response.getBody();
            logger.debug("Player perspective response: {}", jsonResponse);
            List<PlayerPerspective> playerPerspectives = objectMapper.readValue(jsonResponse, new TypeReference<List<PlayerPerspective>>() {
            });

            if (playerPerspectives.isEmpty()) {
                logger.warn("No player perspective data found for ID: {}", playerPerspectiveId);
                return null;
            }

            PlayerPerspective playerPerspectiveFound = playerPerspectives.get(0);
            playerPerspectiveFound.setId(null);
            playerPerspectiveFound.setIgdbId(playerPerspectiveId);
            playerPerspectiveFound.addGame(game);

            // Save and return the player perspective
            playerPerspectiveFound = playerPerspectiveRepository.save(playerPerspectiveFound);
            logger.info("Player perspective '{}' saved to database.", playerPerspectiveFound.getName());
            return playerPerspectiveFound;
        } catch (Exception e) {
            logger.error("Error while parsing the player perspective data for ID: {}", playerPerspectiveId, e);
            throw new RuntimeException("Error while parsing the player perspective data", e);
        }
    }

    // - SearchForAgeRatings
    private AgeRating SearchForAgeRatings(Integer ageRatingId) {
        logger.info("Searching for age rating with ID: {}", ageRatingId);

        // Check if age rating already exists
        AgeRating ageRatingAtDb = ageRatingRepository.findByIgdbId(ageRatingId);
        if (ageRatingAtDb != null) {
            logger.debug("Age rating with ID '{}' found in database.", ageRatingId);
            return ageRatingAtDb;
        }

        // Fetch age rating data from IGDB API
        ResponseEntity<String> response = SendRequest(ageRatingId, ageRatingsEndpoint);

        if (!response.getStatusCode().is2xxSuccessful()) {
            logger.error("Failed to obtain age rating with ID: {}", ageRatingId);
            throw new RuntimeException("Failed to obtain age rating");
        }

        try {
            String jsonResponse = response.getBody();
            logger.debug("Age rating response: {}", jsonResponse);
            List<AgeRating> ageRatings = objectMapper.readValue(jsonResponse, new TypeReference<List<AgeRating>>() {
            });

            if (ageRatings.isEmpty()) {
                logger.warn("No age rating data found for ID: {}", ageRatingId);
                return null;
            }

            AgeRating ageRatingFound = ageRatings.get(0);
            ageRatingFound.setId(null);
            ageRatingFound.setIgdbId(ageRatingId);

            // Save and return the age rating
            ageRatingFound = ageRatingRepository.save(ageRatingFound);
            logger.info("Age rating '{}' saved to database.", ageRatingFound.getDescription());
            return ageRatingFound;
        } catch (Exception e) {
            logger.error("Error while parsing the age rating data for ID: {}", ageRatingId, e);
            throw new RuntimeException("Error while parsing the age rating data", e);
        }
    }

    // - SearchForFranchises
    private Franchise SearchForFranchises(Integer franchiseId, Game gameEntity) {
        logger.info("Searching for franchise with ID: {}", franchiseId);

        // Check if franchise already exists
        Franchise franchiseAtDb = franchiseRepository.findByIgdbId(franchiseId);
        if (franchiseAtDb != null) {
            logger.debug("Franchise with ID '{}' found in database.", franchiseId);
            franchiseAtDb.addGame(gameEntity);
            return franchiseAtDb;
        }

        // Fetch franchise data from IGDB API
        ResponseEntity<String> response = SendRequest(franchiseId, franchisesEndpoint);

        if (!response.getStatusCode().is2xxSuccessful()) {
            logger.error("Failed to obtain franchise with ID: {}", franchiseId);
            throw new RuntimeException("Failed to obtain franchise");
        }

        try {
            String jsonResponse = response.getBody();
            logger.debug("Franchise response: {}", jsonResponse);
            List<Franchise> franchises = objectMapper.readValue(jsonResponse, new TypeReference<List<Franchise>>() {
            });

            if (franchises.isEmpty()) {
                logger.warn("No franchise data found for ID: {}", franchiseId);
                return null;
            }

            Franchise franchiseFound = franchises.get(0);
            franchiseFound.setId(null);
            franchiseFound.setIgdbId(franchiseId);
            franchiseFound.addGame(gameEntity);

            // Save and return the franchise
            franchiseFound = franchiseRepository.save(franchiseFound);
            logger.info("Franchise '{}' saved to database.", franchiseFound.getName());
            return franchiseFound;
        } catch (Exception e) {
            logger.error("Error while parsing the franchise data for ID: {}", franchiseId, e);
            throw new RuntimeException("Error while parsing the franchise data", e);
        }
    }

    // - SearchForLanguageSupport
    private LanguageSupport SearchForLanguageSupport(Integer languageSupportId, Game game) {
        logger.info("Searching for language support with ID: {}", languageSupportId);

        // Check if language support already exists
        LanguageSupport languageSupportAtDb = languageSupportRepository.findByIgdbId(languageSupportId);

        // Fetch language support data from IGDB API
        ResponseEntity<String> response = SendRequest(languageSupportId, languageSupportEndpoint);

        if (!response.getStatusCode().is2xxSuccessful()) {
            logger.error("Failed to obtain language support with ID: {}", languageSupportId);
            throw new RuntimeException("Failed to obtain language support");
        }

        try {
            String jsonResponse = response.getBody();
            logger.warn("LanguageSupport response: {}", jsonResponse);
            Integer idToSet = null;
            List<IgdbGameLanguageSupportDto> languageSupports = objectMapper.readValue(jsonResponse,
                    new TypeReference<List<IgdbGameLanguageSupportDto>>() {});

            if (languageSupports.isEmpty()) {
                logger.warn("No language support data found for ID: {}", languageSupportId);
                return null;
            }

            IgdbGameLanguageSupportDto languageSupportFound = languageSupports.get(0);

            // Check if language support already exists
            if (languageSupportAtDb != null) {
                idToSet = languageSupportAtDb.getId();
                if (languageSupportAtDb.getUpdatedAt() == null) {
                    logger.warn("Language support '{}' has no updated_at timestamp.", languageSupportAtDb.
                            getLanguageSupportType().getName()
                            + " for ID: " + languageSupportId
                            + " Language: " + languageSupportAtDb.getLanguage().getName());

                    languageSupportAtDb.setUpdatedAt(0);
                }
                if (languageSupportFound.getUpdatedAt() <= languageSupportAtDb.getUpdatedAt()) {
                    logger.info("Language support '{}' has not been updated.", languageSupportFound.getType());
                    return languageSupportAtDb;
                }
            }

            // Check if 'type' is null
            if (languageSupportFound.getType() == null) {
                logger.warn("Language support type is null for ID: {}", languageSupportId);
                return null;
            } else {
                LanguageSupportType supportType = LanguageSupportType.fromId(languageSupportFound.getType());
                if (supportType == null) {
                    logger.warn("No LanguageSupportType found for type ID: {}", languageSupportFound.getType());
                    return null;
                }
                // Create and populate LanguageSupport entity
                LanguageSupport newLanguageSupport = new LanguageSupport();
                newLanguageSupport.setId(idToSet);
                newLanguageSupport.setLanguageSupportType(supportType);
                newLanguageSupport.setLanguage(SearchForLanguage(languageSupportFound.getLanguage()));
                newLanguageSupport.setGame(game);
                newLanguageSupport.setIgdbId(languageSupportId);

                // Save and return the language support
                newLanguageSupport = languageSupportRepository.save(newLanguageSupport);
                logger.info("LanguageSupport '{}' saved to database.", newLanguageSupport.getLanguageSupportType().getName());
                return newLanguageSupport;
            }
        } catch (Exception e) {
            logger.error("Error while parsing the language support data for ID: {}", languageSupportId, e);
            throw new RuntimeException("Error while parsing the language support data", e);
        }
    }


    // - SearchForLanguage
    private Language SearchForLanguage(Integer languageId) {
        logger.info("Searching for language with ID: {}", languageId);

        // Check if the language already exists
        Language languageAtDb = languageRepository.findByIgdbId(languageId);
        if (languageAtDb != null) {
            logger.debug("Language with ID '{}' found in database.", languageId);
            return languageAtDb;
        }

        // Fetch language data from IGDB API
        ResponseEntity<String> response = SendRequest(languageId, languagesEndpoint);

        if (!response.getStatusCode().is2xxSuccessful()) {
            logger.error("Failed to obtain language with ID: {}", languageId);
            throw new RuntimeException("Failed to obtain language");
        }

        try {
            String jsonResponse = response.getBody();
            logger.debug("Language response: {}", jsonResponse);
            List<IgdbLanguageDto> languages = objectMapper.readValue(jsonResponse,
                    new TypeReference<List<IgdbLanguageDto>>() {
            });

            if (languages.isEmpty()) {
                logger.warn("No language data found for ID: {}", languageId);
                return null;
            }

            IgdbLanguageDto languageFound = languages.get(0);

            // Create and populate Language entity
            Language newLanguage = new Language();
            newLanguage.setName(languageFound.getName());
            newLanguage.setLocale(languageFound.getLocale());
            newLanguage.setNativeName(languageFound.getNativeName());
            newLanguage.setIgdbId(languageId);

            // Save and return the language
            newLanguage = languageRepository.save(newLanguage);
            logger.info("Language '{}' saved to database.", newLanguage.getName());
            return newLanguage;
        } catch (Exception e) {
            logger.error("Error while parsing the language data for ID: {}", languageId, e);
            throw new RuntimeException("Error while parsing the language data", e);
        }
    }

    // - SearchForInvolvedCompanies
    private CompanyGame SearchForInvolvedCompanies(Integer companyId, Game game) {
        logger.info("Searching for involved company with ID: {}", companyId);

        // Check if company game already exists
        CompanyGame companyGameAtDb = companyGameRepository.findByIgdbId(companyId);
        if (companyGameAtDb != null) {
            logger.debug("Involved company with ID '{}' found in database.", companyId);
            return companyGameAtDb;
        }

        // Fetch involved company data from IGDB API
        ResponseEntity<String> response = SendRequest(companyId, involvedCompaniesEndpoint);

        if (!response.getStatusCode().is2xxSuccessful()) {
            logger.error("Failed to obtain involved company with ID: {}", companyId);
            throw new RuntimeException("Failed to obtain involved company");
        }

        try {
            String jsonResponse = response.getBody();
            logger.debug("Involved company response: {}", jsonResponse);
            List<IgbdCompanyGameDto> companies = objectMapper.readValue(jsonResponse, new TypeReference<List<IgbdCompanyGameDto>>() {
            });

            if (companies.isEmpty()) {
                logger.warn("No involved company data found for ID: {}", companyId);
                return null;
            }

            IgbdCompanyGameDto companyFound = companies.get(0);

            // Create and populate CompanyGame entity
            CompanyGame newCompanyGame = new CompanyGame();
            newCompanyGame.setIgdbId(companyId);
            newCompanyGame.setGame(game);
            newCompanyGame.setDeveloper(companyFound.isDeveloper());
            newCompanyGame.setPublisher(companyFound.isPublisher());
            newCompanyGame.setSupporter(companyFound.isSupporter());
            newCompanyGame.setPorter(companyFound.isPorter());

            Company company = SearchForCompanies(companyFound.getCompany(), newCompanyGame);
            newCompanyGame.setCompany(company);

            // Save and return the company game
            assert company != null;
            newCompanyGame = companyGameRepository.save(newCompanyGame);
            logger.info("Involved company '{}' saved to database.", company.getName());
            return newCompanyGame;
        } catch (Exception e) {
            logger.error("Error while parsing the involved company data for ID: {}", companyId, e);
            throw new RuntimeException("Error while parsing the involved company data", e);
        }
    }

    // - SearchForCompanies
    private Company SearchForCompanies(Integer companyId, CompanyGame companyGame) {
        logger.info("Searching for company with ID: {}", companyId);

        // Check if company already exists
        Company companyAtDb = companyRepository.findByIgdbId(companyId);
        if (companyAtDb != null) {
            logger.debug("Company with ID '{}' found in database.", companyId);
            companyAtDb.addCompanyGame(companyGame);
            return companyAtDb;
        }

        // Fetch company data from IGDB API
        ResponseEntity<String> response = SendRequest(companyId, companiesEndpoint);

        if (!response.getStatusCode().is2xxSuccessful()) {
            logger.error("Failed to obtain company with ID: {}", companyId);
            throw new RuntimeException("Failed to obtain company");
        }

        try {
            String jsonResponse = response.getBody();
            logger.debug("Company response: {}", jsonResponse);
            List<IgbdCompanyDto> companies = objectMapper.readValue(jsonResponse, new TypeReference<List<IgbdCompanyDto>>() {
            });

            if (companies.isEmpty()) {
                logger.warn("No company data found for ID: {}", companyId);
                return null;
            }

            IgbdCompanyDto companyFound = companies.get(0);

            // Create and populate Company entity
            Company newCompany = new Company();
            newCompany.setName(companyFound.getName());
            newCompany.setIgdbId(companyId);
            newCompany.setDescription(companyFound.getDescription());
            newCompany.setLogoUrl(SearchForCompanyLogo(companyFound.getLogo()));
            newCompany.addCompanyGame(companyGame);

            // Save and return the company
            newCompany = companyRepository.save(newCompany);
            logger.info("Company '{}' saved to database.", newCompany.getName());
            return newCompany;
        } catch (Exception e) {
            logger.error("Error while parsing the company data for ID: {}", companyId, e);
            throw new RuntimeException("Error while parsing the company data", e);
        }
    }

    // - SearchForCompanyLogo
    private String SearchForCompanyLogo(Integer logoId) {
        if (logoId == null) {
            logger.warn("No logo ID provided for company");
            return null;
        }

        logger.info("Searching for company logo with ID: {}", logoId);

        ResponseEntity<String> response = SendRequest(logoId, companyLogosEndpoint);

        if (!response.getStatusCode().is2xxSuccessful()) {
            logger.error("Failed to obtain company logo with ID: {}", logoId);
            throw new RuntimeException("Failed to obtain company logo");
        }

        try {
            String jsonResponse = response.getBody();
            logger.debug("Company logo response: {}", jsonResponse);
            List<IgdbCompanyLogoDto> logos = objectMapper.readValue(jsonResponse, new TypeReference<List<IgdbCompanyLogoDto>>() {
            });

            if (logos.isEmpty()) {
                logger.warn("No company logo data found for ID: {}", logoId);
                return null;
            }

            String logoUrl = logos.get(0).getUrl();
            logger.info("Company logo URL obtained: {}", logoUrl);

            return logoUrl;
        } catch (Exception e) {
            logger.error("Error while parsing the company logo data for ID: {}", logoId, e);
            throw new RuntimeException("Error while parsing the company logo data", e);
        }
    }

    // - SearchForRegions
    private Region SearchForRegions(Integer regionIgdbId) {
        logger.info("Searching for region with ID: {}", regionIgdbId);

        // Check if region already exists in the database
        Region regionAtDb = regionRepository.findByIgdbId(regionIgdbId);
        if (regionAtDb != null) {
            logger.debug("Region with ID '{}' found in database.", regionIgdbId);
            return regionAtDb;
        }

        // Fetch region data from IGDB API
        ResponseEntity<String> response = SendRequest(regionIgdbId, regionsEndpoint);

        if (!response.getStatusCode().is2xxSuccessful()) {
            logger.error("Failed to obtain region with ID: {}", regionIgdbId);
            throw new RuntimeException("Failed to obtain region");
        }

        try {
            String jsonResponse = response.getBody();
            logger.debug("Region response: {}", jsonResponse);
            List<Region> regions = objectMapper.readValue(jsonResponse, new TypeReference<List<Region>>() {
            });

            if (regions.isEmpty()) {
                logger.warn("No region data found for ID: {}", regionIgdbId);
                return null;
            }

            Region region = regions.get(0);
            region.setId(null);
            region.setIgdbId(regionIgdbId);

            // Save and return the region
            region = regionRepository.save(region);
            logger.info("Region '{}' saved to database.", region.getName());
            return region;
        } catch (Exception e) {
            logger.error("Error while parsing the region data for ID: {}", regionIgdbId, e);
            throw new RuntimeException("Error while parsing the region data", e);
        }
    }

    // - SearchForReleaseDates
    private ReleaseDate SearchForReleaseDates(Integer releaseDateId, Game game) {
        logger.info("Searching for release date with ID: {}", releaseDateId);

        // Check if release date already exists
        ReleaseDate releaseDateAtDb = releaseDateRepository.findByIgdbId(releaseDateId);
        if (releaseDateAtDb != null) {
            logger.debug("Release date with ID '{}' found in database.", releaseDateId);
            return releaseDateAtDb;
        }

        // Fetch release date data from IGDB API
        ResponseEntity<String> response = SendRequest(releaseDateId, releaseDatesEndpoint, "*");

        if (!response.getStatusCode().is2xxSuccessful()) {
            logger.error("Failed to obtain release date with ID: {}", releaseDateId);
            throw new RuntimeException("Failed to obtain release date");
        }

        try {
            String jsonResponse = response.getBody();
            logger.debug("Release date response: {}", jsonResponse);
            List<ReleaseDateIgdbDto> releaseDates = objectMapper.readValue(jsonResponse, new TypeReference<List<ReleaseDateIgdbDto>>() {
            });

            if (releaseDates.isEmpty()) {
                logger.warn("No release date data found for ID: {}", releaseDateId);
                return null;
            }

            ReleaseDateIgdbDto foundIgdbReleaseDate = releaseDates.get(0);

            if (foundIgdbReleaseDate.getDate() == null || foundIgdbReleaseDate.getPlatform() == null || foundIgdbReleaseDate.getRegion() == null) {
                logger.warn("Incomplete release date data for ID: {}", releaseDateId);
                return null;
            }

            // Create and populate ReleaseDate entity
            ReleaseDate foundReleaseDate = new ReleaseDate();
            foundReleaseDate.setIgdbId(releaseDateId);
            foundReleaseDate.setDate(foundIgdbReleaseDate.getDate());
            foundReleaseDate.setGame(game);

            Platform platform = SearchForPlatforms(foundIgdbReleaseDate.getPlatform(), game);
            foundReleaseDate.setPlatform(platform);

            Region region = SearchForRegions(foundIgdbReleaseDate.getRegion());
            if (region != null) {
                logger.debug("Region found: {}", region.getName());
                foundReleaseDate.setRegion(region);
            }

            // Save and return the release date
            foundReleaseDate = releaseDateRepository.save(foundReleaseDate);
            logger.info("Release date '{}' saved to database.", foundReleaseDate.getDate()) ;
            return foundReleaseDate;
        } catch (Exception e) {
            logger.error("Error while parsing the release date data for ID: {}", releaseDateId, e);
            throw new RuntimeException("Error while parsing the release date data", e);
        }
    }

    // - GetCoverUrl
    private String GetCoverUrl(Integer coverId) {
        logger.info("Getting cover URL with ID: {}", coverId);

        String url = etlUrl + coverEndpoint;
        ResponseEntity<String> response = SendRequest(coverId, coverEndpoint, "url");

        if (!response.getStatusCode().is2xxSuccessful()) {
            logger.error("Failed to obtain cover with ID: {}", coverId);
            throw new RuntimeException("Failed to obtain cover");
        }

        try {
            String jsonResponse = response.getBody();
            logger.debug("Cover response: {}", jsonResponse);
            List<CoverIgdbDto> foundCovers = objectMapper.readValue(jsonResponse, new TypeReference<List<CoverIgdbDto>>() {
            });

            if (foundCovers.isEmpty()) {
                logger.warn("No cover data found for ID: {}", coverId);
                return null;
            }

            String coverUrl = foundCovers.get(0).getUrl();
            logger.info("Cover URL obtained: {}", coverUrl);

            return coverUrl;
        } catch (Exception e) {
            logger.error("Error while parsing the cover data for ID: {}", coverId, e);
            throw new RuntimeException("Error while parsing the cover data", e);
        }
    }

    /**
     * Sends a request to the IGDB API.
     *
     * @param igdbId   The IGDB ID of the entity.
     * @param endpoint The API endpoint.
     * @param fields   Optional fields to retrieve.
     * @return The ResponseEntity containing the API response.
     */
    private ResponseEntity<String> SendRequest(Integer igdbId, String endpoint, String... fields) {
        String url = etlUrl + endpoint;
        logger.info("Sending request to: {}", url);

        String fieldString = fields.length > 0 ? "fields " + String.join(", ", fields) + ";" : "fields *;";
        String requestBody = fieldString + " where id = " + igdbId + ";";

        HttpHeaders headers = getHttpHeaders(); // Use headers from IgdbService
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
        logger.info("Request sent successfully");

        return response;
    }
}
