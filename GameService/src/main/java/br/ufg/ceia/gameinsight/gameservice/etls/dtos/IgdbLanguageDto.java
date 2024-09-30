package br.ufg.ceia.gameinsight.gameservice.etls.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * DTO representing a language from the IGDB API.
 */
public class IgdbLanguageDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * The unique identifier of the language.
     */
    @JsonProperty("id")
    private Integer id;

    /**
     * The name of the language.
     */
    @JsonProperty("name")
    private String name;

    /**
     * The native name of the language.
     */
    @JsonProperty("native_name")
    private String nativeName;

    /**
     * The locale of the language.
     */
    @JsonProperty("locale")
    private String locale;

    /**
     * The creation timestamp (Unix epoch seconds).
     */
    @JsonProperty("created_at")
    private Integer createdAt;

    /**
     * The update timestamp (Unix epoch seconds).
     */
    @JsonProperty("updated_at")
    private Integer updatedAt;

    /**
     * The checksum of the language data.
     */
    @JsonProperty("checksum")
    private String checksum;

    /**
     * Default constructor.
     */
    public IgdbLanguageDto() {
    }

    /**
     * Parameterized constructor with all fields.
     *
     * @param id          The unique identifier of the language.
     * @param name        The name of the language.
     * @param nativeName  The native name of the language.
     * @param locale      The locale of the language.
     * @param createdAt   The creation timestamp.
     * @param updatedAt   The update timestamp.
     * @param checksum    The checksum of the language data.
     */
    public IgdbLanguageDto(Integer id, String name, String nativeName, String locale, Integer createdAt, Integer updatedAt, String checksum) {
        this.id = id;
        this.name = name;
        this.nativeName = nativeName;
        this.locale = locale;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.checksum = checksum;
    }

    // Getters and Setters

    /**
     * Gets the unique identifier of the language.
     *
     * @return The language ID.
     */
    public Integer getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the language.
     *
     * @param id The language ID to set.
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Gets the name of the language.
     *
     * @return The language name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the language.
     *
     * @param name The language name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the native name of the language.
     *
     * @return The native language name.
     */
    public String getNativeName() {
        return nativeName;
    }

    /**
     * Sets the native name of the language.
     *
     * @param nativeName The native language name to set.
     */
    public void setNativeName(String nativeName) {
        this.nativeName = nativeName;
    }

    /**
     * Gets the locale of the language.
     *
     * @return The language locale.
     */
    public String getLocale() {
        return locale;
    }

    /**
     * Sets the locale of the language.
     *
     * @param locale The language locale to set.
     */
    public void setLocale(String locale) {
        this.locale = locale;
    }

    /**
     * Gets the creation timestamp.
     *
     * @return The creation timestamp.
     */
    public Integer getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the creation timestamp.
     *
     * @param createdAt The creation timestamp to set.
     */
    public void setCreatedAt(Integer createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Gets the update timestamp.
     *
     * @return The update timestamp.
     */
    public Integer getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Sets the update timestamp.
     *
     * @param updatedAt The update timestamp to set.
     */
    public void setUpdatedAt(Integer updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Gets the checksum of the language data.
     *
     * @return The checksum.
     */
    public String getChecksum() {
        return checksum;
    }

    /**
     * Sets the checksum of the language data.
     *
     * @param checksum The checksum to set.
     */
    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    /**
     * Returns a string representation of the IgdbLanguageDto.
     *
     * @return A string representation of the object.
     */
    @Override
    public String toString() {
        return "IgdbLanguageDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", nativeName='" + nativeName + '\'' +
                ", locale='" + locale + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", checksum='" + checksum + '\'' +
                '}';
    }

    /**
     * Determines equality based on all fields.
     *
     * @param o The object to compare.
     * @return True if equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IgdbLanguageDto that = (IgdbLanguageDto) o;

        return Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(nativeName, that.nativeName) &&
                Objects.equals(locale, that.locale) &&
                Objects.equals(createdAt, that.createdAt) &&
                Objects.equals(updatedAt, that.updatedAt) &&
                Objects.equals(checksum, that.checksum);
    }

    /**
     * Generates a hash code based on all fields.
     *
     * @return The hash code.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, name, nativeName, locale, createdAt, updatedAt, checksum);
    }
}
