package br.ufg.ceia.gameinsight.gameservice.domain.game;

import br.ufg.ceia.gameinsight.gameservice.domain.company.company_game.CompanyGame;
import br.ufg.ceia.gameinsight.gameservice.domain.game.age_rating.AgeRating;
import br.ufg.ceia.gameinsight.gameservice.domain.game.franchise.Franchise;
import br.ufg.ceia.gameinsight.gameservice.domain.game.game_mode.GameMode;
import br.ufg.ceia.gameinsight.gameservice.domain.game.game_theme.GameTheme;
import br.ufg.ceia.gameinsight.gameservice.domain.game.genre.Genre;
import br.ufg.ceia.gameinsight.gameservice.domain.game.localization.Localization;
import br.ufg.ceia.gameinsight.gameservice.domain.game.player_perspective.PlayerPerspective;
import br.ufg.ceia.gameinsight.gameservice.domain.game.release_date.ReleaseDate;
import br.ufg.ceia.gameinsight.gameservice.domain.game.requirement.Requirement;
import br.ufg.ceia.gameinsight.gameservice.domain.platform.Platform;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @brief Entity class representing a Game in the database.
 * @details This class holds all the information about a game including title, cover,
 * genres, platforms, release dates, ratings, and related entities like companies,
 * localizations, and requirements.
 */
@Entity
@Table(name = "game")
@Getter
@Setter
public class Game implements Serializable {

    // Serial version UID for the Serializable interface
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * @brief Unique identifier for the game.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * @brief The IGDB identifier for the game.
     */
    private Integer IGDBId;

    /**
     * @brief The title of the game.
     */
    private String title;

    /**
     * @brief The last Instant the game was updated.
     */
    private Instant updatedAt;

    /**
     * @brief The cover image URL of the game.
     */
    @Column(length = 1024)
    private String cover;

    /**
     * @brief The summary or description of the game.
     */
    @Column(columnDefinition = "TEXT")
    private String summary;

    /**
     * @brief The overall rating of the game.
     */
    private float rating;

    /**
     * @brief The total number of ratings received for the game.
     */
    private int ratingCount;

    /**
     * @brief The list of release dates for the game.
     */
    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReleaseDate> releaseDates;

    /**
     * @brief The list of age ratings associated with the game.
     */
    @ManyToMany
    private List<AgeRating> ageRatings;

    /**
     * @brief The list of genres associated with the game.
     */
    @ManyToMany
    private List<Genre> genres;

    /**
     * @brief The list of themes associated with the game.
     */
    @ManyToMany
    private List<GameTheme> themes;

    /**
     * @brief The list of franchises associated with the game.
     */
    @ManyToMany
    private List<Franchise> franchises;

    /**
     * @brief The list of game modes available in the game.
     */
    @ManyToMany
    private List<GameMode> gameModes;

    /**
     * @brief The list of player perspectives available in the game.
     */
    @ManyToMany
    private List<PlayerPerspective> playerPerspectives;

    /**
     * @brief The list of localizations available for the game.
     */
    @ManyToMany
    private List<Localization> localizations;

    /**
     * @brief The list of companies involved in the game's development or publishing.
     */
    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CompanyGame> involvedCompanies = new ArrayList<>();

    /**
     * @brief The list of platforms the game is available on.
     */
    @ManyToMany
    private List<Platform> platforms;

    /**
     * @brief The list of system requirements for the game.
     */
    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Requirement> requirements;

    /**
     * @brief The list of similar games to the current game.
     * @details This list is populated by the recommendation engine, or by the Igdb API.
     */
    @ManyToMany
    @JsonIgnore
    private List<Game> similarGames;

    /**
     * @brief Default constructor.
     */
    public Game() {}

