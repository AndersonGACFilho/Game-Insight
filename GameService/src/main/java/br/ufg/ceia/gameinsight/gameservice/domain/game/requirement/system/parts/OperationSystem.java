package br.ufg.ceia.gameinsight.gameservice.domain.game.requirement.system.parts;

import jakarta.persistence.*;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

/**
 * Represents the operational system of a system.
 */
@Entity
@Table(name = "operation_system")
public class OperationSystem implements Serializable{
    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The unique identifier of the operational system.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * The name of the operational system.
     */
    private String name;

    /**
     * The constructor of the class.
     */
    public OperationSystem() {
    }

    /**
     * The constructor of the class.
     * @param id The unique identifier of the operational system.
     * @param name The name of the operational system.
     */
    public OperationSystem(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Returns the unique identifier of the operational system.
     * @return The unique identifier of the operational system.
     */
    public Integer getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the operational system.
     * @param id The unique identifier of the operational system.
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Returns the name of the operational system.
     * @return The name of the operational system.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the operational system.
     * @param name The name of the operational system.
     */
    public void setName(String name) {
        this.name = name;
    }
}
