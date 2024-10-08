package br.ufg.ceia.gameinsight.userservice.domain.user.pcConfig.parts;

import java.io.Serial;
import java.io.Serializable;

/**
 * Represents the GPU of a user's PC.
 * <br>
 * This class is part of the User's PC configuration.
 * <br>
 * This class includes the GPU of the user's PC.
 */
public class GPU implements Serializable {
    /**
     * The serial version UID.
     */
    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * The name of the GPU.
     */
    private String name;

    public GPU() {
    }

    /**
     * Creates a GPU with the given name.
     *
     * @param name The name of the GPU.
     */
    public GPU(String name) {
        this.name = name;
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
}