    /**
     * @brief Parameterized constructor.
     * @param id The ID of the game.
     * @param title The title of the game.
     * @param cover The cover URL of the game.
     * @param releaseDates The list of release dates for the game.
     * @param ageRatings The list of age ratings.
     * @param summary The summary of the game.
     * @param genres The genres associated with the game.
     * @param themes The themes associated with the game.
     * @param franchises The franchises related to the game.
     * @param gameModes The game modes available in the game.
     * @param playerPerspectives The player perspectives available.
     * @param localizations The localizations for the game.
     * @param rating The rating of the game.
     * @param ratingCount The total rating count for the game.
     * @param involvedCompanies The companies involved in the game.
     * @param platforms The platforms the game is available on.
     * @param requirements The system requirements for the game.
     */
    public Game(Integer id, String title, String cover, List<ReleaseDate> releaseDates,
                List<AgeRating> ageRatings, String summary, List<Genre> genres,
                List<GameTheme> themes, List<Franchise> franchises, List<GameMode> gameModes,
                List<PlayerPerspective> playerPerspectives, List<Localization> localizations,
                float rating, int ratingCount, List<CompanyGame> involvedCompanies,
                List<Platform> platforms, List<Requirement> requirements) {
        this.id = id;
        this.title = title;
        this.cover = cover;
        this.releaseDates = releaseDates;
        this.ageRatings = ageRatings;
        this.summary = summary;
        this.genres = genres;
        this.themes = themes;
        this.franchises = franchises;
        this.gameModes = gameModes;
        this.playerPerspectives = playerPerspectives;
        this.localizations = localizations;
        this.rating = rating;
        this.ratingCount = ratingCount;
        this.involvedCompanies = involvedCompanies;
        this.platforms = platforms;
        this.requirements = requirements;
    }

    /**
     * @brief String representation of the Game entity.
     * @return String representation of the Game object.
     */
    @Override
    public String toString() {
        return "Game{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", cover='" + cover + '\'' +
                ", releaseDates=" + releaseDates +
                ", ageRatings=" + ageRatings +
                ", summary='" + summary + '\'' +
                ", genres=" + genres +
                ", themes=" + themes +
                ", franchises=" + franchises +
                ", gameModes=" + gameModes +
                ", playerPerspectives=" + playerPerspectives +
                ", localizations=" + localizations +
                ", rating=" + rating +
                ", ratingCount=" + ratingCount +
                ", involvedCompanies=" + involvedCompanies +
                ", platforms=" + platforms +
                ", requirements=" + requirements +
                '}';
    }

