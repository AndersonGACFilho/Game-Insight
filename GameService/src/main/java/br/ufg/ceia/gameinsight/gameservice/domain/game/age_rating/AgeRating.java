package br.ufg.ceia.gameinsight.gameservice.domain.game.age_rating;

import br.ufg.ceia.gameinsight.gameservice.domain.game.region.Region;
import jakarta.persistence.*;
import java.io.Serial;
import java.io.Serializable;
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
     * The Igdb identifier of the age rating.
     */
    private Integer igdbId;

    /**
     * The description of the age rating.
     */
    private String description;

    /**
     * The region of the age rating.
     */
    @ManyToOne
    @JoinColumn(name = "region_id", nullable = true)
    private Region region;

    /**
     * Default constructor.
     */
    public AgeRating() {
    }

    /**
     * Constructor with all fields.
     *
     * @param id The unique identifier of the age rating.
     * @param description The description of the age rating.
     * @param region The region of the age rating.
     */
    public AgeRating(Integer id, String description, Region region) {
        this.id = id;
        this.description = description;
        this.region = region;
    }

    /**
     * Constructor without id (auto-generated).
     *
     * @param description The description of the age rating.
     * @param region The region of the age rating.
     */
    public AgeRating(String description, Region region) {
        this.description = description;
        this.region = region;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public Integer getIgdbId() {
        return igdbId;
    }

    public void setIgdbId(Integer igdbId) {
        this.igdbId = igdbId;
    }

    @Override
    public String toString() {
        return "AgeRating{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", region=" + region +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AgeRating)) return false;
        AgeRating that = (AgeRating) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(description, that.description) &&
                Objects.equals(region, that.region);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, description, region);
    }
}