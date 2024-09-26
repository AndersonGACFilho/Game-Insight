package br.ufg.ceia.gameinsight.gameservice.domain.company.company_game;

import br.ufg.ceia.gameinsight.gameservice.domain.company.Company;
import br.ufg.ceia.gameinsight.gameservice.domain.game.Game;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "company_game")
@Getter
@Setter
public class CompanyGame implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany
    @JoinTable(
            name = "game_company_game", // Tabela de junção para relação Many-to-Many
            joinColumns = @JoinColumn(name = "company_game_id"),
            inverseJoinColumns = @JoinColumn(name = "game_id")
    )
    private List<Game> games;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    private String role;

    public CompanyGame() {
    }

    public CompanyGame(Long id, List<Game> games, Company company, String role) {
        this.id = id;
        this.games = games;
        this.company = company;
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CompanyGame that)) return false;
        return Objects.equals(id, that.id) &&
                Objects.equals(games, that.games) &&
                Objects.equals(company, that.company) &&
                Objects.equals(role, that.role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, games, company, role);
    }

    @Override
    public String toString() {
        return "CompanyGame{" +
                "id=" + id +
                ", games=" + games +
                ", company=" + company +
                ", role='" + role + '\'' +
                '}';
    }
}