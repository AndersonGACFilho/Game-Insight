package br.ufg.ceia.gameinsight.userservice.domain.profiles;

import br.ufg.ceia.gameinsight.userservice.domain.games.Game;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;
import java.util.Objects;

/**
 * This class represents the SteamProfile entity.
 * <p>
 * This class holds the Steam profile details of the user.
 */
public class SteamProfile {

    /**
     * The username of the user on Steam.
     */
    @Field
    private String username;

    /**
     * The login token for the user's Steam profile.
     */
    @Field
    private String loginToken;

    /**
     * The list of games associated with the user's Steam profile.
     */
    @Field
    private List<Game> games;

    // Getters and setters

    /**
     * Gets the username of the user on Steam.
     *
     * @return The username of the user on Steam.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username of the user on Steam.
     *
     * @param username The username of the user on Steam.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the login token for the user's Steam profile.
     *
     * @return The login token for the user's Steam profile.
     */
    public String getLoginToken() {
        return loginToken;
    }

    /**
     * Sets the login token for the user's Steam profile.
     *
     * @param loginToken The login token for the user's Steam profile.
     */
    public void setLoginToken(String loginToken) {
        this.loginToken = loginToken;
    }

    /**
     * Gets the list of games associated with the user's Steam profile.
     *
     * @return The list of games associated with the user's Steam profile.
     */
    public List<Game> getGames() {
        return games;
    }

    /**
     * Sets the list of games associated with the user's Steam profile.
     *
     * @param games The list of games associated with the user's Steam profile.
     */
    public void setGames(List<Game> games) {
        this.games = games;
    }

    // Add and Remove Game
    /**
     * Adds a game to the list of games associated with the user's Steam profile.
     *
     * @param game The game to be added.
     */
    public void addGame(Game game) {
        this.games.add(game);
    }

    /**
     * Removes a game from the list of games associated with the user's Steam profile.
     *
     * @param game The game to be removed.
     */
    public void removeGame(Game game) {
        this.games.remove(game);
    }

    // Equals and HashCode

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param o The reference object with which to compare.
     * @return true if this object is the same as the obj argument; false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SteamProfile that = (SteamProfile) o;
        return  Objects.equals(username, that.username) &&
                Objects.equals(loginToken, that.loginToken) &&
                Objects.equals(games, that.games);
    }

    /**
     * Returns the hash code value for the object on which this method is invoked.
     *
     * @return The hash code value for the object on which this method is invoked.
     */
    @Override
    public int hashCode() {
        return Objects.hash(username, loginToken, games);
    }

    // toString

    /**
     * Returns a string representation of the SteamProfile object.
     *
     * @return A string representation of the SteamProfile object.
     */
    @Override
    public String toString() {
        return "SteamProfile{" +
                "username='" + username + '\'' +
                ", loginToken='" + loginToken + '\'' +
                ", games=" + games +
                '}';
    }
}