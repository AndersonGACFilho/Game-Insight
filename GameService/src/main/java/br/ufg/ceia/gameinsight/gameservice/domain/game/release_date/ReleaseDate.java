package br.ufg.ceia.gameinsight.gameservice.domain.game.release_date;

import br.ufg.ceia.gameinsight.gameservice.domain.game.Game;
import br.ufg.ceia.gameinsight.gameservice.domain.game.region.Region;
import br.ufg.ceia.gameinsight.gameservice.domain.platform.Platform;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Repository;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Date;

/**
 * This class represents the ReleaseDate entity.
 * <p>
 * This class holds details about the release date of a game.
 */
@Setter
@Getter
@Entity
@Table(name = "release_date")
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
    private Long id;

    /**
     * The release date of the game.
     */
    private long date;

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

    public ReleaseDate(long id, long date) {
        this.id = id;
        this.date = date;
    }

    public ReleaseDate(long date) {
        this.date = date;
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
        return date == that.date
            && game.equals(that.game)
            && platform.equals(that.platform)
            && region.equals(that.region);
    }

}
