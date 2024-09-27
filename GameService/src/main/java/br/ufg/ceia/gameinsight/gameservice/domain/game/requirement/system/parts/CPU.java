package br.ufg.ceia.gameinsight.gameservice.domain.game.requirement.system.parts;

import jakarta.persistence.*;
import org.springframework.stereotype.Repository;

import java.io.Serial;
import java.io.Serializable;

/**
 * Represents the CPU of a system.
 */
@Entity
@Table(name = "cpu")
public class CPU implements Serializable{
    /**
     * The serial version UID.
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * The unique identifier of the CPU.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * The name of the CPU.
     */
    private String name;

    /**
     * The constructor of the class.
     */
    public CPU() {
    }

    /**
     * The constructor of the class.
     * @param id The unique identifier of the CPU.
     * @param name The name of the CPU.
     */
    public CPU(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Returns the unique identifier of the CPU.
     * @return The unique identifier of the CPU.
     */
    public Integer getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the CPU.
     * @param id The unique identifier of the CPU.
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Returns the name of the CPU.
     * @return The name of the CPU.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the CPU.
     * @param name The name of the CPU.
     */
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "CPU{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CPU cpu)) return false;
        return getId().equals(cpu.getId());
    }
}
