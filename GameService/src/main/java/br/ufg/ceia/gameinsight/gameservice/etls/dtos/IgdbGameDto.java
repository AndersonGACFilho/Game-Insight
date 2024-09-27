package br.ufg.ceia.gameinsight.gameservice.etls.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @brief DTO class for the IGDB Game
 * @details DTO class for the IGDB Game with all the fields
 * that are returned by the IGDB API.
 */
public class IgdbGameDto implements Serializable {
    // Serial version UID
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Id of the game in the IGDB API
     */
    @JsonProperty("id")
    private Integer id;

    /**
     * Age ratings ids of the game
     */
    @JsonProperty(required = false, value = "age_ratings")
    private List<Integer> ageRatings;

    /**
     * Alternative names ids of the game
     */
    @JsonProperty(required = false, value = "alternative_names")
    private List<Integer> alternativeNames;

    /**
     * The Francises associated with the game
     */
    @JsonProperty(required = false, value = "franchises")
    private List<Integer> franchises;

    /**
     * Id of the category of the game
     */
    @JsonProperty(required = false, value = "category")
    private Integer category;

    /**
     * Id of the cover of the game
     */
    @JsonProperty(required = false, value = "cover")
    private Integer cover;

    /**
     * Creation date of the game
     */
    @JsonProperty(required = false, value = "created_at")
    private Integer createdAt;

    /**
     * External games ids of the game
     */
    @JsonProperty(required = false, value = "external_games")
    private List<Integer> externalGames;

    /**
     * First release date of the game
     */
    @JsonProperty(required = false, value = "first_release_date")
    private Integer firstReleaseDate;

    /**
     * Game modes ids of the game
     */
    @JsonProperty(required = false, value = "game_modes")
    private List<Integer> gameModes;

    /**
     * Genres ids of the game
     */
    @JsonProperty(required = false, value = "genres")
    private List<Integer> genres;

    /**
     * Involved companies ids of the game
     */
    @JsonProperty(required = false, value = "involved_companies")
    private List<Integer> involvedCompanies;

    /**
     * Keywords ids of the game
     */
    @JsonProperty(required = false, value = "keywords")
    private List<Integer> keywords;

    /**
     * Name of the game
     */
    @JsonProperty(required = false, value = "name")
    private String name;

    /**
     * Platforms ids of the game
     */
    @JsonProperty(required = false, value = "platforms")
    private List<Integer> platforms;

    /**
     * Player perspectives ids of the game
     */
    @JsonProperty(required = false, value = "player_perspectives")
    private List<Integer> playerPerspectives;

    /**
     * Release dates ids of the game
     */
    @JsonProperty(required = false, value = "release_dates")
    private List<Integer> releaseDates;

    /**
     * Screenshots ids of the game
     */
    @JsonProperty(required = false, value = "screenshots")
    private List<Integer> screenshots;

    /**
     * Similar games ids of the game
     */
    @JsonProperty(required = false, value = "similar_games")
    private List<Integer> similarGames;

    /**
     * Slug of the game
     */
    @JsonProperty(required = false, value = "slug")
    private String slug;

    /**
     * Storyline of the game
     */
    @JsonProperty(required = false, value = "storyline")
    private String storyline;

    /**
     * Summary of the game
     */
    @JsonProperty(required = false, value = "summary")
    private String summary;

    /**
     * Tags ids of the game
     */
    @JsonProperty(required = false, value = "tags")
    private List<Integer> tags;

    /**
     * Themes ids of the game
     */
    @JsonProperty(required = false, value = "themes")
    private List<Integer> themes;

    /**
     * Update date of the game
     */
    @JsonProperty(required = false, value = "updated_at")
    private Integer updatedAt;

    /**
     * URL of the game
     */
    @JsonProperty(required = false, value = "url")
    private String url;

    /**
     * Videos ids of the game
     */
    @JsonProperty(required = false, value = "videos")
    private List<Integer> videos;

