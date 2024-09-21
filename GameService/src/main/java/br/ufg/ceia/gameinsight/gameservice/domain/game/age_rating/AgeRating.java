package br.ufg.ceia.gameinsight.gameservice.domain.game.age_rating;

import br.ufg.ceia.gameinsight.gameservice.domain.game.region.Region;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import org.springframework.stereotype.Repository;

import java.io.Serial;
import java.io.Serializable;

/**
 * This class represents the age rating of a game.
 * The age rating is a classification system used to determine the age group for which a game is suitable.
 */
@Repository
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
    private Long id;
    /**
     * The name of the age rating.
     */
    private String name;
    /**
     * The description of the age rating.
     */
    private String description;

    /**
     * The region of the age rating.
     */
    @ManyToOne
    private Region region;

    public AgeRating() {
    }

    public AgeRating(Long id, String name, String description, Region region) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public AgeRating(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
