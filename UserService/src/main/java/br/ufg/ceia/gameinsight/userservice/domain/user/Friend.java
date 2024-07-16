package br.ufg.ceia.gameinsight.userservice.domain.user;

import br.ufg.ceia.gameinsight.userservice.domain.games.Game;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

/**
 * This class represents the Friend entity.
 * <p>
 * This class holds details about a friend of the user.
 */
public class Friend {

    /**
     * The unique identifier for the friend.
     */
    @Field
    private String friendId;

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
}