    /**
     * @brief Equals method to compare two Game objects.
     * @param o Object to be compared.
     * @return true if the objects are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Game)) return false;
        Game game = (Game) o;
        return id == game.id &&
                Float.compare(game.rating, rating) == 0 &&
                ratingCount == game.ratingCount &&
                Objects.equals(title, game.title) &&
                Objects.equals(cover, game.cover) &&
                Objects.equals(releaseDates, game.releaseDates) &&
                Objects.equals(ageRatings, game.ageRatings) &&
                Objects.equals(summary, game.summary) &&
                Objects.equals(genres, game.genres) &&
                Objects.equals(themes, game.themes) &&
                Objects.equals(franchises, game.franchises) &&
                Objects.equals(gameModes, game.gameModes) &&
                Objects.equals(playerPerspectives, game.playerPerspectives) &&
                Objects.equals(localizations, game.localizations) &&
                Objects.equals(involvedCompanies, game.involvedCompanies) &&
                Objects.equals(platforms, game.platforms) &&
                Objects.equals(requirements, game.requirements);
    }

    /**
     * @brief Hash code method for the Game entity.
     * @return Hash code of the Game object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, title, cover, releaseDates, ageRatings, summary, genres, themes, franchises, gameModes, playerPerspectives, localizations, rating, ratingCount, involvedCompanies, platforms, requirements);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public int getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(int ratingCount) {
        this.ratingCount = ratingCount;
    }

    public List<ReleaseDate> getReleaseDates() {
        return releaseDates;
    }

    public void setReleaseDates(List<ReleaseDate> releaseDates) {
        this.releaseDates = releaseDates;
    }

    public List<AgeRating> getAgeRatings() {
        return ageRatings;
    }

    public void setAgeRatings(List<AgeRating> ageRatings) {
        this.ageRatings = ageRatings;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }

    public List<GameTheme> getThemes() {
        return themes;
    }

    public void setThemes(List<GameTheme> themes) {
        this.themes = themes;
    }

    public List<Franchise> getFranchises() {
        return franchises;
    }

    public void setFranchises(List<Franchise> franchises) {
        this.franchises = franchises;
    }

    public List<GameMode> getGameModes() {
        return gameModes;
    }

    public void setGameModes(List<GameMode> gameModes) {
        this.gameModes = gameModes;
    }

    public List<PlayerPerspective> getPlayerPerspectives() {
        return playerPerspectives;
    }

    public void setPlayerPerspectives(List<PlayerPerspective> playerPerspectives) {
        this.playerPerspectives = playerPerspectives;
    }

    public List<Localization> getLocalizations() {
        return localizations;
    }

    public void setLocalizations(List<Localization> localizations) {
        this.localizations = localizations;
    }

    public List<CompanyGame> getInvolvedCompanies() {
        return involvedCompanies;
    }

    public void setInvolvedCompanies(List<CompanyGame> involvedCompanies) {
        this.involvedCompanies = involvedCompanies;
    }

    public List<Platform> getPlatforms() {
        return platforms;
    }

    public void setPlatforms(List<Platform> platforms) {
        this.platforms = platforms;
    }

    public List<Requirement> getRequirements() {
        return requirements;
    }

    public void setRequirements(List<Requirement> requirements) {
        this.requirements = requirements;
    }

    // Adders and removers for the lists of related entities
    public void addReleaseDate(ReleaseDate releaseDate) {
        this.releaseDates.add(releaseDate);
    }

    public void removeReleaseDate(ReleaseDate releaseDate) {
        this.releaseDates.remove(releaseDate);
    }

    public void addAgeRating(AgeRating ageRating) {
        this.ageRatings.add(ageRating);
    }

    public void removeAgeRating(AgeRating ageRating) {
        this.ageRatings.remove(ageRating);
    }

    public void addGenre(Genre genre) {
        this.genres.add(genre);
    }

    public void removeGenre(Genre genre) {
        this.genres.remove(genre);
    }

    public void addTheme(GameTheme theme) {
        this.themes.add(theme);
    }

    public void removeTheme(GameTheme theme) {
        this.themes.remove(theme);
    }

    public void addFranchise(Franchise franchise) {
        this.franchises.add(franchise);
    }

    public void removeFranchise(Franchise franchise) {
        this.franchises.remove(franchise);
    }

    public void addGameMode(GameMode gameMode) {
        this.gameModes.add(gameMode);
    }

    public void removeGameMode(GameMode gameMode) {
        this.gameModes.remove(gameMode);
    }

    public void addPlayerPerspective(PlayerPerspective playerPerspective) {
        this.playerPerspectives.add(playerPerspective);
    }

    public void removePlayerPerspective(PlayerPerspective playerPerspective) {
        this.playerPerspectives.remove(playerPerspective);
    }

    public void addLocalization(Localization localization) {
        this.localizations.add(localization);
    }

    public void removeLocalization(Localization localization) {
        this.localizations.remove(localization);
    }

    public void addCompany(CompanyGame company) {
        this.involvedCompanies.add(company);
    }

    public void removeCompany(CompanyGame company) {
        this.involvedCompanies.remove(company);
    }

    public void addPlatform(Platform platform) {
        this.platforms.add(platform);
    }

    public void removePlatform(Platform platform) {
        this.platforms.remove(platform);
    }

    public void addRequirement(Requirement requirement) {
        this.requirements.add(requirement);
    }

    public void removeRequirement(Requirement requirement) {
        this.requirements.remove(requirement);
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getIGDBId() {
        return IGDBId;
    }

    public void setIGDBId(Integer IGDBId) {
        this.IGDBId = IGDBId;
    }
}
