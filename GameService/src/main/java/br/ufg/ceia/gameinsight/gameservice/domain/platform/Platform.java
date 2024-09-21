package br.ufg.ceia.gameinsight.gameservice.domain.platform;

import br.ufg.ceia.gameinsight.gameservice.domain.game.Game;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import org.springframework.stereotype.Repository;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * This class represents the platform of a game.
 * The platform is a classification system used to determine the platform for which a game is suitable.
 */
@Repository
public class Platform implements Serializable {
    /**
     * The serial version UID.
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * The unique identifier of the platform.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The name of the platform.
     */
    private String name;

    /**
     * The abbreviation of the platform.
     */
    private String abbreviation;

    /**
     * The generation of the platform.
     */
    private String generation;

    /**
     * The games associated with the platform.
     */
    @ManyToMany
    @JsonIgnore
    private List<Game> games;

    /**
     * The constructor of the class.
     */
    public Platform() {
    }

    /**
     * The constructor of the class.
     * @param id The unique identifier of the platform.
     * @param name The name of the platform.
     * @param abbreviation The abbreviation of the platform.
     * @param generation The generation of the platform.
     */
    public Platform(Long id, String name, String abbreviation, String generation) {
        this.id = id;
        this.name = name;
        this.abbreviation = abbreviation;
        this.generation = generation;
    }

    /**
     * The constructor of the class.
     * @param name The name of the platform.
     * @param abbreviation The abbreviation of the platform.
     * @param generation The generation of the platform.
     */
    public Platform(String name, String abbreviation, String generation) {
        this.name = name;
        this.abbreviation = abbreviation;
        this.generation = generation;
    }

    /**
     * Get the unique identifier of the platform.
     * @return The unique identifier of the platform.
     */
    public Long getId() {
        return id;
    }

    /**
     * Set the unique identifier of the platform.
     * @param id The unique identifier of the platform.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Get the name of the platform.
     * @return The name of the platform.
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of the platform.
     * @param name The name of the platform.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the abbreviation of the platform.
     * @return The abbreviation of the platform.
     */
    public String getAbbreviation() {
        return abbreviation;
    }

    /**
     * Set the abbreviation of the platform.
     * @param abbreviation The abbreviation of the platform.
     */
    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    /**
     * Get the generation of the platform.
     * @return The generation of the platform.
     */
    public String getGeneration() {
        return generation;
    }

    /**
     * Set the generation of the platform.
     * @param generation The generation of the platform.
     */
    public void setGeneration(String generation) {
        this.generation = generation;
    }

    /**
     * Get the games associated with the platform.
     * @return The games associated with the platform.
     */
    public List<Game> getGames() {
        return games;
    }

    /**
     * Set the games associated with the platform.
     * @param games The games associated with the platform.
     */
    public void setGames(List<Game> games) {
        this.games = games;
    }

    /**
     * Override the toString method.
     * @return The string representation of the platform.
     */
    @Override
    public String toString() {
        return "Platform{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", abbreviation='" + abbreviation + '\'' +
                ", generation='" + generation + '\'' +
                '}';
    }

    /**
     * Override the equals' method.
     * @param o The object to compare.
     * @return True if the objects are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Platform platform)) return false;
        return getId().equals(platform.getId());
    }
}
