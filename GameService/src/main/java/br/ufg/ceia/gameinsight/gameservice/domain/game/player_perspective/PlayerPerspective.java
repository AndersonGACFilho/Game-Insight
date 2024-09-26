package br.ufg.ceia.gameinsight.gameservice.domain.game.player_perspective;

import br.ufg.ceia.gameinsight.gameservice.domain.game.Game;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.springframework.stereotype.Repository;

import java.io.Serial;
import java.io.Serializable;
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
    private long id;

    /**
     * The name of the player perspective.
     */
    private String name;

    /**
     * The description of the player perspective.
     */
    private String description;

    /**
     * The games associated with the player perspective.
     */
    @ManyToMany
    @JsonIgnore
    private List<Game> games;

    public PlayerPerspective() {
    }

    public PlayerPerspective(long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public PlayerPerspective(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
