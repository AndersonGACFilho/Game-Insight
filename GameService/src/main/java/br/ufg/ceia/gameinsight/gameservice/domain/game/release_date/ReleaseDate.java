package br.ufg.ceia.gameinsight.gameservice.domain.game.release_date;

import br.ufg.ceia.gameinsight.gameservice.domain.game.Game;
import br.ufg.ceia.gameinsight.gameservice.domain.game.region.Region;
import br.ufg.ceia.gameinsight.gameservice.domain.platform.Platform;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import org.springframework.stereotype.Repository;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Date;

/**
 * This class represents the ReleaseDate entity.
 * <p>
 * This class holds details about the release date of a game.
 */
@Repository
public class ReleaseDate implements Serializable {
    /**
     * The serial version UID.
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * The unique identifier of the release date.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    /**
     * The release date of the game.
     */
    private Date date;

    /**
     * The game associated with the release date.
     */
    @ManyToOne
    private Game game;

    /**
     * The platform of the release date.
     */
    @ManyToOne
    private Platform platform;

    /**
     * The region of the release date.
     */
    @ManyToOne
    private Region region;

    public ReleaseDate() {
    }

    public ReleaseDate(long id, Date date) {
        this.id = id;
        this.date = date;
    }

    public ReleaseDate(Date date) {
        this.date = date;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Platform getPlatform() {
        return platform;
    }

    public void setPlatform(Platform platform) {
        this.platform = platform;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    @Override
    public String toString() {
        return "ReleaseDate{" +
                "id=" + id +
                ", date=" + date +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReleaseDate that)) return false;
        return id == that.id;
    }

}
