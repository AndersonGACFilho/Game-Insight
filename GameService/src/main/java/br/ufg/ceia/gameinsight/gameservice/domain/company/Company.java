package br.ufg.ceia.gameinsight.gameservice.domain.company;

import br.ufg.ceia.gameinsight.gameservice.domain.company.company_game.CompanyGame;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * This class represents a company involved in game development or publishing.
 * A company can be associated with multiple games.
 */
@Entity
public class Company implements Serializable {

    /**
     * The serial version UID.
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * The unique identifier of the company.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    /**
     * The company logo URL.
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
     * The games that the company has involvement with.
     */
    @OneToMany(mappedBy = "company")
    @JsonIgnore
    private List<CompanyGame> companyGames;

    /**
     * Default constructor.
     */
    public Company() {
    }

    /**
     * Constructor with parameters.
     * @param id The unique identifier of the company.
     * @param logoUrl The logo URL of the company.
     * @param name The name of the company.
     * @param description The description of the company.
     */
    public Company(long id, String logoUrl, String name, String description) {
        this.id = id;
        this.logoUrl = logoUrl;
        this.name = name;
        this.description = description;
    }

    /**
     * Constructor without id.
     * @param logoUrl The logo URL of the company.
     * @param name The name of the company.
     * @param description The description of the company.
     */
    public Company(String logoUrl, String name, String description) {
        this.logoUrl = logoUrl;
        this.name = name;
        this.description = description;
    }

    /**
     * Get the unique identifier of the company.
     * @return The unique identifier of the company.
     */
    public long getId() {
        return id;
    }

    /**
     * Set the unique identifier of the company.
     * @param id The unique identifier of the company.
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Get the logo URL of the company.
     * @return The logo URL of the company.
     */
    public String getLogoUrl() {
        return logoUrl;
    }

    /**
     * Set the logo URL of the company.
     * @param logoUrl The logo URL of the company.
     */
    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    /**
     * Get the name of the company.
     * @return The name of the company.
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of the company.
     * @param name The name of the company.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the description of the company.
     * @return The description of the company.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the description of the company.
     * @param description The description of the company.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get the list of games associated with the company.
     * @return The list of games associated with the company.
     */
    public List<CompanyGame> getCompanyGames() {
        return companyGames;
    }

    /**
     * Set the list of games associated with the company.
     * @param companyGames The list of games associated with the company.
     */
    public void setCompanyGames(List<CompanyGame> companyGames) {
        this.companyGames = companyGames;
    }

    /**
     * Override the toString method.
     * @return The string representation of the company.
     */
    @Override
    public String toString() {
        return "Company{" +
                "id=" + id +
                ", logoUrl='" + logoUrl + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    /**
     * Override the equals method.
     * @param o The object to compare.
     * @return True if the objects are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Company)) return false;
        Company company = (Company) o;
        return id == company.id &&
                Objects.equals(logoUrl, company.logoUrl) &&
                Objects.equals(name, company.name) &&
                Objects.equals(description, company.description);
    }

    /**
     * Override the hashCode method.
     * @return The hash code of the object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, logoUrl, name, description);
    }
}