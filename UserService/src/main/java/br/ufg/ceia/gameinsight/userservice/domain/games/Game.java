package br.ufg.ceia.gameinsight.userservice.domain.games;

import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * This class represents the Game entity.
 * <p>
 * This class holds details about a game associated with a user's profile.
 */
public class Game {

    /**
     * The unique identifier for the game.
     */
    @Field
    private String gameId;

    /**
     * The title of the game.
     */
    @Field
    private String title;

    /**
     * The genre of the game.
     */
    @Field
    private String genre;

    /**
     * The release date of the game.
     */
    @Field
    private Date releaseDate;

    /**
     * The developer of the game.
     */
    @Field
    private String developer;

    /**
     * The thumbnail image URL of the game.
     */
    @Field
    private String thumbnail;

    /**
     * The list of achievements associated with the game.
     */
    @Field
    private List<Achievement> achievements;

    // Getters and setters

    /**
     * Gets the unique identifier for the game.
     *
     * @return The unique identifier for the game.
     */
    public String getGameId() {
        return gameId;
    }

    /**
     * Sets the unique identifier for the game.
     *
     * @param gameId The unique identifier for the game.
     */
    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    /**
     * Gets the list of achievements associated with the game.
     *
     * @return The list of achievements associated with the game.
     */
    public List<Achievement> getAchievements() {
        return achievements;
    }

    /**
     * Sets the list of achievements associated with the game.
     *
     * @param achievements The list of achievements associated with the game.
     */
    public void setAchievements(List<Achievement> achievements) {
        this.achievements = achievements;
    }

    /**
     * Gets the thumbnail image URL of the game.
     *
     * @return The thumbnail image URL of the game.
     */
    public String getThumbnail() {
        return thumbnail;
    }

    /**
     * Sets the thumbnail image URL of the game.
     *
     * @param thumbnail The thumbnail image URL of the game.
     */
    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    /**
     * Gets the developer of the game.
     *
     * @return The developer of the game.
     */
    public String getDeveloper() {
        return developer;
    }

    /**
     * Sets the developer of the game.
     *
     * @param developer The developer of the game.
     */
    public void setDeveloper(String developer) {
        this.developer = developer;
    }

    /**
     * Gets the release date of the game.
     *
     * @return The release date of the game.
     */
    public Date getReleaseDate() {
        return releaseDate;
    }

    /**
     * Sets the release date of the game.
     *
     * @param releaseDate The release date of the game.
     */
    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    /**
     * Gets the genre of the game.
     *
     * @return The genre of the game.
     */
    public String getGenre() {
        return genre;
    }

    /**
     * Sets the genre of the game.
     *
     * @param genre The genre of the game.
     */
    public void setGenre(String genre) {
        this.genre = genre;
    }

    /**
     * Gets the title of the game.
     *
     * @return The title of the game.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of the game.
     *
     * @param title The title of the game.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    // Equals and hashcode

    /**
     * Compares this game to another object.
     *
     * @param o The object to compare to.
     * @return True if the objects are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Game game = (Game) o;
        return Objects.equals(gameId, game.gameId) &&
            Objects.equals(title, game.title) &&
            Objects.equals(genre, game.genre) &&
            Objects.equals(releaseDate, game.releaseDate) &&
            Objects.equals(developer, game.developer) &&
            Objects.equals(thumbnail, game.thumbnail) &&
            Objects.equals(achievements, game.achievements);
    }

    /**
     * Returns the hash code value for this game.
     *
     * @return The hash code value for this game.
     */
    @Override
    public int hashCode() {
        return Objects.hash(gameId, title, genre,
                releaseDate, developer, thumbnail,
                achievements);
    }

    // ToString
    /**
     * Returns a string representation of the Game object.
     *
     * @return A string representation of the Game object.
     */
    @Override
    public String toString() {
        return "Game{" +
                "gameId='" + gameId + '\'' +
                ", title='" + title + '\'' +
                ", genre='" + genre + '\'' +
                ", releaseDate=" + releaseDate +
                ", developer='" + developer + '\'' +
                ", thumbnail='" + thumbnail + '\'' +
                ", achievements=" + achievements +
                '}';
    }
}