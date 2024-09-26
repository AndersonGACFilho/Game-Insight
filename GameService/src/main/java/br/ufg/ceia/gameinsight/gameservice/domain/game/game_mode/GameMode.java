package br.ufg.ceia.gameinsight.gameservice.domain.game.game_mode;

import br.ufg.ceia.gameinsight.gameservice.domain.game.Game;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * This class represents the GameMode entity.
 * <p>
 * This class holds details about the mode of a game.
 */
@Entity
@Table(name = "game_mode")
public class GameMode implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * The unique identifier of the game mode.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    /**
     * The name of the game mode.
     */
    private String name;

    /**
     * The description of the game mode.
     */
    private String description;

    /**
     * The games associated with the game mode.
     */
    @ManyToMany
    @JsonIgnore
    private List<Game> games;

    public GameMode() {
    }

    public GameMode(long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public GameMode(String name, String description) {
        this.name = name;
        this.description = description;
    }

    // Getters and Setters

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Game> getGames() {
        return games;
    }

    public void setGames(List<Game> games) {
        this.games = games;
    }

    public void addGame(Game game) {
        this.games.add(game);
    }

    public void removeGame(Game game) {
        this.games.remove(game);
    }

    @Override
    public String toString() {
        return "GameMode{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", games=" + games +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GameMode)) return false;
        GameMode gameMode = (GameMode) o;
        return id == gameMode.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}