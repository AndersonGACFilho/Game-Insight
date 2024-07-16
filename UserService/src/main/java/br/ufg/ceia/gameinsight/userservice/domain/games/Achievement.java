package br.ufg.ceia.gameinsight.userservice.domain.games;

import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.Objects;

/**
 * This class represents the Achievement entity.
 * <p>
 * This class holds details about an achievement in a game.
 */
public class Achievement {

    /**
     * The unique identifier for the achievement.
     */
    @Field
    private String achievementId;

    /**
     * The name of the achievement.
     */
    @Field
    private String name;

    /**
     * The description of the achievement.
     */
    @Field
    private String description;

    /**
     * The thumbnail image of the achievement.
     */
    @Field
    private String thumbnail;

    /**
     * The State of the achievement.
     */
    @Field
    private AchievementState state;

    /**
     * The date when the achievement was earned.
     */
    @Field
    private Date dateEarned;

    // Getters and setters

    /**
     * Gets the unique identifier for the achievement.
     *
     * @return The unique identifier for the achievement.
     */
    public String getAchievementId() {
        return achievementId;
    }

    /**
     * Sets the unique identifier for the achievement.
     *
     * @param achievementId The unique identifier for the achievement.
     */
    public void setAchievementId(String achievementId) {
        this.achievementId = achievementId;
    }

    /**
     * Gets the name of the achievement.
     *
     * @return The name of the achievement.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the achievement.
     *
     * @param name The name of the achievement.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the description of the achievement.
     *
     * @return The description of the achievement.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the achievement.
     *
     * @param description The description of the achievement.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the thumbnail image of the achievement.
     *
     * @return The thumbnail image of the achievement.
     */
    public String getThumbnail() {
        return thumbnail;
    }

    /**
     * Sets the thumbnail image of the achievement.
     *
     * @param thumbnail The thumbnail image of the achievement.
     */
    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    /**
     * Gets the state of the achievement.
     *
     * @return The state of the achievement.
     */
    public AchievementState getState() {
        return state;
    }

    /**
     * Sets the state of the achievement.
     *
     * @param state The state of the achievement.
     */
    public void setState(AchievementState state) {
        this.state = state;
    }

    /**
     * Gets the date when the achievement was earned.
     *
     * @return The date when the achievement was earned.
     */
    public Date getDateEarned() {
        return dateEarned;
    }

    /**
     * Sets the date when the achievement was earned.
     *
     * @param dateEarned The date when the achievement was earned.
     */
    public void setDateEarned(Date dateEarned) {
        this.dateEarned = dateEarned;
    }

    // Equals and hashcode

    /**
     * Compares this achievement with another object for equality.
     *
     * @param o The object to compare with this achievement.
     * @return True if the objects are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Achievement that = (Achievement) o;
        return Objects.equals(achievementId, that.achievementId) &&
                Objects.equals(name, that.name) &&
                Objects.equals(description, that.description) &&
                Objects.equals(thumbnail, that.thumbnail) &&
                Objects.equals(state, that.state) &&
                Objects.equals(dateEarned, that.dateEarned);
    }

    /**
     * Returns the hashcode of this achievement.
     *
     * @return The hashcode of this achievement.
     */
    @Override
    public int hashCode() {
        return Objects.hash(achievementId, name, description,
                thumbnail, state, dateEarned);
    }

    // toString
    /**
     * Returns a string representation of this achievement.
     *
     * @return A string representation of this achievement.
     */
    @Override
    public String toString() {
        return "Achievement{" +
                "achievementId='" + achievementId + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", thumbnail='" + thumbnail + '\'' +
                ", state=" + state.toString() +
                ", dateEarned=" + dateEarned +
                '}';
    }
}
