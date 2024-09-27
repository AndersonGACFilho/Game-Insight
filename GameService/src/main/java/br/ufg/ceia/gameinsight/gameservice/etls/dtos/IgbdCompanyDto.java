package br.ufg.ceia.gameinsight.gameservice.etls.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

public class IgbdCompanyDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty("id")
    private Integer id;

    @JsonProperty(required = false, value = "updated_at")
    private Integer updatedAt;

    @JsonProperty(required = false, value = "name")
    private String name;

    @JsonProperty(required = false, value = "description")
    private String description;

    @JsonProperty(required = false, value = "logo")
    private Integer logo;

    /**
     * The constructor.
     */
    public IgbdCompanyDto() {

    }

    /**
     * The constructor.
     * @param id The id.
     * @param name The name.
     * @param description The description.
     * @param logo The logo.
     */
    public IgbdCompanyDto(Integer id, String name, String description, Integer logo) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.logo = logo;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Integer getLogo() {
        return logo;
    }

    public void setLogo(Integer logo) {
        this.logo = logo;
    }

    public Instant getUpdatedAt() {
        return Instant.ofEpochSecond(updatedAt);
    }

    public void setUpdatedAt(Integer updatedAt) {
        this.updatedAt = updatedAt;
    }
}