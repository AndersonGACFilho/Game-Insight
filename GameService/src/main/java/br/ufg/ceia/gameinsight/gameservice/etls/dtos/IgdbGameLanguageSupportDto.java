package br.ufg.ceia.gameinsight.gameservice.etls.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * DTO representing the language support data from IGDB.
 */
public class IgdbGameLanguageSupportDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * The IGDB ID of the language support.
     */
    @JsonProperty(required = false, value = "id")
    private Integer id;

    /**
     * The IGDB ID of the language.
     */
    @JsonProperty(required = false, value = "language")
    private Integer language;

    /**
     * The IGDB ID of the language support type (e.g., Audio, Subtitles).
     */
    @JsonProperty(required = false, value = "language_support_type")
    private Integer type;

    /**
     * Default constructor.
     */
    public IgdbGameLanguageSupportDto() {
    }

    /**
     * Constructor with all fields.
     *
     * @param id      The IGDB ID of the language support.
     * @param language The IGDB ID of the language.
     * @param type    The IGDB ID of the language support type.
     */
    public IgdbGameLanguageSupportDto(Integer id, Integer language, Integer type) {
        this.id = id;
        this.language = language;
        this.type = type;
    }

    /**
     * Constructor without the IGDB ID (useful for creating new records where ID is auto-generated).
     *
     * @param language The IGDB ID of the language.
     * @param type     The IGDB ID of the language support type.
     */
    public IgdbGameLanguageSupportDto(Integer language, Integer type) {
        this.language = language;
        this.type = type;
    }

    // Getters and Setters

    /**
     * Gets the IGDB ID of the language support.
     *
     * @return the language support ID.
     */
    public Integer getId() {
        return id;
    }

    /**
     * Sets the IGDB ID of the language support.
     *
     * @param id the language support ID to set.
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Gets the IGDB ID of the language.
     *
     * @return the language ID.
     */
    public Integer getLanguage() {
        return language;
    }

    /**
     * Sets the IGDB ID of the language.
     *
     * @param language the language ID to set.
     */
    public void setLanguage(Integer language) {
        this.language = language;
    }

    /**
     * Gets the IGDB ID of the language support type.
     *
     * @return the type ID.
     */
    public Integer getType() {
        return type;
    }

    /**
     * Sets the IGDB ID of the language support type.
     *
     * @param type the type ID to set.
     */
    public void setType(Integer type) {
        this.type = type;
    }

    /**
     * Returns a string representation of the Language Support DTO.
     *
     * @return string representation.
     */
    @Override
    public String toString() {
        return "IgdbGameLanguageSupportDto{" +
                "id=" + id +
                ", language=" + language +
                ", type=" + type +
                '}';
    }

    /**
     * Determines equality based on all fields.
     *
     * @param o the object to compare.
     * @return true if equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IgdbGameLanguageSupportDto that = (IgdbGameLanguageSupportDto) o;

        return Objects.equals(id, that.id) &&
                Objects.equals(language, that.language) &&
                Objects.equals(type, that.type);
    }

    /**
     * Generates a hash code based on all fields.
     *
     * @return the hash code.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, language, type);
    }
}
