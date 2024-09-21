package br.ufg.ceia.gameinsight.gameservice.domain.game.requirement.system.parts;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.springframework.stereotype.Repository;

import java.io.Serial;
import java.io.Serializable;

/**
 * Represents the RAM of a system.
 */
@Repository
public class RAM implements Serializable {
    /**
     * The serial version UID.
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * The unique identifier of the RAM.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The name of the RAM.
     */
    private String name;

    /**
     * The constructor of the class.
     */
    public RAM() {
    }

    /**
     * The constructor of the class.
     * @param id The unique identifier of the RAM.
     * @param name The name of the RAM.
     */
    public RAM(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Returns the unique identifier of the RAM.
     * @return The unique identifier of the RAM.
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the RAM.
     * @param id The unique identifier of the RAM.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Returns the name of the RAM.
     * @return The name of the RAM.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the RAM.
     * @param name The name of the RAM.
     */
    public void setName(String name) {
        this.name = name;
    }
}
