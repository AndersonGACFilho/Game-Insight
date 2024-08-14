package br.ufg.ceia.gameinsight.userservice.domain.user.pcConfig.parts;

import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serial;
import java.io.Serializable;

/**
 * Represents the RAM of a user's PC.
 * <br>
 * This class is part of the User's PC configuration.
 * <br>
 * This class includes:
 * <ul>
 *     <li>The amount of RAM in the user's PC.</li>
 *     <li>The RAM's frequency.</li>
 *     <li>The RAM's type.</li>
 * </ul>
 */
public class RAM implements Serializable {
    /**
     * The serial version UID.
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * The amount of RAM in the user's PC.
     */
    @Field
    private int amount;

    /**
     * The RAM's frequency.
     */
    @Field
    private int frequency;

    /**
     * The RAM's type.
     */
    @Field
    private String type;

    public RAM() {
    }

    /**
     * Creates a RAM object with the given amount.
     *
     * @param amount The amount of RAM in the user's PC.
     */
    public RAM(int amount) {
        this.amount = amount;
    }

    /**
     * Returns the amount of RAM in the user's PC.
     *
     * @return The amount of RAM in the user's PC.
     */
    public int getAmount() {
        return amount;
    }

    /**
     * Sets the amount of RAM in the user's PC.
     *
     * @param amount The amount of RAM in the user's PC.
     */
    public void setAmount(int amount) {
        this.amount = amount;
    }

    /**
     * Returns the RAM's frequency.
     *
     * @return The RAM's frequency.
     */
    public int getFrequency() {
        return frequency;
    }

    /**
     * Sets the RAM's frequency.
     *
     * @param frequency The RAM's frequency.
     */
    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    /**
     * Returns the RAM's type.
     *
     * @return The RAM's type.
     */
    public String getType() {
        return type;
    }
}
