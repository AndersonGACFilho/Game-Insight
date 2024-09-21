package br.ufg.ceia.gameinsight.gameservice.domain.game.requirement;

import br.ufg.ceia.gameinsight.gameservice.domain.game.requirement.system.SystemRequirement;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import org.springframework.stereotype.Repository;

import java.io.Serial;
import java.io.Serializable;

/**
 * This class represents the requirements of a game.
 * The requirements are the minimum and recommended hardware and software requirements for a game.
 */
@Repository
public class Requirement implements Serializable {
    /**
     * The serial version UID.
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * The unique identifier of the requirement.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The minimum hardware and software requirements for the game.
     */
    @OneToOne
    private SystemRequirement minimum;

    /**
     * The recommended hardware and software requirements for the game.
     */
    @OneToOne
    private SystemRequirement recommended;

    public Requirement() {
    }

    public Requirement(Long id, SystemRequirement minimum, SystemRequirement recommended) {
        this.id = id;
        this.minimum = minimum;
        this.recommended = recommended;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

}
