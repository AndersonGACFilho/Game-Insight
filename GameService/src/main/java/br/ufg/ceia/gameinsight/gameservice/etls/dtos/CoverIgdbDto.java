package br.ufg.ceia.gameinsight.gameservice.etls.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;

/**
 * This class represents the DTO for the cover image of a game from IGDB.
 */
public class CoverIgdbDto implements Serializable{
    /**
     * The serial version UID.
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * The url of the cover image.
     */
    @JsonProperty(required = false, value = "url")
    private String url;

    /**
     * The constructor of the class.
     */
    public CoverIgdbDto() {
    }

    /**
     * The getter for the url attribute.
     */
    public String getUrl() {
        return url;
    }

    /**
     * The setter for the url attribute.
     */
    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "CoverIgdbDto{" +
                "url='" + url + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CoverIgdbDto that = (CoverIgdbDto) o;

        return url.equals(that.url);
    }
}
