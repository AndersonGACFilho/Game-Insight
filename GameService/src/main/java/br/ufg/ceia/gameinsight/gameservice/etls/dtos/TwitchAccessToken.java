package br.ufg.ceia.gameinsight.gameservice.etls.dtos;

import java.util.Objects;

public class TwitchAccessToken {
    private String access_token;
    private Integer expires_in;
    private String token_type;

    public TwitchAccessToken() {
    }

    public TwitchAccessToken(String access_token, Integer expires_in, String token_type) {
        this.access_token = access_token;
        this.expires_in = expires_in;
        this.token_type = token_type;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public Integer getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(Integer expires_in) {
        this.expires_in = expires_in;
    }

    public String getToken_type() {
        return token_type;
    }

    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }

    @Override
    public String toString() {
        return "TwitchAccessToken{" +
                "access_token='" + access_token + '\'' +
                ", expires_in=" + expires_in +
                ", token_type='" + token_type + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TwitchAccessToken that = (TwitchAccessToken) o;

        if (!Objects.equals(access_token, that.access_token)) return false;
        if (!Objects.equals(expires_in, that.expires_in)) return false;
        return Objects.equals(token_type, that.token_type);
    }
}
