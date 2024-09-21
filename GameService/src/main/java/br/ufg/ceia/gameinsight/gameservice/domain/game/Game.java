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
import jakarta.persistence.*;
import org.springframework.stereotype.Repository;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * This class represents the Game entity.
 * <p>
 * This class holds details about a game associated with a user's profile.
 */
@Repository
public class Game implements Serializable {
    /**
     * The serial version UID.
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * id of the game.
     */
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    /**
     * The title of the game.
     */
    private String title;

    /**
     * The cover image URL of the game.
     */
    private String cover;

    /**
     * The release dates of the game.
     */
    @OneToMany
    List<ReleaseDate> releaseDates;

    /**
     * The list of age ratings associated with the game.
     */
    @ManyToMany(mappedBy = "games")
    List<AgeRating> ageRatings;

    /**
     * The story of the game.
     */
    private String storyLine;

    /**
     * The list of genres associated with the game.
     */
    @ManyToMany(mappedBy = "games")
    List<Genre> genres;

    /**
     * The list of themes associated with the game.
     */
    @ManyToMany(mappedBy = "games")
    List<GameTheme> themes;

    /**
     * The list of franchises associated with the game.
     */
    @ManyToMany(mappedBy = "games")
    List<Franchise> franchises;

    /**
     * The list of Game Modes associated with the game.
     */
    @ManyToMany(mappedBy = "games")
    List<GameMode> gameModes;

    /**
     * The list of player perspectives associated with the game.
     */
    @ManyToMany(mappedBy = "games")
    List<PlayerPerspective> playerPerspectives;

    /**
     * The list of localizations associated with the game.
     */
    @ManyToMany(mappedBy = "games")
    List<Localization> localizations;

    /**
     * The list of platforms associated with the game.
     */
    private float rating;

    /**
     * The number of ratings for the game.
     */
    private int ratingCount;

    /**
     * The list of involved companies associated with the game.
     */
    @OneToMany(mappedBy = "games")
    List<CompanyGame> involvedCompanies;

    /**
     * The list of platforms associated with the game.
     */
    @ManyToMany(mappedBy = "games")
    List<Platform> platforms;

    /**
     * The list of requirements associated with the game.
     */
    @OneToMany(mappedBy = "games")
    List<Requirement> requirements;

    /**
     * The constructor of the class.
     */
    public Game() {
    }

    /**
     * The constructor of the class.
     * @param id The unique identifier of the game.
     * @param title The title of the game.
     * @param cover The cover image URL of the game.
     * @param releaseDates The release dates of the game.
     * @param ageRatings The list of age ratings associated with the game.
     * @param storyLine The story of the game.
     * @param genres The list of genres associated with the game.
     * @param themes The list of themes associated with the game.
     * @param franchises The list of franchises associated with the game.
     * @param gameModes The list of Game Modes associated with the game.
     * @param playerPerspectives The list of player perspectives associated with the game.
     * @param localizations The list of localizations associated with the game.
     * @param rating The rating of the game.
     * @param ratingCount The number of ratings for the game.
     * @param involvedCompanies The list of involved companies associated with the game.
     * @param platforms The list of platforms associated with the game.
     * @param requirements The list of requirements associated with the game.
     */
    public Game(long id, String title, String cover, List<ReleaseDate> releaseDates, List<AgeRating> ageRatings, String storyLine, List<Genre> genres, List<GameTheme> themes, List<Franchise> franchises, List<GameMode> gameModes, List<PlayerPerspective> playerPerspectives, List<Localization> localizations, float rating, int ratingCount, List<CompanyGame> involvedCompanies, List<Platform> platforms, List<Requirement> requirements) {
        this.id = id;
        this.title = title;
        this.cover = cover;
        this.releaseDates = releaseDates;
        this.ageRatings = ageRatings;
        this.storyLine = storyLine;
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
     * Returns the unique identifier of the game.
     * @return The unique identifier of the game.
     */
    public long getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the game.
     * @param id The unique identifier of the game.
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Returns the title of the game.
     * @return The title of the game.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of the game.
     * @param title The title of the game.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Returns the cover image URL of the game.
     * @return The cover image URL of the game.
     */
    public String getCover() {
        return cover;
    }

    /**
     * Sets the cover image URL of the game.
     * @param cover The cover image URL of the game.
     */
    public void setCover(String cover) {
        this.cover = cover;
    }

    /**
     * Returns the release dates of the game.
     * @return The release dates of the game.
     */
    public List<ReleaseDate> getReleaseDates() {
        return releaseDates;
    }

    /**
     * Sets the release dates of the game.
     * @param releaseDates The release dates of the game.
     */
    public void setReleaseDates(List<ReleaseDate> releaseDates) {
        this.releaseDates = releaseDates;
    }

    /**
     * Returns the list of age ratings associated with the game.
     * @return The list of age ratings associated with the game.
     */
    public List<AgeRating> getAgeRatings() {
        return ageRatings;
    }

    /**
     * Sets the list of age ratings associated with the game.
     * @param ageRatings The list of age ratings associated with the game.
     */
    public void setAgeRatings(List<AgeRating> ageRatings) {
        this.ageRatings = ageRatings;
    }

    /**
     * Returns the story of the game.
     * @return The story of the game.
     */
    public String getStoryLine() {
        return storyLine;
    }

    /**
     * Sets the story of the game.
     * @param storyLine The story of the game.
     */
    public void setStoryLine(String storyLine) {
        this.storyLine = storyLine;
    }

    /**
     * Returns the list of genres associated with the game.
     * @return The list of genres associated with the game.
     */
    public List<Genre> getGenres() {
        return genres;
    }

    /**
     * Sets the list of genres associated with the game.
     * @param genres The list of genres associated with the game.
     */
    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }

