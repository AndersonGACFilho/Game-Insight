package br.ufg.ceia.gameinsight.gameservice.domain.game.game_theme;

import br.ufg.ceia.gameinsight.gameservice.domain.game.Game;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This class represents the GameTheme entity.
 * <p>
 * This class holds details about the theme of a game.
 */
@Entity
@Table(name = "game_theme")
public class GameTheme implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * The unique identifier of the game theme.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * The Igdb identifier of the region.
     */
    private Integer igdbId;

    /**
     * The name of the game theme.
     */
    private String name;

    /**
     * The games associated with the game theme.
     */
    @ManyToMany
    @JsonIgnore
    private List<Game> games;

    public GameTheme() {
    }

    public GameTheme(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public GameTheme(String name) {
        this.name = name;
    }

    // Getters and Setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Game> getGames() {
        return games;
    }

    public void setGames(List<Game> games) {
        this.games = games;
    }

    public Integer getIgdbId() {
        return igdbId;
    }

    public void setIgdbId(Integer igdbId) {
        this.igdbId = igdbId;
    }

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

    public void removeGame(Game game) {
        this.games.remove(game);
    }

    @Override
    public String toString() {
        return "GameTheme{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", games=" + games +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GameTheme gameTheme)) return false;
        return Objects.equals(id, gameTheme.id) && name.equals(gameTheme.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}