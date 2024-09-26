package br.ufg.ceia.gameinsight.userservice.configs;

/**
 * The JWT response.
 */
public class JwtResponse {
    private String token;

    /**
     * Creates a new instance of the JwtResponse class.
     *
     * @param token The token.
     */
    public JwtResponse(String token) {
        this.token = token;
    }

    /**
     * Gets the token.
     *
     * @return The token.
     */
    public String getToken() {
        return token;
    }
}
