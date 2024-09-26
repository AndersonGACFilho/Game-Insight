package br.ufg.ceia.gameinsight.userservice.domain.user.pcConfig.parts;

import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serial;
import java.io.Serializable;

/**
 * Represents the DirectX version of a user's PC.
 * <br>
 * This class is part of the User's PC configuration.
 * <br>
 * This class includes the DirectX version of the user's PC.
 */
public class DirectX implements Serializable {
    /**
     * The serial version UID.
     */
    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * The DirectX version of the user's PC.
     */
    @Field
    private String version;

    public DirectX() {
    }

    /**
     * Creates a DirectX object with the given version.
     *
     * @param version The DirectX version of the user's PC.
     */
    public DirectX(String version) {
        this.version = version;
    }

    /**
     * Returns the DirectX version of the user's PC.
     *
     * @return The DirectX version of the user's PC.
     */
    public String getVersion() {
        return version;
    }

    /**
     * Sets the DirectX version of the user's PC.
     *
     * @param version The DirectX version of the user's PC.
     */
    public DirectX setVersion(String version) {
        this.version = version;
        return this;
    }
}
