package br.ufg.ceia.gameinsight.gameservice.domain.company;

import br.ufg.ceia.gameinsight.gameservice.domain.company.company_game.CompanyGame;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.springframework.stereotype.Repository;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * This class represents the relationship between a company and a game.
 * The company game is a relationship that represents the games that a company has.
 */
@Repository
public class Company implements Serializable {
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
     * The company logo url.
     */
    private String logoUrl;

    /**
     * The company name.
     */
    private String name;

    /**
     * The company description.
     */
    private String description;

    /**
     * The games that the company has involvement.
     */
    @OneToMany(mappedBy = "company")
    @JsonIgnore
    List<CompanyGame> companyGames;

    public Company() {
    }

    public Company(long id, String logoUrl, String name, String description) {
        this.id = id;
        this.logoUrl = logoUrl;
        this.name = name;
        this.description = description;
    }

    public Company(String logoUrl, String name, String description) {
        this.logoUrl = logoUrl;
        this.name = name;
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<CompanyGame> getCompanyGames() {
        return companyGames;
    }

    public void setCompanyGames(List<CompanyGame> companyGames) {
        this.companyGames = companyGames;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Company{" +
                "id=" + id +
                ", logoUrl='" + logoUrl + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Company company)) return false;

        if (id != company.id) return false;
        if (!Objects.equals(logoUrl, company.logoUrl)) return false;
        if (!Objects.equals(name, company.name)) return false;
        if (!Objects.equals(description, company.description)) return false;
        return Objects.equals(companyGames, company.companyGames);
    }

}
