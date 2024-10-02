package br.ufg.ceia.gameinsight.gameservice.domain.company.company_game;

import br.ufg.ceia.gameinsight.gameservice.domain.company.Company;
import br.ufg.ceia.gameinsight.gameservice.domain.game.Game;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "company_game")
@Getter
@Setter
public class CompanyGame implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer igdbId;

    private Instant updatedAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "game_id")
    private Game game;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "company_id")
    private Company company;

    /**
     * The role of the company in the game.
     */
    @JsonProperty("publisher")
    private boolean isPublisher;

    /**
     * The role of the company in the game.
     */
    @JsonProperty("developer")
    private boolean isDeveloper;

    /**
     * The role of the company in the game.
     */
    @JsonProperty("porting")
    private boolean isPorter;

    /**
     * The role of the company in the game.
     */
    @JsonProperty("supporting")
    private boolean isSupporter;

    public CompanyGame() {
    }

    public CompanyGame(Integer id, Game game, Company company, String role) {
        this.id = id;
        this.game = game;
        this.company = company;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CompanyGame that)) return false;
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
                ", game=" + game +
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

    public Integer getIgdbId() {
        return igdbId;
    }

    public void setIgdbId(Integer igdbId) {
        this.igdbId = igdbId;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
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
}