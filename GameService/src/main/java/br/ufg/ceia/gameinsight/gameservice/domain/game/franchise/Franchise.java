package br.ufg.ceia.gameinsight.gameservice.domain.game.franchise;

import br.ufg.ceia.gameinsight.gameservice.domain.game.Game;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import org.springframework.stereotype.Repository;

import java.time.Instant;
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
    private Integer id;

    /**
     * The igdb identifier of the franchise.
     */
    private Integer igdbId;

    /**
     * The name of the franchise.
     */
    private String name;

    /**
     * The description of the franchise.
     */
    private String slug;

    /**
     * The instant that the franchise was updated.
     */
    @JsonProperty("updated_at")
    private Instant updatedAt;

    /**
     * The games associated with the franchise.
     */
    @ManyToMany
    @JsonIgnore
    private List<Game> games;

    public Franchise() {
    }

    public Franchise(Integer id, String name, String description) {
        this.id = id;
        this.name = name;
        this.slug = description;
    }

    public Franchise(String name, String description) {
        this.name = name;
        this.slug = description;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String setSlug() {
        return slug;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
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

    public Integer getIgdbId() {
        return igdbId;
    }

    public void setIgdbId(Integer igdbId) {
        this.igdbId = igdbId;
    }
}