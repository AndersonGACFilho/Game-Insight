package br.ufg.ceia.gameinsight.gameservice.domain.game.age_rating;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enum representing various game ratings with associated integer values.
 */
public enum RatingEnum {
    THREE(1, "Three"),
    SEVEN(2, "Seven"),
    TWELVE(3, "Twelve"),
    SIXTEEN(4, "Sixteen"),
    EIGHTEEN(5, "Eighteen"),
    RP(6, "RP"),
    EC(7, "EC"),
    E(8, "E"),
    E10(9, "E10"),
    T(10, "T"),
    M(11, "M"),
    AO(12, "AO"),
    CERO_A(13, "CERO_A"),
    CERO_B(14, "CERO_B"),
    CERO_C(15, "CERO_C"),
    CERO_D(16, "CERO_D"),
    CERO_Z(17, "CERO_Z"),
    USK_0(18, "USK_0"),
    USK_6(19, "USK_6"),
    USK_12(20, "USK_12"),
    USK_16(21, "USK_16"),
    USK_18(22, "USK_18"),
    GRAC_ALL(23, "GRAC_ALL"),
    GRAC_TWELVE(24, "GRAC_Twelve"),
    GRAC_FIFTEEN(25, "GRAC_Fifteen"),
    GRAC_EIGHTEEN(26, "GRAC_Eighteen"),
    GRAC_TESTING(27, "GRAC_TESTING"),
    CLASS_IND_L(28, "CLASS_IND_L"),
    CLASS_IND_TEN(29, "CLASS_IND_Ten"),
    CLASS_IND_TWELVE(30, "CLASS_IND_Twelve"),
    CLASS_IND_FOURTEEN(31, "CLASS_IND_Fourteen"),
    CLASS_IND_SIXTEEN(32, "CLASS_IND_Sixteen"),
    CLASS_IND_EIGHTEEN(33, "CLASS_IND_Eighteen"),
    ACB_G(34, "ACB_G"),
    ACB_PG(35, "ACB_PG"),
    ACB_M(36, "ACB_M"),
    ACB_MA15(37, "ACB_MA15"),
    ACB_R18(38, "ACB_R18"),
    ACB_RC(39, "ACB_RC");

    private final int id;
    private final String name;

    /**
     * Constructor to associate each enum constant with its integer value and name.
     *
     * @param id   The integer value of the rating.
     * @param name The name of the rating.
     */
    RatingEnum(int id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Retrieves the integer value associated with the rating.
     *
     * @return The integer value of the rating.
     */
    @JsonValue
    public int getId() {
        return id;
    }

    /**
     * Retrieves the name of the rating.
     *
     * @return The name of the rating.
     */
    public String getName() {
        return name;
    }

    /**
     * Converts an integer ID to its corresponding RatingEnum.
     *
     * @param id The integer ID of the rating.
     * @return The corresponding RatingEnum, or null if not found.
     */
    @JsonCreator
    public static RatingEnum fromId(int id) {
        for (RatingEnum rating : RatingEnum.values()) {
            if (rating.getId() == id) {
                return rating;
            }
        }
        return null; // Alternatively, throw an exception if preferred
    }

    /**
     * Converts a name to its corresponding RatingEnum.
     *
     * @param name The name of the rating.
     * @return The corresponding RatingEnum, or null if not found.
     */
    public static RatingEnum fromName(String name) {
        for (RatingEnum rating : RatingEnum.values()) {
            if (rating.getName().equalsIgnoreCase(name)) {
                return rating;
            }
        }
        return null; // Alternatively, throw an exception if preferred
    }
}
