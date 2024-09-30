package br.ufg.ceia.gameinsight.gameservice.domain.game.languages;

import br.ufg.ceia.gameinsight.gameservice.domain.game.Game;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * This class represents the language support for a game.
 */
@Entity
@Table(name = "language_supports")
public class LanguageSupport implements Serializable {

    /**
     * The serial version UID.
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * The unique identifier for the language support.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("local_id")
    private Integer id;

    /**
     * The IGDB identifier of the language support.
     */
    @Column(name = "igdb_id", unique = true, nullable = false)
    @JsonProperty("id")
    private Integer igdbId;

    /**
     * The update timestamp (Unix epoch seconds).
     */
    @Column(name = "updated_at")
    private Integer updatedAt;

    /**
     * Reference to the associated game.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    /**
     * Reference to the supported language.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "language_id", nullable = false)
    private Language language;

    /**
     * Type of language support.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "language_support_type", nullable = false)
    private LanguageSupportType languageSupportType;

    /**
     * Default constructor.
     */
    public LanguageSupport() {
    }

    /**
     * Constructor with all fields.
     *
     * @param id                  The unique identifier.
     * @param igdbId              The IGDB ID of the language support.
     * @param game                The associated game.
     * @param language            The supported language.
     * @param languageSupportType The type of language support.
     */
    public LanguageSupport(Integer id, Integer igdbId, Game game, Language language, LanguageSupportType languageSupportType) {
        this.id = id;
        this.igdbId = igdbId;
        this.game = game;
        this.language = language;
        this.languageSupportType = languageSupportType;
    }

    /**
     * Constructor without the ID (for creating new records).
     *
     * @param igdbId              The IGDB ID of the language support.
     * @param game                The associated game.
     * @param language            The supported language.
     * @param languageSupportType The type of language support.
     */
    public LanguageSupport(Integer igdbId, Game game, Language language, LanguageSupportType languageSupportType) {
        this.igdbId = igdbId;
        this.game = game;
        this.language = language;
        this.languageSupportType = languageSupportType;
    }

    // Getters and Setters

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
     * Gets the associated game.
     *
     * @return the game.
     */
    public Game getGame() {
        return game;
    }

    /**
     * Sets the associated game.
     *
     * @param game the game to set.
     */
    public void setGame(Game game) {
        this.game = game;
    }

    /**
     * Gets the supported language.
     *
     * @return the language.
     */
    public Language getLanguage() {
        return language;
    }

    /**
     * Sets the supported language.
     *
     * @param language the language to set.
     */
    public void setLanguage(Language language) {
        this.language = language;
    }

    /**
     * Gets the type of language support.
     *
     * @return the language support type.
     */
    public LanguageSupportType getLanguageSupportType() {
        return languageSupportType;
    }

    /**
     * Sets the type of language support.
     *
     * @param languageSupportType the type to set.
     */
    public void setLanguageSupportType(LanguageSupportType languageSupportType) {
        this.languageSupportType = languageSupportType;
    }

    /**
     * Gets the update timestamp.
     *
     * @return the update timestamp.
     */
    public Integer getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Sets the update timestamp.
     *
     * @param updatedAt the update timestamp to set.
     */
    public void setUpdatedAt(Integer updatedAt) {
        this.updatedAt = updatedAt;
    }

    // toString
    @Override
    public String toString() {
        return "LanguageSupport{" +
                "id=" + id +
                ", igdbId=" + igdbId +
                ", game=" + (game != null ? game.getId() : null) +
                ", language=" + (language != null ? language.getId() : null) +
                ", languageSupportType=" + languageSupportType +
                '}';
    }

    // equals and hashCode

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LanguageSupport that = (LanguageSupport) o;

        return Objects.equals(id, that.id) &&
                Objects.equals(igdbId, that.igdbId) &&
                Objects.equals(game, that.game) &&
                Objects.equals(language, that.language) &&
                languageSupportType == that.languageSupportType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, igdbId, game, language, languageSupportType);
    }
}
