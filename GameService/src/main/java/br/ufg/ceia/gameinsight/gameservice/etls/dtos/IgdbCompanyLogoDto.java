package br.ufg.ceia.gameinsight.gameservice.etls.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;

public class IgdbCompanyLogoDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty("id")
    private Integer id;

    @JsonProperty(required = false, value = "url")
    private String url;

    public IgdbCompanyLogoDto() {
    }

    public IgdbCompanyLogoDto(Integer id, String url, Integer cloudinaryId) {
        this.id = id;
        this.url = url;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