    /**
     * Websites ids of the game
     */
    @JsonProperty(required = false, value = "websites")
    private List<Integer> websites;

    /**
     * Checksum of the game
     */
    @JsonProperty(required = false, value = "checksum")
    private String checksum;

    /**
     * Game localizations ids of the game
     */
    @JsonProperty(required = false, value = "game_localizations")
    private List<Integer> gameLocalizations;

    /**
     * Total rating of the game
     */
    @JsonProperty(required = false, value = "total_rating")
    private float totalRating;

    /**
     * Total rating count of the game
     */
    @JsonProperty(required = false, value = "total_rating_count")
    private int totalRatingCount;

    // Getters and Setters for all the fields

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<Integer> getAgeRatings() {
        return ageRatings;
    }

    public void setAgeRatings(List<Integer> ageRatings) {
        this.ageRatings = ageRatings;
    }

    public List<Integer> getAlternativeNames() {
        return alternativeNames;
    }

    public void setAlternativeNames(List<Integer> alternativeNames) {
        this.alternativeNames = alternativeNames;
    }

    public Integer getCategory() {
        return category;
    }

    public void setCategory(Integer category) {
        this.category = category;
    }

    public Integer getCover() {
        return cover;
    }

    public void setCover(Integer cover) {
        this.cover = cover;
    }

