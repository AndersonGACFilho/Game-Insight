package br.ufg.ceia.gameinsight.gameservice.domain.game.localization;

import br.ufg.ceia.gameinsight.gameservice.domain.game.Game;
import br.ufg.ceia.gameinsight.gameservice.domain.game.region.Region;
import jakarta.persistence.*;
import org.springframework.stereotype.Repository;

import java.io.Serial;
import java.io.Serializable;

/**
 * This class represents the localization of a game.
 * The localization is a classification system used to determine the language and region for which a game is suitable.
 */
@Entity
@Table(name = "localization")
public class Localization implements Serializable {
    /**
     * The serial version UID.
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * The unique identifier of the localization.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * The name of the localization.
     */
    private String name;
    /**
     * The description of the localization.
     */
    private String description;

    /**
     * The region of the localization.
     */
    @ManyToOne
    private Region region;

    /**
     * The game associated with the localization.
     */
    @ManyToOne
    private Game game;

    public Localization() {
    }

    public Localization(Long id, String name, String description, Region region) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public Localization(String name, String description) {
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

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    @Override
    public String toString() {
        return "Localization{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", region=" + region +
                ", game=" + game.getTitle() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Localization that)) return false;
        return getId().equals(that.getId()) && getName().equals(that.getName()) && getDescription().equals(that.getDescription()) && getRegion().equals(that.getRegion());
    }
}
