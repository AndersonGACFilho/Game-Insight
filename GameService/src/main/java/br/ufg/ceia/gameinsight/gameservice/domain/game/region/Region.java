package br.ufg.ceia.gameinsight.gameservice.domain.game.region;

import jakarta.persistence.*;
import org.springframework.stereotype.Repository;

/**
 *
 */
@Entity
@Table(name = "region")
public class Region {
    /**
     * The unique identifier of the region.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The name of the region.
     */
    private String name;

    /**
     * The unique identifier of the region.
     * The identifier is a unique string that identifies the region.
     */
    private String identifier;

    /**
     * The category of the region.
     * The category is a classification system used to determine the category of a region.
     * For example, the category of a region can be "continent", "locale", ...
     */
    private String category;

    public Region() {
    }

    public Region(Long id, String name, String identifier, String category) {
        this.id = id;
        this.name = name;
        this.identifier = identifier;
        this.category = category;
    }

    public Region(String name, String identifier, String category) {
        this.name = name;
        this.identifier = identifier;
        this.category = category;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getCategory() {
        return category;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Region{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", identifier='" + identifier + '\'' +
                ", category='" + category + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Region region)) return false;
        return id.equals(region.id) && name.equals(region.name) && identifier.equals(region.identifier) && category.equals(region.category);
    }
}
