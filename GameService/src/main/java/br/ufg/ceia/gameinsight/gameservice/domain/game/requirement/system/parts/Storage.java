package br.ufg.ceia.gameinsight.gameservice.domain.game.requirement.system.parts;

import jakarta.persistence.*;
import org.springframework.stereotype.Repository;

import java.io.Serial;
import java.io.Serializable;

/**
 * Represents the storage of a system.
 */
@Entity
@Table(name = "storage")
public class Storage implements Serializable{
    /**
     * The serial version UID.
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * The unique identifier of the storage.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The name of the storage.
     */
    private String name;

    /**
     * The constructor of the class.
     */
    public Storage() {
    }

    /**
     * The constructor of the class.
     * @param id The unique identifier of the storage.
     * @param name The name of the storage.
     */
    public Storage(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Returns the unique identifier of the storage.
     * @return The unique identifier of the storage.
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the storage.
     * @param id The unique identifier of the storage.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Returns the name of the storage.
     * @return The name of the storage.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the storage.
     * @param name The name of the storage.
     */
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Storage{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Storage storage)) return false;
        return getId().equals(storage.getId());
    }

}
