package br.ufg.ceia.gameinsight.gameservice.domain.game.player_perspective;

import br.ufg.ceia.gameinsight.gameservice.domain.game.Game;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.springframework.stereotype.Repository;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the PlayerPerspective entity.
 * <p>
 * This class holds details about the perspective of a player in a game.
 */
@Entity
@Table(name = "players_perspective")
public class PlayerPerspective implements Serializable {
    /**
     * The serial version UID.
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * The unique identifier of the player perspective.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * The Igdb identifier of the player perspective.
     */
    private Integer igdbId;

    /**
     * The name of the player perspective.
     */
    private String name;

    /**
     * The list of games associated with the player perspective.
     */
    @ManyToMany(mappedBy = "playerPerspectives")
    @JsonIgnore
    private List<Game> games;

    public PlayerPerspective() {
    }

    public PlayerPerspective(Integer id, String name ) {
        this.id = id;
        this.name = name;
    }

    public PlayerPerspective(String name ) {
        this.name = name;
    }

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

    public Integer getIgdbId() {
        return igdbId;
    }

    public void setIgdbId(Integer igdbId) {
        this.igdbId = igdbId;
    }

    public List<Game> getGames() {
        return games;
    }

    public void setGames(List<Game> games) {
        this.games = games;
    }

    public void addGame(Game game) {
        if (game == null) {
            return;
        }
        if (this.games == null) {
            this.games = new ArrayList<>();
            this.games.add(game);
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
}
