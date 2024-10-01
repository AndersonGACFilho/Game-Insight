package br.ufg.ceia.gameinsight.gameservice.domain.game.languages;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

/**
 * Enum representing the different types of language support for games.
 */
public enum LanguageSupportType {
    AUDIO(1, "Audio"),
    SUBTITLES(2, "Subtitles"),
    INTERFACE(3, "Interface");

    private final int id;
    private final String name;

    LanguageSupportType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Retrieves the ID associated with the language support type.
     *
     * @return the ID of the language support type.
     */
    public int getId() {
        return id;
    }

    /**
     * Retrieves the name of the language support type.
     *
     * @return the name of the language support type.
     */
    public String getName() {
        return name;
    }

    /**
     * Converts an ID to its corresponding LanguageSupportType.
     *
     * @param id the ID to convert.
     * @return the corresponding LanguageSupportType, or null if not found.
     */
    public static LanguageSupportType fromId(int id) {
        for (LanguageSupportType type : LanguageSupportType.values()) {
            if (type.getId() == id) {
                return type;
            }
        }
        return null;
    }

    /**
     * Converts a name to its corresponding LanguageSupportType.
     *
     * @param name the name to convert.
     * @return the corresponding LanguageSupportType, or null if not found.
     */
    public static LanguageSupportType fromName(String name) {
        for (LanguageSupportType type : LanguageSupportType.values()) {
            if (type.getName().equalsIgnoreCase(name)) {
                return type;
            }
        }
        return null;
    }
}
