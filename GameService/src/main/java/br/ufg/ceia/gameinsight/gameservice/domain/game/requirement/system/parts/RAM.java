package br.ufg.ceia.gameinsight.gameservice.domain.game.requirement.system.parts;

import jakarta.persistence.*;
import org.springframework.stereotype.Repository;

import java.io.Serial;
import java.io.Serializable;

/**
 * Represents the RAM of a system.
 */
@Entity
@Table(name = "ram")
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
    private Integer id;

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
    public RAM(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Returns the unique identifier of the RAM.
     * @return The unique identifier of the RAM.
     */
    public Integer getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the RAM.
     * @param id The unique identifier of the RAM.
     */
    public void setId(Integer id) {
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
