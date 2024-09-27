package br.ufg.ceia.gameinsight.gameservice.domain.game.genre;

import br.ufg.ceia.gameinsight.gameservice.domain.game.Game;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.springframework.stereotype.Repository;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * This class represents the Genre entity.
 * <p>
 * This class holds details about the genre of a game.
 */
@Entity
@Table(name = "genre")
public class Genre implements Serializable{
    /**
     * The serial version UID.
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * The unique identifier of the genre.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The name of the genre.
     */
    private String name;

    /**
     * The slug of the genre.
     */
    private String slug;

    /**
     * The list of games associated with the genre.
     */
    @ManyToMany(mappedBy = "genres")
    @JsonIgnore
    private List<Game> games;

    public Genre() {
    }

    public Genre(long id, String name, String slug) {
        this.id = id;
        this.name = name;
        this.slug = slug;
    }

    public Genre(String name, String slug) {
        this.name = name;
        this.slug = slug;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
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
        return "Genre{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", slug='" + slug + '\'' +
                ", games={" + games +
                "}}";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Genre genre = (Genre) obj;
        return id == genre.id;
    }
}
