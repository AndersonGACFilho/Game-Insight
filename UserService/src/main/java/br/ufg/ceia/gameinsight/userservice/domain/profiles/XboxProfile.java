package br.ufg.ceia.gameinsight.userservice.domain.profiles;

import br.ufg.ceia.gameinsight.userservice.domain.games.Game;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;
import java.util.Objects;

/**
 * This class represents the XboxProfile entity.
 * <p>
 * This class holds the Xbox profile details of the user.
 */
public class XboxProfile {

    /**
     * The username of the user on Xbox.
     */
    @Field
    private String username;

    /**
     * The login token for the user's Xbox profile.
     */
    @Field
    private String loginToken;

    /**
     * The list of games associated with the user's Xbox profile.
     */
    @Field
    private List<Game> games;

    // Getters and setters

    /**
     * Gets the username of the user on Xbox.
     *
     * @return The username of the user on Xbox.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username of the user on Xbox.
     *
     * @param username The username of the user on Xbox.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the list of games associated with the user's Xbox profile.
     *
     * @return The list of games associated with the user's Xbox profile.
     */
    public List<Game> getGames() {
        return games;
    }

    /**
     * Sets the list of games associated with the user's Xbox profile.
     *
     * @param games The list of games associated with the user's Xbox profile.
     */
    public void setGames(List<Game> games) {
        this.games = games;
    }

    /**
     * Gets the login token for the user's Xbox profile.
     *
     * @return The login token for the user's Xbox profile.
     */
    public String getLoginToken() {
        return loginToken;
    }

    /**
     * Sets the login token for the user's Xbox profile.
     *
     * @param loginToken The login token for the user's Xbox profile.
     */
    public void setLoginToken(String loginToken) {
        this.loginToken = loginToken;
    }

    // Equals and hashcode

    /**
     * Compares this XboxProfile object to another object.
     *
     * @param o The object to compare this XboxProfile object against.
     * @return True if the objects are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        XboxProfile that = (XboxProfile) o;
        return username.equals(that.username) &&
                loginToken.equals(that.loginToken) &&
                games.equals(that.games);
    }

    /**
     * Returns the hashcode of this XboxProfile object.
     *
     * @return The hashcode of this XboxProfile object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(username, loginToken, games);
    }

    // toString
    /**
     * Returns a string representation of this XboxProfile object.
     *
     * @return A string representation of this XboxProfile object.
     */
    @Override
    public String toString() {
        return "XboxProfile{" +
                "username='" + username + '\'' +
                ", loginToken='" + loginToken + '\'' +
                ", games=" + games +
                '}';
    }
}