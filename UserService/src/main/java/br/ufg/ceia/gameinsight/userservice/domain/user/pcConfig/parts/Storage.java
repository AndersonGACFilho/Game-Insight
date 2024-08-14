package br.ufg.ceia.gameinsight.userservice.domain.user.pcConfig.parts;

import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serial;
import java.io.Serializable;

/**
 * Represents the storage of a user's PC.
 * <br>
 * This class is part of the User's PC configuration.
 * <br>
 * This class includes:
 * <ul>
 *     <li>The size of the storage.</li>
 *     <li>The type of the storage.</li>
 * </ul>
 */
public class Storage implements Serializable {
    /**
     * The serial version UID.
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * The size of the storage.
     */
    @Field
    private int size;

    /**
     * The type of the storage.
     */
    @Field
    private String type;

    public Storage() {
    }

    /**
     * Creates a Storage object with the given size.
     *
     * @param size The size of the storage.
     * @param type The type of the storage.
     */
    public Storage(int size, String type) {
        this.size = size;
        this.type = type;
    }

    /**
     * Returns the size of the storage.
     *
     * @return The size of the storage.
     */
    public int getSize() {
        return size;
    }

    /**
     * Sets the size of the storage.
     *
     * @param size The size of the storage.
     */
    public void setSize(int size) {
        this.size = size;
    }

    /**
     * Returns the type of the storage.
     *
     * @return The type of the storage.
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the type of the storage.
     *
     * @param type The type of the storage.
     */
    public void setType(String type) {
        this.type = type;
    }
}
