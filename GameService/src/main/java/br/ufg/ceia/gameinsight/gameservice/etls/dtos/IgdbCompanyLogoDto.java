package br.ufg.ceia.gameinsight.gameservice.etls.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;

public class IgdbCompanyLogoDto implements Serializable {
    @Serial
    private static final Long serialVersionUID = 1L;

    @JsonProperty("id")
    private Long id;

    @JsonProperty(required = false, value = "url")
    private String url;

    public IgdbCompanyLogoDto() {
    }

    public IgdbCompanyLogoDto(Long id, String url, Long cloudinaryId) {
        this.id = id;
        this.url = url;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
