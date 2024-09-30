package br.ufg.ceia.gameinsight.gameservice.domain.game.languages;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * Entity representing a language.
 */
@Entity
@Table(name = "languages")
public class Language implements Serializable {

    /**
     * The serial version UID.
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * The unique identifier of the language.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * The IGDB identifier of the language.
     */
    @Column(name = "igdb_id")
    @JsonProperty("id")
    private Integer igdbId;

    /**
     * The name of the language.
     */
    @Column(nullable = false, length = 255)
    private String name;

    /**
     * The native name of the language.
     */
    @Column(name = "native_name", length = 255)
    @JsonProperty("native_name")
    private String nativeName;

    /**
     * The locale of the language.
     */
    @Column(length = 10)
    private String locale;

    /**
     * Default constructor.
     */
    public Language() {
    }

    /**
     * Constructor with all fields except collections.
     *
     * @param id         The unique identifier.
     * @param name       The name of the language.
     * @param nativeName The native name of the language.
     * @param locale     The locale of the language.
     */
    public Language(Integer id, String name, String nativeName, String locale) {
        this.id = id;
        this.name = name;
        this.nativeName = nativeName;
        this.locale = locale;
    }

    /**
     * Constructor without id.
     *
     * @param name       The name of the language.
     * @param nativeName The native name of the language.
     * @param locale     The locale of the language.
     */
    public Language(String name, String nativeName, String locale) {
        this.name = name;
        this.nativeName = nativeName;
        this.locale = locale;
    }

    /**
     * Constructor without id and locale.
     *
     * @param name       The name of the language.
     * @param nativeName The native name of the language.
     */
    public Language(String name, String nativeName) {
        this.name = name;
        this.nativeName = nativeName;
    }

    /**
     * Gets the unique identifier.
     *
     * @return the ID.
     */
    public Integer getId() {
        return id;
    }

    /**
     * Sets the unique identifier.
     *
     * @param id the ID to set.
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Gets the IGDB identifier.
     *
     * @return the IGDB ID.
     */
    public Integer getIgdbId() {
        return igdbId;
    }

    /**
     * Sets the IGDB identifier.
     *
     * @param igdbId the IGDB ID to set.
     */
    public void setIgdbId(Integer igdbId) {
        this.igdbId = igdbId;
    }

    /**
     * Gets the name of the language.
     *
     * @return the name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the language.
     *
     * @param name the name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the native name of the language.
     *
     * @return the native name.
     */
    public String getNativeName() {
        return nativeName;
    }

    /**
     * Sets the native name of the language.
     *
     * @param nativeName the native name to set.
     */
    public void setNativeName(String nativeName) {
        this.nativeName = nativeName;
    }

    /**
     * Gets the locale of the language.
     *
     * @return the locale.
     */
    public String getLocale() {
        return locale;
    }

    /**
     * Sets the locale of the language.
     *
     * @param locale the locale to set.
     */
    public void setLocale(String locale) {
        this.locale = locale;
    }

    /**
     * Returns a string representation of the Language.
     *
     * @return string representation.
     */
    @Override
    public String toString() {
        return "Language{" +
                "id=" + id +
                ", igdbId=" + igdbId +
                ", name='" + name + '\'' +
                ", nativeName='" + nativeName + '\'' +
                ", locale='" + locale + '\'' +
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
        if (!(o instanceof Language that)) return false;
        return Objects.equals(id, that.id) &&
                Objects.equals(igdbId, that.igdbId) &&
                Objects.equals(name, that.name) &&
                Objects.equals(nativeName, that.nativeName) &&
                Objects.equals(locale, that.locale);
    }

    /**
     * Generates a hash code based on all fields.
     *
     * @return the hash code.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, igdbId, name, nativeName, locale);
    }
}
