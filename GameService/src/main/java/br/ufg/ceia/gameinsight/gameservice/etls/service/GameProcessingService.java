package br.ufg.ceia.gameinsight.gameservice.etls.service;

// Import statements...

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
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.relational.core.sql.In;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Service class for processing individual games with batch processing.
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
     * Processes an individual game with batch fetching of related entities.
     *
     * @param game        The IgdbGameDto object containing game data.
     * @param accessToken The access token for the IGDB API.
     */
    @Transactional
    public void processGame(IgdbGameDto game, String accessToken) {
        try {
            logger.info("Processing game: {}", game.getName());
            this.accessToken = accessToken;

            // Retrieve or create the Game entity
            Game gameEntity = gameRepository.findByIgdbId(game.getId());
            if (gameEntity == null) {
                gameEntity = new Game();
                gameEntity.setIgdbId(game.getId());
            }

            // Check if the game entity in the database is outdated
            Instant updatedAt = Instant.ofEpochSecond(game.getUpdatedAt());
            if (gameEntity.getUpdatedAt() != null ){
                if (updatedAt.isBefore(gameEntity.getUpdatedAt()) ||
                        updatedAt.equals(gameEntity.getUpdatedAt())) {
                    logger.info("Game '{}' is up to date.", game.getName());
                    return;
                }
            }

            // Update game fields
            gameEntity.setTitle(game.getName());
            gameEntity.setSummary(game.getSummary());
            gameEntity.setStoryline(game.getStoryline());
            gameEntity.setRating(game.getTotalRating());
            gameEntity.setRatingCount(game.getTotalRatingCount());
            gameEntity.setUpdatedAt(updatedAt);
            gameEntity.setCover(GetCoverUrl(game.getCover()));

            // **Save the Game entity early**
            gameRepository.save(gameEntity);
            logger.info("Game '{}' saved to the database.", game.getName());

            // Collect IGDB IDs
            List<Integer> platformIds = game.getPlatforms() != null ? game.getPlatforms() : new ArrayList<>();
            List<Integer> releaseDateIds = game.getReleaseDates() != null ? game.getReleaseDates() : new ArrayList<>();
            List<Integer> genreIds = game.getGenres() != null ? game.getGenres() : new ArrayList<>();
            List<Integer> ageRatingIds = game.getAgeRatings() != null ? game.getAgeRatings() : new ArrayList<>();
            List<Integer> playerPerspectiveIds = game.getPlayerPerspectives() != null ? game.getPlayerPerspectives() : new ArrayList<>();
            List<Integer> themeIds = game.getThemes() != null ? game.getThemes() : new ArrayList<>();
            List<Integer> gameModeIds = game.getGameModes() != null ? game.getGameModes() : new ArrayList<>();
            List<Integer> franchiseIds = game.getFranchises() != null ? game.getFranchises() : new ArrayList<>();
            List<Integer> languageSupportIds = game.getLanguageSupports() != null ? game.getLanguageSupports() : new ArrayList<>();
            List<Integer> involvedCompanyIds = game.getInvolvedCompanies() != null ? game.getInvolvedCompanies() : new ArrayList<>();

            // Batch fetch and save entities
            Map<Integer, Platform> platforms = processPlatforms(platformIds, gameEntity);
            Map<Integer, Genre> genres = processGenres(genreIds, gameEntity);
            Map<Integer, GameMode> gameModes = processGameModes(gameModeIds, gameEntity);
            Map<Integer, GameTheme> themes = processGameThemes(themeIds, gameEntity);
            Map<Integer, PlayerPerspective> playerPerspectives = processPlayerPerspectives(playerPerspectiveIds, gameEntity);
            Map<Integer, AgeRating> ageRatings = processAgeRatings(ageRatingIds);
            Map<Integer, ReleaseDate> releaseDates = processReleaseDates(releaseDateIds, gameEntity, platforms);
            Map<Integer, Franchise> franchises = processFranchises(franchiseIds, gameEntity);
            Map<Integer, LanguageSupport> languageSupports = processLanguageSupports(languageSupportIds, gameEntity);
            Map<Integer, CompanyGame> companyGames = processCompanyGames(involvedCompanyIds, gameEntity);

            // Process Similar Games
            List<Game> similarGames = processSimilarGames(game.getSimilarGames(), gameEntity);

            // Set all associated entities to the game
            gameEntity.setPlatforms(new ArrayList<>(platforms.values()));
            gameEntity.setGenres(new ArrayList<>(genres.values()));
            gameEntity.setGameModes(new ArrayList<>(gameModes.values()));
            gameEntity.setThemes(new ArrayList<>(themes.values()));
            gameEntity.setPlayerPerspectives(new ArrayList<>(playerPerspectives.values()));
            gameEntity.setAgeRatings(new ArrayList<>(ageRatings.values()));
            gameEntity.setFranchises(new ArrayList<>(franchises.values()));
            gameEntity.setLanguageSupports(new ArrayList<>(languageSupports.values()));
            gameEntity.setInvolvedCompanies(new ArrayList<>(companyGames.values()));
            gameEntity.setReleaseDates(new ArrayList<>(releaseDates.values()));
            gameEntity.setSimilarGames(similarGames);

            // Save the game entity with all associations
            gameRepository.save(gameEntity);

            logger.info("Game '{}' processed and associations set successfully.", game.getName());
        } catch (Exception e) {
            logger.error("Error while processing game: {}", game.getName(), e);
            // Handle exception as per requirements (e.g., continue processing other games or halt)
        }
    }

    // --- Dedicated Batch Processing Methods ---

    private Map<Integer, Platform> processPlatforms(List<Integer> platformIds, Game game) {
        if (platformIds == null || platformIds.isEmpty()) {
            return Collections.emptyMap();
        }

        logger.info("Processing Platforms with IDs: {}", platformIds);

        // Fetch existing platforms from the database
        List<Platform> existingPlatforms = platformRepository.findAllByIgdbIdIn(platformIds);

        // Fetch missing platforms from IGDB API
        List<Platform> fetchedPlatforms = fetchEntitiesFromApi(platformIds, platformEndpoint, Platform.class);

        // Prepare the fetched platforms for saving (set IgdbId and clear local Id)
        fetchedPlatforms.forEach(platform -> {
            platform.setIgdbId(platform.getId());
            platform.addGame(game);
            platform.setId(null);  // Ensure new platforms get a new ID when saved
        });

        // Merge fetched platforms with existing platforms
        List<Platform> finalFetchedPlatforms = fetchedPlatforms;
        existingPlatforms.forEach(platform -> {
            Platform fetchedPlatform = finalFetchedPlatforms.stream()
                    .filter(p -> p.getIgdbId().equals(platform.getIgdbId()))
                    .findFirst()
                    .orElse(null);

            if (fetchedPlatform != null && fetchedPlatform.getUpdatedAt().isAfter(platform.getUpdatedAt())) {
                fetchedPlatform.setId(platform.getId());
            }
        });

        // Save all platforms that need updating or adding
        fetchedPlatforms = platformRepository.saveAll(fetchedPlatforms);

        // Combine the existing platforms with updated ones, removing duplicates
        Map<Integer, Platform> combinedPlatformsMap = new HashMap<>();

        // Add existing platforms to the map (IgdbId as key to ensure uniqueness)
        existingPlatforms.forEach(platform -> combinedPlatformsMap.put(platform.getIgdbId(), platform));

        // Add or update platforms from platformsToSave (this will overwrite any duplicates)
        fetchedPlatforms.forEach(platform -> combinedPlatformsMap.put(platform.getIgdbId(), platform));

        // Return the combined list of platforms without duplicates
        return combinedPlatformsMap;
    }

    private Map<Integer, Genre> processGenres(List<Integer> genreIds, Game game) {
        if (genreIds == null || genreIds.isEmpty()) {
            return Collections.emptyMap();
        }

        logger.info("Processing Genres with IDs: {}", genreIds);

        // Fetch existing Genres from the database
        List<Genre> existingGenres = genreRepository.findAllByIgdbIdIn(genreIds);

        // Fetch missing Genres from IGDB API
        List<Genre> fetchedGenres = fetchEntitiesFromApi(genreIds, genresEndpoint, Genre.class);

        fetchedGenres.forEach(genre -> {
            genre.setIgdbId(genre.getId());
            genre.addGame(game);
            genre.setId(null); // Reset to allow creation of new entity if not present
        });

        // Merge fetched genres with existing genres based on updatedAt field
        List<Genre> finalFetchedGenres = fetchedGenres;
        existingGenres.forEach(genre -> {
            Genre fetchedGenre = finalFetchedGenres.stream()
                    .filter(g -> g.getIgdbId().equals(genre.getIgdbId()))
                    .findFirst()
                    .orElse(null);

            if (fetchedGenre != null && fetchedGenre.getUpdatedAt().isAfter(genre.getUpdatedAt())) {
                fetchedGenre.setId(genre.getId());
            }
        });

        // Save new or updated genres
        fetchedGenres = genreRepository.saveAll(fetchedGenres);

        // Combine existing genres with updated ones, removing duplicates
         Map<Integer, Genre> combinedGenresMap = new HashMap<>();

        // Add existing genres to the map (IgdbId as key to ensure uniqueness)
        existingGenres.forEach(genre -> combinedGenresMap.put(genre.getIgdbId(), genre));

        // Add or update genres from fetchedGenres (this will overwrite any duplicates)
        fetchedGenres.forEach(genre -> combinedGenresMap.put(genre.getIgdbId(), genre));

        // Return the combined list of genres without duplicates
        return combinedGenresMap;
    }

    private Map<Integer, GameMode> processGameModes(List<Integer> gameModeIds, Game game) {
        if (gameModeIds == null || gameModeIds.isEmpty()) {
            return Collections.emptyMap();
        }

        logger.info("Processing Game Modes with IDs: {}", gameModeIds);

        // Fetch existing Game Modes from the database
        List<GameMode> existingGameModes = gameModeRepository.findAllByIgdbIdIn(gameModeIds);

        // Fetch missing Game Modes from IGDB API
        List<GameMode> fetchedGameModes = fetchEntitiesFromApi(gameModeIds, gameModesEndpoint, GameMode.class);

        fetchedGameModes.forEach(gameMode -> {
            gameMode.setIgdbId(gameMode.getId());
            gameMode.addGame(game);
            gameMode.setId(null); // Reset to allow creation of new entity if not present
        });

        // Merge fetched game modes with existing game modes
        List<GameMode> finalFetchedGameModes = fetchedGameModes;
        existingGameModes.forEach(gameMode -> {
            GameMode fetchedGameMode = finalFetchedGameModes.stream()
                    .filter(gm -> gm.getIgdbId().equals(gameMode.getIgdbId()))
                    .findFirst()
                    .orElse(null);

            if (fetchedGameMode != null && fetchedGameMode.getUpdatedAt().isAfter(gameMode.getUpdatedAt())) {
                fetchedGameMode.setId(gameMode.getId());
            }
        });

        // Save new or updated game modes
        fetchedGameModes = gameModeRepository.saveAll(fetchedGameModes);

        // Combine existing game modes with updated ones, removing duplicates
         Map<Integer, GameMode> combinedGameModesMap = new HashMap<>();

        // Add existing game modes to the map (IgdbId as key to ensure uniqueness)
        existingGameModes.forEach(gameMode -> combinedGameModesMap.put(gameMode.getIgdbId(), gameMode));

        // Add or update game modes from fetchedGameModes (this will overwrite any duplicates)
        fetchedGameModes.forEach(gameMode -> combinedGameModesMap.put(gameMode.getIgdbId(), gameMode));

        // Return the combined list of game modes without duplicates
        return combinedGameModesMap;
    }



    private Map<Integer, GameTheme> processGameThemes(List<Integer> themeIds, Game game) {
        if (themeIds == null || themeIds.isEmpty()) {
            return Collections.emptyMap();
        }

        logger.info("Processing Game Themes with IDs: {}", themeIds);

        // Fetch existing Game Themes from the database
        List<GameTheme> existingThemes = themeRepository.findAllByIgdbIdIn(themeIds);

        // Fetch missing Game Themes from IGDB API
        List<GameTheme> fetchedThemes = fetchEntitiesFromApi(themeIds, gameThemesEndpoint, GameTheme.class);

        fetchedThemes.forEach(theme -> {
            theme.setIgdbId(theme.getId());
            theme.addGame(game);
            theme.setId(null); // Reset to allow creation of new entity if not present
        });

        // Merge fetched themes with existing themes
        List<GameTheme> finalFetchedThemes = fetchedThemes;
        existingThemes.forEach(theme -> {
            GameTheme fetchedTheme = finalFetchedThemes.stream()
                    .filter(th -> th.getIgdbId().equals(theme.getIgdbId()))
                    .findFirst()
                    .orElse(null);

            if (fetchedTheme != null && fetchedTheme.getUpdatedAt().isAfter(theme.getUpdatedAt())) {
                fetchedTheme.setId(theme.getId());
            }
        });

        // Save new or updated themes
        fetchedThemes = themeRepository.saveAll(fetchedThemes);

        // Combine existing themes with updated ones, removing duplicates
         Map<Integer, GameTheme> combinedThemesMap = new HashMap<>();

        // Add existing themes to the map (IgdbId as key to ensure uniqueness)
        existingThemes.forEach(theme -> combinedThemesMap.put(theme.getIgdbId(), theme));

        // Add or update themes from fetchedThemes (this will overwrite any duplicates)
        fetchedThemes.forEach(theme -> combinedThemesMap.put(theme.getIgdbId(), theme));

        // Return the combined list of themes without duplicates
        return combinedThemesMap;
    }

    private Map<Integer, PlayerPerspective> processPlayerPerspectives(List<Integer> perspectiveIds, Game game) {
        if (perspectiveIds == null || perspectiveIds.isEmpty()) {
            return Collections.emptyMap();
        }

        logger.info("Processing Player Perspectives with IDs: {}", perspectiveIds);

        // Fetch existing Player Perspectives from the database
        List<PlayerPerspective> existingPerspectives = playerPerspectiveRepository.findAllByIgdbIdIn(perspectiveIds);

        // Fetch missing Player Perspectives from IGDB API
        List<PlayerPerspective> fetchedPerspectives = fetchEntitiesFromApi(perspectiveIds, playerPerspectivesEndpoint, PlayerPerspective.class);

        fetchedPerspectives.forEach(perspective -> {
            perspective.setIgdbId(perspective.getId());
            perspective.setId(null);
            perspective.addGame(game);
        });

        // Merge fetched perspectives with existing perspectives
        List<PlayerPerspective> finalFetchedPerspectives = fetchedPerspectives;
        existingPerspectives.forEach(perspective -> {
            PlayerPerspective fetchedPerspective = finalFetchedPerspectives.stream()
                    .filter(pp -> pp.getIgdbId().equals(perspective.getIgdbId()))
                    .findFirst()
                    .orElse(null);

            if (fetchedPerspective != null && fetchedPerspective.getUpdatedAt().isAfter(perspective.getUpdatedAt())) {
                fetchedPerspective.setId(perspective.getId());
            }
        });

        // Save new or updated perspectives
        fetchedPerspectives = playerPerspectiveRepository.saveAll(fetchedPerspectives);

        // Combine existing perspectives with updated ones, removing duplicates
         Map<Integer, PlayerPerspective> combinedPerspectivesMap = new HashMap<>();

        // Add existing perspectives to the map (IgdbId as key to ensure uniqueness)
        existingPerspectives.forEach(perspective -> combinedPerspectivesMap.put(perspective.getIgdbId(), perspective));

        // Add or update perspectives from fetchedPerspectives (this will overwrite any duplicates)
        fetchedPerspectives.forEach(perspective -> combinedPerspectivesMap.put(perspective.getIgdbId(), perspective));

        // Return the combined list of perspectives without duplicates
        return combinedPerspectivesMap;
    }

    private Map<Integer, AgeRating> processAgeRatings(List<Integer> ageRatingIds) {
        if (ageRatingIds == null || ageRatingIds.isEmpty()) {
            return Collections.emptyMap();
        }

        logger.info("Processing Age Ratings with IDs: {}", ageRatingIds);

        // Fetch existing Age Ratings from the database
        List<AgeRating> existingAgeRatings = ageRatingRepository.findAllByIgdbIdIn(ageRatingIds);

        // Fetch missing Age Ratings from IGDB API
        List<AgeRating> fetchedAgeRatings = fetchEntitiesFromApi(ageRatingIds, ageRatingsEndpoint, AgeRating.class);

        fetchedAgeRatings.forEach(ageRating -> {
            ageRating.setIgdbId(ageRating.getId());
            ageRating.setId(null); // Reset to allow creation of new entity if not present
        });

        // Merge fetched age ratings with existing age ratings
        List<AgeRating> finalFetchedAgeRatings = fetchedAgeRatings;
        existingAgeRatings.forEach(ageRating -> {
            AgeRating fetchedAgeRating = finalFetchedAgeRatings.stream()
                    .filter(ar -> ar.getIgdbId().equals(ageRating.getIgdbId()))
                    .findFirst()
                    .orElse(null);

            if (fetchedAgeRating != null && fetchedAgeRating.getUpdatedAt().isAfter(ageRating.getUpdatedAt())) {
                fetchedAgeRating.setId(ageRating.getId());
            }
        });

        // Save new or updated age ratings
        fetchedAgeRatings = ageRatingRepository.saveAll(fetchedAgeRatings);

        // Combine existing age ratings with updated ones, removing duplicates
         Map<Integer, AgeRating> combinedAgeRatingsMap = new HashMap<>();

        // Add existing age ratings to the map (IgdbId as key to ensure uniqueness)
        existingAgeRatings.forEach(ageRating -> combinedAgeRatingsMap.put(ageRating.getIgdbId(), ageRating));

        // Add or update age ratings from fetchedAgeRatings (this will overwrite any duplicates)
        fetchedAgeRatings.forEach(ageRating -> combinedAgeRatingsMap.put(ageRating.getIgdbId(), ageRating));

        // Return the combined list of age ratings without duplicates
        return combinedAgeRatingsMap;
    }

    private Map<Integer, ReleaseDate> processReleaseDates(List<Integer> releaseDateIds, Game game,
        Map<Integer, Platform> platforms) {
        if (releaseDateIds == null || releaseDateIds.isEmpty()) {
            return Collections.emptyMap();
        }

        logger.info("Processing Release Dates with IDs: {}", releaseDateIds);

        // Fetch existing Release Dates from the database
        List<ReleaseDate> existingReleaseDates = releaseDateRepository.findAllByIgdbIdIn(releaseDateIds);

        // Fetch missing Release Dates from IGDB API
        List<ReleaseDateIgdbDto> foundReleaseDates = fetchEntitiesFromApi(releaseDateIds, releaseDatesEndpoint, ReleaseDateIgdbDto.class);

        // Transform fetched release dates to ReleaseDate entities
        List<ReleaseDate> fetchedReleaseDates = foundReleaseDates.stream()
                .map(dto -> {
                    ReleaseDate releaseDate = new ReleaseDate();
                    releaseDate.setIgdbId(dto.getId());
                    releaseDate.setUpdatedAt(dto.getUpdatedAt());
                    releaseDate.setPlatform(platforms.get(dto.getPlatform()));
                    releaseDate.setGame(game);
                    return releaseDate;
                })
                .toList();

        // Merge fetched release dates with existing release dates
        List<ReleaseDate> finalFetchedReleaseDates = fetchedReleaseDates;
        existingReleaseDates.forEach(releaseDate -> {
            ReleaseDate fetchedReleaseDate = finalFetchedReleaseDates.stream()
                    .filter(rd -> rd.getId().equals(releaseDate.getIgdbId()))
                    .findFirst()
                    .orElse(null);

            if (fetchedReleaseDate != null && fetchedReleaseDate.getUpdatedAt().isAfter(releaseDate.getUpdatedAt())) {
                fetchedReleaseDate.setId(releaseDate.getId());
            }
        });


        fetchedReleaseDates = releaseDateRepository.saveAll(fetchedReleaseDates);

        // Combine existing release dates with updated ones, removing duplicates
         Map<Integer, ReleaseDate> combinedReleaseDatesMap = new HashMap<>();

        // Add existing release dates to the map (IgdbId as key to ensure uniqueness)
        existingReleaseDates.forEach(releaseDate -> combinedReleaseDatesMap.put(releaseDate.getIgdbId(), releaseDate));

        // Add or update release dates from releaseDatesToSave (this will overwrite any duplicates)
        fetchedReleaseDates.forEach(releaseDate -> combinedReleaseDatesMap.put(releaseDate.getIgdbId(), releaseDate));

        // Return the combined list of release dates without duplicates
        return combinedReleaseDatesMap;
    }

    private Map<Integer, Region> processRegions(List<Integer> regionIds) {
        if (regionIds == null || regionIds.isEmpty()) {
            return Collections.emptyMap();
        }

        logger.info("Processing Regions with IDs: {}", regionIds);

        // Fetch existing Regions from the database
        List<Region> existingRegions = regionRepository.findAllByIgdbIdIn(regionIds);

        // Fetch missing Regions from IGDB API
        List<Region> fetchedRegions = fetchEntitiesFromApi(regionIds, regionsEndpoint, Region.class);

        fetchedRegions.forEach(region -> {
            region.setIgdbId(region.getId());
            region.setId(null); // Reset to allow creation of new entity if not present
        });

        // Merge fetched regions with existing regions
        List<Region> finalFetchedRegions = fetchedRegions;
        existingRegions.forEach(region -> {
            Region fetchedRegion = finalFetchedRegions.stream()
                    .filter(r -> r.getIgdbId().equals(region.getIgdbId()))
                    .findFirst()
                    .orElse(null);

            if (fetchedRegion != null && fetchedRegion.getUpdatedAt().isAfter(region.getUpdatedAt())) {
                fetchedRegion.setId(region.getId());
            }
        });

        // Save new or updated regions
        fetchedRegions = regionRepository.saveAll(fetchedRegions);

        // Combine existing regions with updated ones, removing duplicates
         Map<Integer, Region> combinedRegionsMap = new HashMap<>();

        // Add existing regions to the map (IgdbId as key to ensure uniqueness)
        existingRegions.forEach(region -> combinedRegionsMap.put(region.getIgdbId(), region));

        // Add or update regions from fetchedRegions (this will overwrite any duplicates)
        fetchedRegions.forEach(region -> combinedRegionsMap.put(region.getIgdbId(), region));

        // Return the combined list of regions without duplicates
        return combinedRegionsMap;
    }

    private Map<Integer, Franchise> processFranchises(List<Integer> franchiseIds, Game game) {
        if (franchiseIds == null || franchiseIds.isEmpty()) {
            return Collections.emptyMap();
        }

        logger.info("Processing Franchises with IDs: {}", franchiseIds);

        // Fetch existing Franchises from the database
        List<Franchise> existingFranchises = franchiseRepository.findAllByIgdbIdIn(franchiseIds);

        // Fetch missing Franchises from IGDB API
        List<Franchise> fetchedFranchises = fetchEntitiesFromApi(franchiseIds, franchisesEndpoint, Franchise.class);

        fetchedFranchises.forEach(franchise -> {
            franchise.setIgdbId(franchise.getId());
            franchise.setId(null); // Reset to allow creation of new entity if not present
        });

        // Merge fetched franchises with existing franchises
        List<Franchise> finalFetchedFranchises = fetchedFranchises;
        existingFranchises.forEach(franchise -> {
            Franchise fetchedFranchise = finalFetchedFranchises.stream()
                    .filter(f -> f.getIgdbId().equals(franchise.getIgdbId()))
                    .findFirst()
                    .orElse(null);

            if (fetchedFranchise != null && fetchedFranchise.getUpdatedAt().isAfter(franchise.getUpdatedAt())) {
                fetchedFranchise.setId(franchise.getId());
            }
        });

        // Save new or updated franchises
        fetchedFranchises = franchiseRepository.saveAll(fetchedFranchises);

        // Combine existing franchises with updated ones, removing duplicates
         Map<Integer, Franchise> combinedFranchisesMap = new HashMap<>();

        // Add existing franchises to the map (IgdbId as key to ensure uniqueness)
        existingFranchises.forEach(franchise -> combinedFranchisesMap.put(franchise.getIgdbId(), franchise));

        // Add or update franchises from fetchedFranchises (this will overwrite any duplicates)
        fetchedFranchises.forEach(franchise -> combinedFranchisesMap.put(franchise.getIgdbId(), franchise));

        // Return the combined list of franchises without duplicates
        return combinedFranchisesMap;
    }

    private Map<Integer, LanguageSupport> processLanguageSupports(List<Integer> languageSupportIds, Game game) {
        if (languageSupportIds == null || languageSupportIds.isEmpty()) {
            return Collections.emptyMap();
        }

        logger.info("Processing Language Supports with IDs: {}", languageSupportIds);

        // Fetch existing Language Supports from the database
        List<LanguageSupport> existingLanguageSupports = languageSupportRepository.findAllByIgdbIdIn(languageSupportIds);

        // Fetch missing Language Supports from IGDB API
        List<IgdbGameLanguageSupportDto> foundLanguageSupports = fetchEntitiesFromApi(languageSupportIds,
                languageSupportEndpoint, IgdbGameLanguageSupportDto.class);


        // Transform fetched language supports to LanguageSupport entities
        List<LanguageSupport> fetchedLanguageSupports = foundLanguageSupports.stream()
                .map(dto -> {
                    LanguageSupport languageSupport = new LanguageSupport();
                    languageSupport.setIgdbId(dto.getId());
                    languageSupport.setUpdatedAt(dto.getUpdatedAt());
                    languageSupport.setLanguageSupportType(LanguageSupportType.fromId(dto.getType()));
                    languageSupport.setLanguage(processLanguage(dto.getLanguage()));
                    languageSupport.setGame(game);
                    return languageSupport;
                })
                .toList();

        // Merge fetched language supports with existing language supports
        List<LanguageSupport> finalFetchedLanguageSupports = fetchedLanguageSupports;
        existingLanguageSupports.forEach(languageSupport -> {
            LanguageSupport fetchedLanguageSupport = finalFetchedLanguageSupports.stream()
                    .filter(ls -> ls.getIgdbId().equals(languageSupport.getIgdbId()))
                    .findFirst()
                    .orElse(null);

            if (fetchedLanguageSupport != null && fetchedLanguageSupport.getUpdatedAt().isAfter(
                    languageSupport.getUpdatedAt())) {
                fetchedLanguageSupport.setId(languageSupport.getId());
            }
        });

        fetchedLanguageSupports = languageSupportRepository.saveAll(fetchedLanguageSupports);


        // Combine existing language supports with updated ones, removing duplicates
         Map<Integer, LanguageSupport> combinedLanguageSupportsMap = new HashMap<>();

        // Add existing language supports to the map (IgdbId as key to ensure uniqueness)
        existingLanguageSupports.forEach(languageSupport -> combinedLanguageSupportsMap.put(languageSupport.getIgdbId(), languageSupport));

        // Add or update language supports from fetchedLanguageSupports (this will overwrite any duplicates)
        fetchedLanguageSupports.forEach(languageSupport -> combinedLanguageSupportsMap.put(languageSupport.getIgdbId(), languageSupport));

        // Return the combined list of language supports without duplicates
        return combinedLanguageSupportsMap;
    }

    private Language processLanguage(Integer languageId) {
        if (languageId == null) {
            logger.warn("No Language ID provided.");
            return null;
        }

        logger.info("Fetching Language with ID: {}", languageId);

        // Fetch Language from the database
        Language language = languageRepository.findByIgdbId(languageId);

        // Fetch Language from IGDB API
        String url = etlUrl + languagesEndpoint;
        String requestBody = "fields *; where id = " + languageId + ";";

        HttpHeaders headers = getHttpHeaders();
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
        logger.info("Language request sent successfully");

        if (!response.getStatusCode().is2xxSuccessful()) {
            logger.error("Failed to obtain Language with ID: {}", languageId);
            throw new RuntimeException("Failed to obtain Language from IGDB API");
        }

        try {
            String jsonResponse = response.getBody();
            logger.debug("Language response: {}", jsonResponse);
            List<Language> languageDtos = objectMapper.readValue(jsonResponse,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Language.class));

            if (languageDtos.isEmpty()) {
                logger.warn("No Language data found for ID: {}", languageId);
                return null;
            }

            Language dto = languageDtos.get(0);

            if (dto == null) {
                logger.warn("No Language data found for ID: {}", languageId);
                return null;
            }

            Integer idToSet = null;
            if (language!=null){
                if (language.getUpdatedAt().isAfter(dto.getUpdatedAt())){
                    return language;
                }
                idToSet = language.getId();
            }

            language = new Language();
            language.setId(idToSet);
            language.setIgdbId(dto.getId());
            language.setName(dto.getName());
            language.setUpdatedAt(dto.getUpdatedAt());
            language.setNativeName(dto.getNativeName());
            language.setLocale(dto.getLocale());
            language = languageRepository.save(language);

            logger.info("Language saved to the database: {}", language.getName());
            return language;
        } catch (Exception e) {
            logger.error("Error while parsing the Language data for ID: {}", languageId, e);
            throw new RuntimeException("Error while parsing the Language data", e);
        }
    }

    private Map<Integer, CompanyGame> processCompanyGames(List<Integer> companyGameIds, Game gameEntity) {
        if (companyGameIds == null || companyGameIds.isEmpty()) {
            return Collections.emptyMap();
        }

        logger.info("Processing Involved Companies with IDs: {}", companyGameIds);

        // Fetch existing Company Games from the database
        List<CompanyGame> existingCompanyGames = companyGameRepository.findAllByIgdbIdIn(companyGameIds);

        // Fetch missing Company Games from IGDB API
        List<IgbdCompanyGameDto> foundCompanyGames = fetchEntitiesFromApi(companyGameIds,
                involvedCompaniesEndpoint, IgbdCompanyGameDto.class);

        // Transform fetched company games to CompanyGame entities
        List<CompanyGame> fetchedCompanyGames = foundCompanyGames.stream()
                .map(dto -> {
                    CompanyGame companyGame = new CompanyGame();
                    companyGame.setIgdbId(dto.getId());
                    companyGame.setUpdatedAt(dto.getUpdatedAt());
                    companyGame.setPorter(dto.isPorter());
                    companyGame.setPublisher(dto.isPublisher());
                    companyGame.setDeveloper(dto.isDeveloper());
                    companyGame.setSupporter(dto.isSupporter());
                    companyGame.setCompany(processCompany(dto.getCompany(), companyGame));
                    companyGame.setGame(gameEntity);
                    return companyGame;
                })
                .toList();

        // Merge fetched company games with existing company games
        List<CompanyGame> finalFetchedCompanyGames = fetchedCompanyGames;
        existingCompanyGames.forEach(companyGame -> {
            CompanyGame fetchedCompanyGame = finalFetchedCompanyGames.stream()
                    .filter(cg -> cg.getIgdbId().equals(companyGame.getIgdbId()))
                    .findFirst()
                    .orElse(null);

            if (fetchedCompanyGame != null && fetchedCompanyGame.getUpdatedAt().isAfter(companyGame.getUpdatedAt())) {
                fetchedCompanyGame.setId(companyGame.getId());
            }
        });

        fetchedCompanyGames = companyGameRepository.saveAll(fetchedCompanyGames);

        // Combine existing company games with updated ones, removing duplicates
         Map<Integer, CompanyGame> combinedCompanyGamesMap = new HashMap<>();

        // Add existing company games to the map (IgdbId as key to ensure uniqueness)
        existingCompanyGames.forEach(companyGame -> combinedCompanyGamesMap.put(companyGame.getIgdbId(), companyGame));

        // Add or update company games from fetchedCompanyGames (this will overwrite any duplicates)
        fetchedCompanyGames.forEach(companyGame -> combinedCompanyGamesMap.put(companyGame.getIgdbId(), companyGame));

        // Return the combined list of company games without duplicates
        return combinedCompanyGamesMap;
    }

    // --- Fetch Company ---

    private Company processCompany(Integer companyId, CompanyGame companyGame) {
        if (companyId == null) {
            logger.warn("No Company ID provided.");
            return null;
        }

        logger.info("Fetching Company with ID: {}", companyId);

        // Fetch Company from the database
        Company company = companyRepository.findByIgdbId(companyId);

        // Fetch Company from IGDB API
        String url = etlUrl + companiesEndpoint;
        String requestBody = "fields *; where id = " + companyId + ";";
        HttpHeaders headers = getHttpHeaders();
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
        logger.info("Company request sent successfully");

        if (!response.getStatusCode().is2xxSuccessful()) {
            logger.error("Failed to obtain Company with ID: {}", companyId);
            throw new RuntimeException("Failed to obtain Company from IGDB API");
        }

        try {
            String jsonResponse = response.getBody();
            logger.debug("Company response: {}", jsonResponse);
            List<IgbdCompanyDto> companyDtos = objectMapper.readValue(jsonResponse,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, IgbdCompanyDto.class));

            if (companyDtos.isEmpty()) {
                logger.warn("No Company data found for ID: {}", companyId);
                return null;
            }

            IgbdCompanyDto dto = companyDtos.get(0);

            if (dto == null) {
                logger.warn("No Company data found for ID: {}", companyId);
                return null;
            }

            Integer idToSet = null;
            if (company!=null){
                if (company.getUpdatedAt().isAfter(dto.getUpdatedAt())){
                    return company;
                }
                idToSet = company.getId();
            }

            company = new Company();
            company.setId(idToSet);
            company.setIgdbId(dto.getId());
            company.setName(dto.getName());
            company.setUpdatedAt(dto.getUpdatedAt());
            company.setLogoUrl(SearchCompanyLogo(companyId));
            company.addCompanyGame(companyGame);
            company.setDescription(dto.getDescription());
            company = companyRepository.save(company);

            logger.info("Company saved to the database: {}", company.getName());
            return company;
        } catch (Exception e) {
            logger.error("Error while parsing the Company data for ID: {}", companyId, e);
            throw new RuntimeException("Error while parsing the Company data", e);
        }
    }


    // --- Fetch Company Logo ---

    private String SearchCompanyLogo(Integer companyId) {
        if (companyId == null) {
            logger.warn("No Company ID provided.");
            return null;
        }

        logger.info("Fetching Company Logo with ID: {}", companyId);

        // Fetch Company Logo from IGDB API
        String url = etlUrl + companyLogosEndpoint;
        String requestBody = "fields *; where id = " + companyId + ";";

        HttpHeaders headers = getHttpHeaders();
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
        logger.info("Company Logo request sent successfully");

        if (!response.getStatusCode().is2xxSuccessful()) {
            logger.error("Failed to obtain Company Logo with ID: {}", companyId);
            throw new RuntimeException("Failed to obtain Company Logo from IGDB API");
        }

        try {
            String jsonResponse = response.getBody();
            logger.debug("Company Logo response: {}", jsonResponse);
            List<IgdbCompanyLogoDto> companyLogos = objectMapper.readValue(jsonResponse,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, IgdbCompanyLogoDto.class));

            if (companyLogos.isEmpty()) {
                logger.warn("No Company Logo data found for ID: {}", companyId);
                return null;
            }

            IgdbCompanyLogoDto dto = companyLogos.get(0);

            logger.info("Company Logo saved to the database: {}", dto.getUrl());
            return dto.getUrl();
        } catch (Exception e) {
            logger.error("Error while parsing the Company Logo data for ID: {}", companyId, e);
            throw new RuntimeException("Error while parsing the Company Logo data", e);
        }
    }

    // --- Fetching Similar Games ---

    /**
     * Processes Similar Games by fetching them from the database and establishing bidirectional relationships.
     *
     * @param similarGameIds List of Similar Game IGDB IDs.
     * @param game           The Game entity to associate with Similar Games.
     * @return List of Similar Game entities.
     */
    private List<Game> processSimilarGames(List<Integer> similarGameIds, Game game) {
        List<Game> similarGames = new ArrayList<>();
        if (similarGameIds != null && !similarGameIds.isEmpty()) {
            logger.debug("Game '{}' has similar games: {}", game.getTitle(), similarGameIds);

            // Fetch similar games from the database in batch
            List<Game> fetchedSimilarGames = gameRepository.findAllByIgdbIdIn(similarGameIds);

            if (fetchedSimilarGames.isEmpty()) {
                logger.warn("No similar games found in the database for game '{}'.", game.getTitle());
            } else {
                similarGames.addAll(fetchedSimilarGames);

                // Establish bidirectional relationships
                for (Game similarGame : fetchedSimilarGames) {
                    if (!similarGame.getSimilarGames().contains(game)) {
                        similarGame.addSimilarGame(game);
                        gameRepository.save(similarGame);
                        logger.info("Established bidirectional relationship between '{}' and '{}'.",
                                game.getTitle(), similarGame.getTitle());
                    }
                }
            }
        } else {
            logger.warn("Game '{}' has no similar games.", game.getTitle());
        }
        return similarGames;
    }

    // --- Generic Fetch Method for API Calls ---

    /**
     * Fetches entities from the IGDB API.
     *
     * @param <D>      The type of the DTO.
     * @param ids      List of IGDB IDs.
     * @param endpoint The IGDB API endpoint.
     * @param dtoClass The DTO class.
     * @return List of DTOs fetched from the API.
     */
    private <D> List<D> fetchEntitiesFromApi(List<Integer> ids, String endpoint, Class<D> dtoClass) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }

        String url = etlUrl + endpoint;
        logger.info("Sending batch request to: {}", url);

        // Construct the query to fetch multiple IDs
        String idsString = ids.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(", "));
        String requestBody = "fields *; where id = (" + idsString + ");";

        HttpHeaders headers = getHttpHeaders();
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
        logger.info("Batch request sent successfully");

        if (!response.getStatusCode().is2xxSuccessful()) {
            logger.error("Failed to obtain entities from IGDB API");
            throw new RuntimeException("Failed to obtain entities from IGDB API");
        }

        try {
            String jsonResponse = response.getBody();
            logger.debug("Batch response: {}", jsonResponse);
            List<D> dtos = objectMapper.readValue(jsonResponse,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, dtoClass));
            return dtos;
        } catch (Exception e) {
            logger.error("Error while parsing batch API response", e);
            throw new RuntimeException("Error while parsing batch API response", e);
        }
    }

    // --- Fetch Cover URL ---

    /**
     * Gets the cover URL from the IGDB API.
     *
     * @param coverId The cover ID.
     * @return The cover URL.
     */
    private String GetCoverUrl(Integer coverId) {
        if (coverId == null) {
            logger.warn("No cover ID provided.");
            return null;
        }

        logger.info("Fetching cover URL with ID: {}", coverId);

        String url = etlUrl + coverEndpoint;
        String requestBody = "fields url; where id = " + coverId + ";";

        HttpHeaders headers = getHttpHeaders();
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
        logger.info("Cover request sent successfully");

        if (!response.getStatusCode().is2xxSuccessful()) {
            logger.error("Failed to obtain cover with ID: {}", coverId);
            throw new RuntimeException("Failed to obtain cover from IGDB API");
        }

        try {
            String jsonResponse = response.getBody();
            logger.debug("Cover response: {}", jsonResponse);
            List<CoverIgdbDto> covers = objectMapper.readValue(jsonResponse,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, CoverIgdbDto.class));

            if (covers.isEmpty()) {
                logger.warn("No cover data found for ID: {}", coverId);
                return null;
            }

            String coverUrl = covers.get(0).getUrl();
            logger.info("Cover URL obtained: {}", coverUrl);
            return coverUrl;
        } catch (Exception e) {
            logger.error("Error while parsing the cover data for ID: {}", coverId, e);
            throw new RuntimeException("Error while parsing the cover data", e);
        }
    }
}
