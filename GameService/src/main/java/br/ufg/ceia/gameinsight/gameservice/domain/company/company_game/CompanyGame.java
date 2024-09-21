package br.ufg.ceia.gameinsight.gameservice.domain.company.company_game;

import br.ufg.ceia.gameinsight.gameservice.domain.company.Company;
import br.ufg.ceia.gameinsight.gameservice.domain.game.Game;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.springframework.stereotype.Repository;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * This class represents the relationship between a company and a game.
 * The company game is a relationship that represents the games that a company has.
 */
@Repository
public class CompanyGame implements Serializable {
    /**
     * The serial version UID.
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * The unique identifier of the company game.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    /**
     * The company associated with the company game.
     */
    @ManyToOne
    private Company company;

    /**
     * The games associated with the company game.
     */
    @ManyToMany
    @JsonIgnore
    private List<Game> games;

    /**
     * The role of the company in the game.
     */
    private String role;

    public CompanyGame() {
    }

    public CompanyGame(long id, Company company, List<Game> games, String role) {
        this.id = id;
        this.company = company;
        this.games = games;
        this.role = role;
    }

    public long getId() {
        return id;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public List<Game> getGames() {
        return games;
    }

    public void setGames(List<Game> games) {
        this.games = games;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void addGame(Game game) {
        this.games.add(game);
    }

    public void removeGame(Game game) {
        this.games.remove(game);
    }

    public void addCompany(Company company) {
        this.company = company;
    }

    public void removeCompany(Company company) {
        this.company = null;
    }

    @Override
    public String toString() {
        return "CompanyGame{" +
                "id=" + id +
                ", company=" + company +
                ", games=" + games +
                ", role='" + role + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CompanyGame that)) return false;
        return getId() == that.getId() && getCompany().equals(that.getCompany()) && getGames().equals(that.getGames()) && getRole().equals(that.getRole());
    }
}
