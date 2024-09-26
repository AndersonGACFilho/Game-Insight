package br.ufg.ceia.gameinsight.gameservice.domain.game.franchise;

import br.ufg.ceia.gameinsight.gameservice.domain.game.Game;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Represents a franchise of games.
 */
@Entity
@Table(name = "franchise")
public class Franchise {
    /**
     * The unique identifier of the franchise.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    /**
     * The name of the franchise.
     */
    private String name;

    /**
     * The description of the franchise.
     */
    private String description;

    /**
     * The list of games associated with the franchise.
     */
    @ManyToMany
    @JsonIgnore
    private List<Game> games;

    public Franchise() {
    }

    public Franchise(long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public Franchise(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
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
}