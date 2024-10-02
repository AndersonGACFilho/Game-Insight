package br.ufg.ceia.gameinsight.gameservice.domain.game.age_rating;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * This class represents the age rating of a game.
 * The age rating is a classification system used to determine the age group for which a game is suitable.
 */
@Entity
@Table(name = "age_rating")
public class AgeRating implements Serializable {

    /**
     * The serial version UID.
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * The unique identifier of the age rating.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * The updated at date of the age rating.
     */
    @JsonProperty("updated_at")
    private Instant updatedAt;

    /**
     * The Igdb identifier of the age rating.
     */
    private Integer igdbId;
    
    /**
     * The category of the age rating.
     */
    @Enumerated(EnumType.STRING)
    private CategoryEnum category;

    /**
     * The age rating rating.
     */
    @Enumerated(EnumType.STRING)
    private RatingEnum rating;

    /**
     * Default constructor.
     */
    public AgeRating() {
    }

    /**
     * Constructor with all fields.
     *
     * @param id The unique identifier of the age rating.
     * @param category The rating of the age rating.
     * @param rating The rating of the age rating.
     */
    public AgeRating(Integer id, CategoryEnum category, RatingEnum rating) {
        this.id = id;
        this.category = category;
        this.rating = rating;
    }

    /**
     * Constructor without id (auto-generated).
     *
     * @param category The rating of the age rating.
     * @param rating The rating of the age rating.
     */
    public AgeRating(CategoryEnum category, RatingEnum rating) {
        this.category = category;
        this.rating = rating;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public CategoryEnum getCategory() {
        return category;
    }

    public void setCategory(CategoryEnum category) {
        this.category = category;
    }

    public Integer getIgdbId() {
        return igdbId;
    }

    public void setIgdbId(Integer igdbId) {
        this.igdbId = igdbId;
    }

    public RatingEnum getRating() {
        return rating;
    }

    public void setRating(RatingEnum rating) {
        this.rating = rating;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "AgeRating{" +
                "id=" + id +
                ", category=" + category +
                ", rating=" + rating +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AgeRating that)) return false;
        return Objects.equals(id, that.id) &&
                Objects.equals(category, that.category) &&
                Objects.equals(rating, that.rating);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, category, rating);
    }
}