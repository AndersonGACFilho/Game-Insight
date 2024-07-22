package br.ufg.ceia.gameinsight.userservice.domain.user;

import br.ufg.ceia.gameinsight.userservice.domain.games.Game;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.List;

/**
 * This class represents the Friend entity.
 * <p>
 * This class holds details about a friend of the user.
 */
public class Friend {
    /**
     * The name of the friend.
     */
    @Field
    private String name;

    /**
     * The list of games that the friend is playing.
     */
    @Field
    private List<Game> gamesPlaying;

    // Getters and setters

    /**
     * Gets the name of the friend.
     *
     * @return The name of the friend.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the friend.
     *
     * @param name The name of the friend.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the list of games that the friend is playing.
     *
     * @return The list of games that the friend is playing.
     */
    public List<Game> getGamesPlaying() {
        return gamesPlaying;
    }

    /**
     * Sets the list of games that the friend is playing.
     *
     * @param gamesPlaying The list of games that the friend is playing.
     */
    public void setGamesPlaying(List<Game> gamesPlaying) {
        this.gamesPlaying = gamesPlaying;
    }

    // equals and hashCode

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

        Friend friend = (Friend) o;
        return name.equals(friend.name);
    }

    /**
     * Returns a hash code value for the object.
     *
     * @return A hash code value for this object.
     */
    @Override
    public int hashCode() {
        return name.hashCode();
    }
}