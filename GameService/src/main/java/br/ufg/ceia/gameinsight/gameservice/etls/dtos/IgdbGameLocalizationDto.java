package br.ufg.ceia.gameinsight.gameservice.etls.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;

public class IgdbGameLocalizationDto implements Serializable{
    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty(required = false, value = "name")
    private String name;

    @JsonProperty(required = false, value = "region")
    private long region;

    // Getters and Setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getRegion() {
        return region;
    }

    public void setRegion(long region) {
        this.region = region;
    }

    @Override
    public String toString() {
        return "IgdbGameLocalizationDto{" +
                "name='" + name + '\'' +
                ", region=" + region +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IgdbGameLocalizationDto)) return false;
        IgdbGameLocalizationDto that = (IgdbGameLocalizationDto) o;
        return getRegion() == that.getRegion() && getName().equals(that.getName());
    }

    // Constructors

    public IgdbGameLocalizationDto() {
    }

    public IgdbGameLocalizationDto(String name, long region) {
        this.name = name;
        this.region = region;
    }
}
