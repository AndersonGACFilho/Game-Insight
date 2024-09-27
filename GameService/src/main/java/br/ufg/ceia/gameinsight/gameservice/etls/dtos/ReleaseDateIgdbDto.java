package br.ufg.ceia.gameinsight.gameservice.etls.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;

/**
 * This class represents the DTO for the cover image of a game from IGDB.
 */
public class ReleaseDateIgdbDto implements Serializable{
    /**
     * The serial version UID.
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * The identifier of the release date.
     */
    @JsonProperty(required = false, value = "id")
    private Integer id;

    /**
     * The release date of the game.
     */
    @JsonProperty(required = false, value = "date")
    private Integer date;

    /**
     * The platform of the release date.
     */
    @JsonProperty(required = false, value = "platform")
    private Integer platform;

    /**
     * The region of the release date.
     */
    @JsonProperty(required = false, value = "region")
    private Integer region;

    public ReleaseDateIgdbDto() {
    }

    public ReleaseDateIgdbDto(Integer id, Integer date, Integer platform, Integer region) {
        this.id = id;
        this.date = date;
        this.platform = platform;
        this.region = region;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDate() {
        return date;
    }

    public void setDate(Integer date) {
        this.date = date;
    }

    public Integer getPlatform() {
        return platform;
    }

    public void setPlatform(Integer platform) {
        this.platform = platform;
    }

    public Integer getRegion() {
        return region;
    }

    public void setRegion(Integer region) {
        this.region = region;
    }
}
