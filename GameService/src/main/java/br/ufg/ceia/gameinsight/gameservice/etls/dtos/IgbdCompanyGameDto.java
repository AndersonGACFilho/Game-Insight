package br.ufg.ceia.gameinsight.gameservice.etls.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

public class IgbdCompanyGameDto implements Serializable {
    @Serial
    private static final Long serialVersionUID = 1L;

    @JsonProperty("id")
    private Long id;

    @JsonProperty(required = false, value = "company")
    private Long company;

    @JsonProperty(required = false, value = "game")
    private Long game;

    @JsonProperty(required = false, value = "updated_at")
    private Long updatedAt;

    /**
     * The role of the company in the game.
     */
    @JsonProperty(required = false, value = "publisher")
    private boolean isPublisher;

    /**
     * The role of the company in the game.
     */
    @JsonProperty(required = false, value = "developer")
    private boolean isDeveloper;

    /**
     * The role of the company in the game.
     */
    @JsonProperty(required = false, value = "porting")
    private boolean isPorter;

    /**
     * The role of the company in the game.
     */
    @JsonProperty(required = false, value = "supporting")
    private boolean isSupporter;

    public IgbdCompanyGameDto() {
    }

    public IgbdCompanyGameDto(Long id, Long company, Long game, boolean isPublisher, boolean isDeveloper, boolean isPorter, boolean isSupporter) {
        this.id = id;
        this.company = company;
        this.game = game;
        this.isPublisher = isPublisher;
        this.isDeveloper = isDeveloper;
        this.isPorter = isPorter;
        this.isSupporter = isSupporter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IgbdCompanyGameDto that)) return false;
        return Objects.equals(id, that.id) &&
                Objects.equals(game, that.game) &&
                Objects.equals(company, that.company) &&
                Objects.equals(isPublisher, that.isPublisher) &&
                Objects.equals(isDeveloper, that.isDeveloper) &&
                Objects.equals(isPorter, that.isPorter) &&
                Objects.equals(isSupporter, that.isSupporter);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, game, company, isPublisher, isDeveloper, isPorter, isSupporter);
    }

    @Override
    public String toString() {
        return "CompanyGame{" +
                "id=" + id +
                ", games=" + game +
                ", company=" + company +
                ", isPublisher=" + isPublisher +
                ", isDeveloper=" + isDeveloper +
                ", isPorter=" + isPorter +
                ", isSupporter=" + isSupporter +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCompany() {
        return company;
    }

    public void setCompany(Long company) {
        this.company = company;
    }

    public Long getGame() {
        return game;
    }

    public void setGame(Long game) {
        this.game = game;
    }

    public boolean isPublisher() {
        return isPublisher;
    }

    public void setPublisher(boolean publisher) {
        isPublisher = publisher;
    }

    public boolean isDeveloper() {
        return isDeveloper;
    }

    public void setDeveloper(boolean developer) {
        isDeveloper = developer;
    }

    public boolean isPorter() {
        return isPorter;
    }

    public void setPorter(boolean porter) {
        isPorter = porter;
    }

    public boolean isSupporter() {
        return isSupporter;
    }

    public void setSupporter(boolean supporter) {
        isSupporter = supporter;
    }

    public Instant getUpdatedAt() {
        return Instant.ofEpochSecond(updatedAt);
    }

    public void setUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }
}