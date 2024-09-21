package br.ufg.ceia.gameinsight.gameservice.domain.game.game_mode;

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
 * This class represents the GameMode entity.
 * <p>
 * This class holds details about the mode of a game.
 */
@Repository
public class GameMode implements Serializable{
    /**
     * The serial version UID.
     */
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

    /**
     * Returns the id of the game mode.
     * @return the id of the game mode.
     */
    public long getId() {
        return id;
    }

    /**
     * Returns the name of the game mode.
     * @return the name of the game mode.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the game mode.
     * @param name the name of the game mode.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the description of the game mode.
     * @return the description of the game mode.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the game mode.
     * @param description the description of the game mode.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns the list of games associated with the game mode.
     * @return the list of games associated with the game mode.
     */
    public List<Game> getGames() {
        return games;
    }

    /**
     * Sets the list of games associated with the game mode.
     * @param games the list of games associated with the game mode.
     */
    public void setGames(List<Game> games) {
        this.games = games;
    }

    /**
     * Adds a game to the list of games associated with the game mode.
     * @param game the game to be added.
     */
    public void addGame(Game game) {
        this.games.add(game);
    }

    /**
     * Removes a game from the list of games associated with the game mode.
     * @param game the game to be removed.
     */
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
        if (!(o instanceof GameMode gameMode)) return false;
        return getId() == gameMode.getId();
    }

}
