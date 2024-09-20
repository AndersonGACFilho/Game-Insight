package br.ufg.ceia.gameinsight.gameservice.domain.game;

import br.ufg.ceia.gameinsight.gameservice.domain.company.company_game.CompanyGame;
import br.ufg.ceia.gameinsight.gameservice.domain.game.age_rating.AgeRating;
import br.ufg.ceia.gameinsight.gameservice.domain.game.franchise.Franchise;
import br.ufg.ceia.gameinsight.gameservice.domain.game.game_mode.GameMode;
import br.ufg.ceia.gameinsight.gameservice.domain.game.game_theme.GameTheme;
import br.ufg.ceia.gameinsight.gameservice.domain.game.genre.Genre;
import br.ufg.ceia.gameinsight.gameservice.domain.game.localization.Localization;
import br.ufg.ceia.gameinsight.gameservice.domain.game.player_perspective.PlayerPerspective;
import br.ufg.ceia.gameinsight.gameservice.domain.game.release_date.ReleaseDate;
import br.ufg.ceia.gameinsight.gameservice.domain.game.requirement.Requirement;
import jakarta.persistence.*;
import org.graalvm.nativeimage.Platform;
import org.springframework.stereotype.Repository;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * This class represents the Game entity.
 * <p>
 * This class holds details about a game associated with a user's profile.
 */
@Repository
public class Game implements Serializable {
    /**
     * The serial version UID.
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * id of the game.
     */
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    /**
     * The title of the game.
     */
    private String title;

    /**
     * The cover image URL of the game.
     */
    private String cover;

    /**
     * The release dates of the game.
     */
    @OneToMany
    List<ReleaseDate> releaseDates;

    /**
     * The list of age ratings associated with the game.
     */
    @ManyToMany(mappedBy = "games")
    List<AgeRating> ageRatings;

    /**
     * The story of the game.
     */
    private String storyLine;

    /**
     * The list of genres associated with the game.
     */
    @ManyToMany(mappedBy = "games")
    List<Genre> genres;

    /**
     * The list of themes associated with the game.
     */
    @ManyToMany(mappedBy = "games")
    List<GameTheme> themes;

    /**
     * The list of franchises associated with the game.
     */
    @ManyToMany(mappedBy = "games")
    List<Franchise> franchises;

    /**
     * The list of Game Modes associated with the game.
     */
    @ManyToMany(mappedBy = "games")
    List<GameMode> gameModes;

    /**
     * The list of player perspectives associated with the game.
     */
    @ManyToMany(mappedBy = "games")
    List<PlayerPerspective> playerPerspectives;

    /**
     * The list of localizations associated with the game.
     */
    @ManyToMany(mappedBy = "games")
    List<Localization> localizations;

    /**
     * The list of platforms associated with the game.
     */
    private float rating;

    /**
     * The number of ratings for the game.
     */
    private int ratingCount;

    /**
     * The list of involved companies associated with the game.
     */
    @OneToMany(mappedBy = "games")
    List<CompanyGame> involvedCompanies;

    /**
     * The list of platforms associated with the game.
     */
    @ManyToMany(mappedBy = "games")
    List<Platform> platforms;

    /**
     * The list of requirements associated with the game.
     */
    @OneToMany(mappedBy = "games")
    List<Requirement> requirements;
}