package br.ufg.ceia.gameinsight.gameservice.domain.game.age_rating;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enum representing various game rating categories with associated integer values.
 */
public enum CategoryEnum {
    ESRB(1, "ESRB"),
    PEGI(2, "PEGI"),
    CERO(3, "CERO"),
    USK(4, "USK"),
    GRAC(5, "GRAC"),
    CLASS_IND(6, "CLASS_IND"),
    ACB(7, "ACB");

    private final int id;
    private final String name;

    /**
     * Constructor to associate each enum constant with its integer value and name.
     *
     * @param id   The integer value of the category.
     * @param name The name of the category.
     */
    CategoryEnum(int id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Retrieves the integer value associated with the category.
     *
     * @return The integer value of the category.
     */
    @JsonValue
    public int getId() {
        return id;
    }

    /**
     * Retrieves the name of the category.
     *
     * @return The name of the category.
     */
    public String getName() {
        return name;
    }

    /**
     * Creates a CategoryEnum from its integer value.
     *
     * @param id The integer value of the category.
     * @return The corresponding CategoryEnum.
     * @throws IllegalArgumentException If the id does not match any CategoryEnum.
     */
    @JsonCreator
    public static CategoryEnum fromId(int id) {
        for (CategoryEnum category : CategoryEnum.values()) {
            if (category.getId() == id) {
                return category;
            }
        }
        throw new IllegalArgumentException("Invalid CategoryEnum id: " + id);
    }
}
