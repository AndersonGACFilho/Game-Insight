package br.ufg.ceia.gameinsight.userservice.domain.user.pcConfig.parts;

import org.springframework.data.mongodb.core.mapping.Field;

/**
 * This class represents the User's CPU
 * <br>
 * It is a part of the User's PC Configuration
 * <br>
 * This class includes the following attributes:
 * <ul>
 *     <li>name</li>
 * </ul>
 */
public class CPU {
    /**
     * The name of the CPU
     */
    @Field
    private String name;

    /**
     * Default constructor
     */
    public CPU() {
    }

    /**
     * Constructor with parameters
     * @param name CPU's name
     */
    public CPU(String name) {
        this.name = name;
    }

    /**
     * Getter for the CPU's name
     * @return CPU's name
     */
    public String getName() {
        return name;
    }

    /**
     * Setter for the CPU's name
     * @param name CPU's name
     */
    public void setName(String name) {
        this.name = name;
    }
}
