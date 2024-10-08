package br.ufg.ceia.gameinsight.gameservice.domain.platform;

import br.ufg.ceia.gameinsight.gameservice.domain.game.Game;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This class represents the platform of a game.
 * The platform is a classification system used to determine the platform for which a game is suitable.
 */
@Entity
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
    private Integer id;

    /**
     * The igdb identifier of the platform.
     */
    private Integer igdbId;


    /**
     * The instant that the platform was updated.
     */
    @JsonProperty("updated_at")
    private Instant updatedAt;

    /**
     * The name of the platform.
     */
    @JsonProperty("name")
    private String name;

    /**
     * The abbreviation of the platform.
     */
    @JsonProperty("abbreviation")
    private String abbreviation;

    /**
     * The generation of the platform.
     */
    @JsonProperty("generation")
    private String generation;

    /**
     * The list of games associated with the platform.
     */
    @ManyToMany(mappedBy = "platforms")
    @JsonIgnore
    private List<Game> games;

    /**
     * Default constructor.
     */
    public Platform() {
    }

    /**
     * Constructor with parameters.
     * @param id The unique identifier of the platform.
     * @param name The name of the platform.
     * @param abbreviation The abbreviation of the platform.
     * @param generation The generation of the platform.
     */
    public Platform(Integer id, String name, String abbreviation, String generation) {
        this.id = id;
        this.name = name;
        this.abbreviation = abbreviation;
        this.generation = generation;
    }

    /**
     * Constructor without id.
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
    public Integer getId() {
        return id;
    }

    /**
     * Set the unique identifier of the platform.
     * @param id The unique identifier of the platform.
     */
    public void setId(Integer id) {
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
     * Get the igdb identifier of the platform.
     * @return The igdb identifier of the platform.
     */
    public Integer getIgdbId() {
        return igdbId;
    }

    /**
     * Set the igdb identifier of the platform.
     * @param igdbId The igdb identifier of the platform.
     */
    public void setIgdbId(Integer igdbId) {
        this.igdbId = igdbId;
    }

    /**
     * Get the list of games associated with the platform.
     * @return The list of games associated with the platform.
     */
    public List<Game> getGames() {
        return games;
    }

    /**
     * Set the list of games associated with the platform.
     * @param games The list of games associated with the platform.
     */
    public void setGames(List<Game> games) {
        this.games = games;
    }

    /**
     * Get the instant that the platform was updated.
     * @return The instant that the platform was updated.
     */
    public Instant getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Set the instant that the platform was updated.
     * @param updatedAt The instant that the platform was updated.
     */
    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Add a game to the list of games associated with the platform.
     * @param game The game to add.
     */
    public void addGame(Game game) {
        if (game == null) {
            return;
        }
        if (this.games == null) {
            this.games = new ArrayList<>();
            return;
        }
        if (this.games.contains(game)) {
            return;
        }
        this.games.add(game);
    }

    /**
     * Remove a game from the list of games associated with the platform.
     * @param game The game to remove.
     */
    public void removeGame(Game game) {
        this.games.remove(game);
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
     * Override the equals method.
     * @param o The object to compare.
     * @return True if the objects are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Platform)) return false;
        Platform platform = (Platform) o;
        return Objects.equals(id, platform.id);
    }

    /**
     * Override the hashCode method.
     * @return The hash code of the object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}