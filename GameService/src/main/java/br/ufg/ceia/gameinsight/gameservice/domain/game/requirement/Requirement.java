package br.ufg.ceia.gameinsight.gameservice.domain.game.requirement;

import br.ufg.ceia.gameinsight.gameservice.domain.game.Game;
import br.ufg.ceia.gameinsight.gameservice.domain.game.requirement.system.SystemRequirement;
import jakarta.persistence.*;
import java.io.Serial;
import java.io.Serializable;

@Entity
@Table(name = "requirement")
public class Requirement implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    private SystemRequirement minimum;

    @OneToOne
    private SystemRequirement recommended;

    // Adicionando o relacionamento ManyToOne com Game
    @ManyToOne
    @JoinColumn(name = "game_id")
    private Game game;

    public Requirement() {
    }

    public Requirement(Integer id, SystemRequirement minimum, SystemRequirement recommended, Game game) {
        this.id = id;
        this.minimum = minimum;
        this.recommended = recommended;
        this.game = game;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public SystemRequirement getMinimum() {
        return minimum;
    }

    public void setMinimum(SystemRequirement minimum) {
        this.minimum = minimum;
    }

    public SystemRequirement getRecommended() {
        return recommended;
    }

    public void setRecommended(SystemRequirement recommended) {
        this.recommended = recommended;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }
}