    /**
     * Returns the list of themes associated with the game.
     * @return The list of themes associated with the game.
     */
    public List<GameTheme> getThemes() {
        return themes;
    }

    /**
     * Sets the list of themes associated with the game.
     * @param themes The list of themes associated with the game.
     */
    public void setThemes(List<GameTheme> themes) {
        this.themes = themes;
    }

    /**
     * Returns the list of franchises associated with the game.
     * @return The list of franchises associated with the game.
     */
    public List<Franchise> getFranchises() {
        return franchises;
    }

    /**
     * Sets the list of franchises associated with the game.
     * @param franchises The list of franchises associated with the game.
     */
    public void setFranchises(List<Franchise> franchises) {
        this.franchises = franchises;
    }

    /**
     * Returns the list of Game Modes associated with the game.
     * @return The list of Game Modes associated with the game.
     */
    public List<GameMode> getGameModes() {
        return gameModes;
    }

    /**
     * Sets the list of Game Modes associated with the game.
     * @param gameModes The list of Game Modes associated with the game.
     */
    public void setGameModes(List<GameMode> gameModes) {
        this.gameModes = gameModes;
    }

    /**
     * Returns the list of player perspectives associated with the game.
     * @return The list of player perspectives associated with the game.
     */
    public List<PlayerPerspective> getPlayerPerspectives() {
        return playerPerspectives;
    }

    /**
     * Sets the list of player perspectives associated with the game.
     * @param playerPerspectives The list of player perspectives associated with the game.
     */
    public void setPlayerPerspectives(List<PlayerPerspective> playerPerspectives) {
        this.playerPerspectives = playerPerspectives;
    }

    /**
     * Returns the list of localizations associated with the game.
     * @return The list of localizations associated with the game.
     */
    public List<Localization> getLocalizations() {
        return localizations;
    }

    /**
     * Sets the list of localizations associated with the game.
     * @param localizations The list of localizations associated with the game.
     */
    public void setLocalizations(List<Localization> localizations) {
        this.localizations = localizations;
    }

    /**
     * Returns the rating of the game.
     * @return The rating of the game.
     */
    public float getRating() {
        return rating;
    }

    /**
     * Sets the rating of the game.
     * @param rating The rating of the game.
     */
    public void setRating(float rating) {
        this.rating = rating;
    }

    /**
     * Returns the number of ratings for the game.
     * @return The number of ratings for the game.
     */
    public int getRatingCount() {
        return ratingCount;
    }

    /**
     * Sets the number of ratings for the game.
     * @param ratingCount The number of ratings for the game.
     */
    public void setRatingCount(int ratingCount) {
        this.ratingCount = ratingCount;
    }

    /**
     * Returns the list of involved companies associated with the game.
     * @return The list of involved companies associated with the game.
     */
    public List<CompanyGame> getInvolvedCompanies() {
        return involvedCompanies;
    }

    /**
     * Sets the list of involved companies associated with the game.
     * @param involvedCompanies The list of involved companies associated with the game.
     */
    public void setInvolvedCompanies(List<CompanyGame> involvedCompanies) {
        this.involvedCompanies = involvedCompanies;
    }

    /**
     * Returns the list of platforms associated with the game.
     * @return The list of platforms associated with the game.
     */
    public List<Platform> getPlatforms() {
        return platforms;
    }

    /**
     * Sets the list of platforms associated with the game.
     * @param platforms The list of platforms associated with the game.
     */
    public void setPlatforms(List<Platform> platforms) {
        this.platforms = platforms;
    }

    /**
     * Returns the list of requirements associated with the game.
     * @return The list of requirements associated with the game.
     */
    public List<Requirement> getRequirements() {
        return requirements;
    }

    /**
     * Sets the list of requirements associated with the game.
     * @param requirements The list of requirements associated with the game.
     */
    public void setRequirements(List<Requirement> requirements) {
        this.requirements = requirements;
    }

    @Override
    public String toString() {
        return "Game{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", cover='" + cover + '\'' +
                ", releaseDates=" + releaseDates +
                ", ageRatings=" + ageRatings +
                ", storyLine='" + storyLine + '\'' +
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Game game)) return false;

        if (id != game.id) return false;
        if (Float.compare(game.rating, rating) != 0) return false;
        if (ratingCount != game.ratingCount) return false;
        if (!Objects.equals(title, game.title)) return false;
        if (!Objects.equals(cover, game.cover)) return false;
        if (!Objects.equals(releaseDates, game.releaseDates)) return false;
        if (!Objects.equals(ageRatings, game.ageRatings)) return false;
        if (!Objects.equals(storyLine, game.storyLine)) return false;
        if (!Objects.equals(genres, game.genres)) return false;
        if (!Objects.equals(themes, game.themes)) return false;
        if (!Objects.equals(franchises, game.franchises)) return false;
        if (!Objects.equals(gameModes, game.gameModes)) return false;
        if (!Objects.equals(playerPerspectives, game.playerPerspectives))
            return false;
        if (!Objects.equals(localizations, game.localizations)) return false;
        if (!Objects.equals(involvedCompanies, game.involvedCompanies))
            return false;
        if (!Objects.equals(platforms, game.platforms)) return false;
        return Objects.equals(requirements, game.requirements);
    }
}