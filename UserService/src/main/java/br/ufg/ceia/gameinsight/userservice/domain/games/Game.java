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
     * The title of the game.
     */
    @Field
    private String title;

    /**
     * The genres of the game.
     */
    @Field
    private List<String> genre;

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
     * Gets the genres of the game.
     *
     * @return The genres of the game.
     */
    public List<String> getGenre() {
        return genre;
    }

    /**
     * Sets the genres of the game.
     *
     * @param genres The genre of the game.
     */
    public void setGenre(List<String> genres) {
        this.genre = genres;
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

    // Add and Remove Achievement
    /**
     * Adds an achievement to the list of achievements associated with the game.
     *
     * @param achievement The achievement to add to the list of achievements associated with the game.
     */
    public void addAchievement(Achievement achievement) {
        this.achievements.add(achievement);
    }

    /**
     * Removes an achievement from the list of achievements associated with the game.
     *
     * @param achievement The achievement to remove from the list of achievements associated with the game.
     */
    public void removeAchievement(Achievement achievement) {
        this.achievements.remove(achievement);
    }

    // Add and Remove Genre
    /**
     * Adds a genre to the list of genres associated with the game.
     *
     * @param genre The genre to add to the list of genres associated with the game.
     */
    public void addGenre(String genre) {
        this.genre.add(genre);
    }

    /**
     * Removes a genre from the list of genres associated with the game.
     *
     * @param genre The genre to remove from the list of genres associated with the game.
     */
    public void removeGenre(String genre) {
        this.genre.remove(genre);
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
        return Objects.equals(title, game.title) &&
            Objects.equals(genre, game.genre) &&
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
        return Objects.hash( title, genre,
                thumbnail, achievements);
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
                ", title='" + title + '\'' +
                ", genre='" + genre + '\'' +
                ", thumbnail='" + thumbnail + '\'' +
                ", achievements=" + achievements +
                '}';
    }
}