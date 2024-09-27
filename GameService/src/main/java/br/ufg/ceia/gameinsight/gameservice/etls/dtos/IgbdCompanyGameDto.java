package br.ufg.ceia.gameinsight.gameservice.etls.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

public class IgbdCompanyGameDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty("id")
    private Integer id;

    @JsonProperty(required = false, value = "company")
    private Integer company;

    @JsonProperty(required = false, value = "game")
    private Integer game;

    @JsonProperty(required = false, value = "updated_at")
    private Integer updatedAt;

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

    public IgbdCompanyGameDto(Integer id, Integer company, Integer game, boolean isPublisher, boolean isDeveloper, boolean isPorter, boolean isSupporter) {
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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCompany() {
        return company;
    }

    public void setCompany(Integer company) {
        this.company = company;
    }

    public Integer getGame() {
        return game;
    }

    public void setGame(Integer game) {
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

    public void setUpdatedAt(Integer updatedAt) {
        this.updatedAt = updatedAt;
    }
}