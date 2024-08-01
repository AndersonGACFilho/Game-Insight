package br.ufg.ceia.gameinsight.userservice.domain.marketplace;

import br.ufg.ceia.gameinsight.userservice.domain.games.Game;
import org.graalvm.nativeimage.Platform;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;
import java.util.Objects;

/**
 * This class represents the MarketplaceProfile entity.
 * <p>
 * This class holds the Marketplace profile details of the user.
 */
public class MarketplaceProfile {

    /**
     * The username of the user on Marketplace.
     */
    @Field
    private String username;

    /**
     * The login token for the user's Marketplace profile.
     */
    @Field
    private String loginToken;

    /**
     * The type of the Marketplace can be either Steam, PlayStation, or Xbox.
     */
    @Field
    private MarketplaceType marketplaceType;

    /**
     * The list of games associated with the user's Marketplace profile.
     */
    @Field
    private List<Game> games;

    // Getters and setters

    /**
     * Gets the username of the user on Marketplace.
     *
     * @return The username of the user on Marketplace.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username of the user on Marketplace.
     *
     * @param username The username of the user on Marketplace.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the login token for the user's Marketplace profile.
     *
     * @return The login token for the user's Marketplace profile.
     */
    public String getLoginToken() {
        return loginToken;
    }

    /**
     * Sets the login token for the user's Marketplace profile.
     *
     * @param loginToken The login token for the user's Marketplace profile.
     */
    public void setLoginToken(String loginToken) {
        this.loginToken = loginToken;
    }

    /**
     * Gets the list of games associated with the user's Marketplace profile.
     *
     * @return The list of games associated with the user's Marketplace profile.
     */
    public List<Game> getGames() {
        return games;
    }

    /**
     * Sets the list of games associated with the user's Marketplace profile.
     *
     * @param games The list of games associated with the user's Marketplace profile.
     */
    public void setGames(List<Game> games) {
        this.games = games;
    }

    // Add and Remove Game
    /**
     * Adds a game to the list of games associated with the user's Marketplace profile.
     *
     * @param game The game to add to the list of games associated with the user's Marketplace profile.
     */
    public void addGame(Game game) {
        this.games.add(game);
    }

    /**
     * Removes a game from the list of games associated with the user's Marketplace profile.
     *
     * @param game The game to remove from the list of games associated with the user's Marketplace profile.
     */
    public void removeGame(Game game) {
        this.games.remove(game);
    }

    // Equals and HashCode

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param o The reference object with which to compare.
     * @return {@code true} if this object is the same as the obj argument; {@code false} otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MarketplaceProfile that = (MarketplaceProfile) o;
        return Objects.equals(username, that.username) &&
                Objects.equals(loginToken, that.loginToken) &&
                Objects.equals(games, that.games);
    }

    /**
     * Returns the hash code value for this Marketplace profile.
     *
     * @return The hash code value for this Marketplace profile.
     */
    @Override
    public int hashCode() {
        return Objects.hash(username, loginToken, games);
    }

    // toString
    /**
     * Returns a string representation of this Marketplace profile.
     *
     * @return A string representation of this Marketplace profile.
     */
    @Override
    public String toString() {
        return "MarketplaceProfile{" +
                "username='" + username + '\'' +
                ", loginToken='" + loginToken + '\'' +
                ", games=" + games +
                '}';
    }
}
