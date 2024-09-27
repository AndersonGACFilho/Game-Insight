package br.ufg.ceia.gameinsight.gameservice.domain.game.requirement.system.parts;

import jakarta.persistence.*;
import org.springframework.stereotype.Repository;

import java.io.Serial;
import java.io.Serializable;

/**
 * Represents the GPU of a system.
 */
@Entity
@Table(name = "gpu")
public class GPU implements Serializable {

    /**
     * The serial version UID.
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * The unique identifier of the GPU.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * The name of the GPU.
     */
    private String name;

    /**
     * The constructor of the class.
     */
    public GPU() {
    }

    /**
     * The constructor of the class.
     *
     * @param id   The unique identifier of the GPU.
     * @param name The name of the GPU.
     */
    public GPU(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Returns the unique identifier of the GPU.
     *
     * @return The unique identifier of the GPU.
     */
    public Integer getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the GPU.
     *
     * @param id The unique identifier of the GPU.
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Returns the name of the GPU.
     *
     * @return The name of the GPU.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the GPU.
     *
     * @param name The name of the GPU.
     */
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "GPU{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        GPU gpu = (GPU) obj;
        return id.equals(gpu.id) && name.equals(gpu.name);
    }
}