    public Integer getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Integer createdAt) {
        this.createdAt = createdAt;
    }

    public List<Integer> getExternalGames() {
        return externalGames;
    }

    public void setExternalGames(List<Integer> externalGames) {
        this.externalGames = externalGames;
    }

    public Integer getFirstReleaseDate() {
        return firstReleaseDate;
    }

    public void setFirstReleaseDate(Integer firstReleaseDate) {
        this.firstReleaseDate = firstReleaseDate;
    }

    public List<Integer> getGameModes() {
        return gameModes;
    }

    public void setGameModes(List<Integer> gameModes) {
        this.gameModes = gameModes;
    }

    public List<Integer> getGenres() {
        return genres;
    }

    public void setGenres(List<Integer> genres) {
        this.genres = genres;
    }

    public List<Integer> getInvolvedCompanies() {
        return involvedCompanies;
    }

    public float getTotalRating() {
        return totalRating;
    }

    public int getTotalRatingCount() {
        return totalRatingCount;
    }

    public void setInvolvedCompanies(List<Integer> involvedCompanies) {
        this.involvedCompanies = involvedCompanies;
    }

    public List<Integer> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<Integer> keywords) {
        this.keywords = keywords;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Integer> getPlatforms() {
        return platforms;
    }

    public void setPlatforms(List<Integer> platforms) {
        this.platforms = platforms;
    }

    public List<Integer> getPlayerPerspectives() {
        return playerPerspectives;
    }

    public void setPlayerPerspectives(List<Integer> playerPerspectives) {
        this.playerPerspectives = playerPerspectives;
    }

    public List<Integer> getReleaseDates() {
        return releaseDates;
    }

    public void setReleaseDates(List<Integer> releaseDates) {
        this.releaseDates = releaseDates;
    }

    public List<Integer> getScreenshots() {
        return screenshots;
    }

    public void setScreenshots(List<Integer> screenshots) {
        this.screenshots = screenshots;
    }

    public List<Integer> getSimilarGames() {
        return similarGames;
    }

    public void setSimilarGames(List<Integer> similarGames) {
        this.similarGames = similarGames;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getStoryline() {
        return storyline;
    }

    public void setStoryline(String storyline) {
        this.storyline = storyline;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public List<Integer> getTags() {
        return tags;
    }

    public void setTags(List<Integer> tags) {
        this.tags = tags;
    }

    public List<Integer> getThemes() {
        return themes;
    }

    public void setThemes(List<Integer> themes) {
        this.themes = themes;
    }

    public Integer getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Integer updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<Integer> getVideos() {
        return videos;
    }

    public void setVideos(List<Integer> videos) {
        this.videos = videos;
    }

    public List<Integer> getWebsites() {
        return websites;
    }

    public void setWebsites(List<Integer> websites) {
        this.websites = websites;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public List<Integer> getGameLocalizations() {
        return gameLocalizations;
    }

    public void setGameLocalizations(List<Integer> gameLocalizations) {
        this.gameLocalizations = gameLocalizations;
    }

    public void setTotalRating(float totalRating) {
        this.totalRating = totalRating;
    }

    public void setTotalRatingCount(int totalRatingCount) {
        this.totalRatingCount = totalRatingCount;
    }

    public List<Integer> getFranchises() {
        return franchises;
    }

    public void setFranchises(List<Integer> franchises) {
        this.franchises = franchises;
    }

    @Override
    public String toString() {
        return "IgdbGameDto{" +
                "id=" + id +
                ", ageRatings=" + ageRatings +
                ", alternativeNames=" + alternativeNames +
                ", category=" + category +
                ", cover=" + cover +
                ", createdAt=" + createdAt +
                ", externalGames=" + externalGames +
                ", firstReleaseDate=" + firstReleaseDate +
                ", gameModes=" + gameModes +
                ", genres=" + genres +
                ", involvedCompanies=" + involvedCompanies +
                ", keywords=" + keywords +
                ", name='" + name + '\'' +
                ", platforms=" + platforms +
                ", playerPerspectives=" + playerPerspectives +
                ", releaseDates=" + releaseDates +
                ", screenshots=" + screenshots +
                ", similarGames=" + similarGames +
                ", slug='" + slug + '\'' +
                ", storyline='" + storyline + '\'' +
                ", summary='" + summary + '\'' +
                ", tags=" + tags +
                ", themes=" + themes +
                ", updatedAt=" + updatedAt +
                ", url='" + url + '\'' +
                ", videos=" + videos +
                ", websites=" + websites +
                ", checksum='" + checksum + '\'' +
                ", gameLocalizations=" + gameLocalizations +
                ", totalRating=" + totalRating +
                ", totalRatingCount=" + totalRatingCount +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IgdbGameDto that = (IgdbGameDto) o;

        if (!id.equals(that.id)) return false;
        if (!ageRatings.equals(that.ageRatings)) return false;
        if (!alternativeNames.equals(that.alternativeNames)) return false;
        if (!category.equals(that.category)) return false;
        if (!cover.equals(that.cover)) return false;
        if (!createdAt.equals(that.createdAt)) return false;
        if (!externalGames.equals(that.externalGames)) return false;
        if (!firstReleaseDate.equals(that.firstReleaseDate)) return false;
        if (!gameModes.equals(that.gameModes)) return false;
        if (!genres.equals(that.genres)) return false;
        if (!involvedCompanies.equals(that.involvedCompanies)) return false;
        if (!keywords.equals(that.keywords)) return false;
        if (!name.equals(that.name)) return false;
        if (!platforms.equals(that.platforms)) return false;
        if (!playerPerspectives.equals(that.playerPerspectives)) return false;
        if (!releaseDates.equals(that.releaseDates)) return false;
        if (!screenshots.equals(that.screenshots)) return false;
        if (!similarGames.equals(that.similarGames)) return false;
        if (!slug.equals(that.slug)) return false;
        if (!storyline.equals(that.storyline)) return false;
        if (!summary.equals(that.summary)) return false;
        if (!tags.equals(that.tags)) return false;
        if (!themes.equals(that.themes)) return false;
        if (!updatedAt.equals(that.updatedAt)) return false;
        if (!url.equals(that.url)) return false;
        if (!videos.equals(that.videos)) return false;
        if (!websites.equals(that.websites)) return false;
        if (!checksum.equals(that.checksum)) return false;
        if (totalRating != that.totalRating) return false;
        if (totalRatingCount != that.totalRatingCount) return false;
        return gameLocalizations.equals(that.gameLocalizations);
    }
}
