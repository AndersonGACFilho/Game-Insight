package br.ufg.ceia.gameinsight.gameservice.domain.game.game_theme;

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
 * This class represents the GameTheme entity.
 * <p>
 * This class holds details about the theme of a game.
 */
@Repository
public class GameTheme implements Serializable {
    /**
     * The serial version UID.
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * The unique identifier of the game theme.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    /**
     * The name of the game theme.
     */
    private String name;

    /**
     * The description of the game theme.
     */
    private String description;

    /**
     * The games associated with the game theme.
     */
    @ManyToMany
    @JsonIgnore
    private List<Game> games;

    public GameTheme() {
    }

    public GameTheme(long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public GameTheme(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
        return "GameTheme{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GameTheme)) return false;

        GameTheme gameTheme = (GameTheme) o;

        if (id != gameTheme.id) return false;
        if (!name.equals(gameTheme.name)) return false;
        return description.equals(gameTheme.description);
    }
}
