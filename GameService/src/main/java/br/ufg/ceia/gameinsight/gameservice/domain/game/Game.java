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
import br.ufg.ceia.gameinsight.gameservice.domain.platform.Platform;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "game")
@Getter
@Setter
public class Game implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String title;

    private String cover;

    @OneToMany
    private List<ReleaseDate> releaseDates;

    @ManyToMany
    private List<AgeRating> ageRatings;

    private String storyLine;

    @ManyToMany
    private List<Genre> genres;

    @ManyToMany
    private List<GameTheme> themes;

    @ManyToMany
    private List<Franchise> franchises;

    @ManyToMany
    private List<GameMode> gameModes;

    @ManyToMany
    private List<PlayerPerspective> playerPerspectives;

    @ManyToMany
    private List<Localization> localizations;

    private float rating;

    private int ratingCount;

    @ManyToMany(mappedBy = "games")
    private List<CompanyGame> involvedCompanies;

    @ManyToMany
    private List<Platform> platforms;

    @OneToMany(mappedBy = "game")
    private List<Requirement> requirements;

    public Game() {
    }

    public Game(long id, String title, String cover, List<ReleaseDate> releaseDates, List<AgeRating> ageRatings,
                String storyLine, List<Genre> genres, List<GameTheme> themes, List<Franchise> franchises,
                List<GameMode> gameModes, List<PlayerPerspective> playerPerspectives, List<Localization> localizations,
                float rating, int ratingCount, List<CompanyGame> involvedCompanies, List<Platform> platforms,
                List<Requirement> requirements) {
        this.id = id;
        this.title = title;
        this.cover = cover;
        this.releaseDates = releaseDates;
        this.ageRatings = ageRatings;
        this.storyLine = storyLine;
        this.genres = genres;
        this.themes = themes;
        this.franchises = franchises;
        this.gameModes = gameModes;
        this.playerPerspectives = playerPerspectives;
        this.localizations = localizations;
        this.rating = rating;
        this.ratingCount = ratingCount;
        this.involvedCompanies = involvedCompanies;
        this.platforms = platforms;
        this.requirements = requirements;
    }

    @Override
    public String toString() {
        return "Game{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", cover='" + cover + '\'' +
                ", releaseDates=" + releaseDates +
                ", ageRatings=" + ageRatings +
                ", storyLine='" + storyLine + '\'' +
                ", genres=" + genres +
                ", themes=" + themes +
                ", franchises=" + franchises +
                ", gameModes=" + gameModes +
                ", playerPerspectives=" + playerPerspectives +
                ", localizations=" + localizations +
                ", rating=" + rating +
                ", ratingCount=" + ratingCount +
                ", involvedCompanies=" + involvedCompanies +
                ", platforms=" + platforms +
                ", requirements=" + requirements +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Game game)) return false;

        if (id != game.id) return false;
        if (Float.compare(game.rating, rating) != 0) return false;
        if (ratingCount != game.ratingCount) return false;
        if (!Objects.equals(title, game.title)) return false;
        if (!Objects.equals(cover, game.cover)) return false;
        if (!Objects.equals(releaseDates, game.releaseDates)) return false;
        if (!Objects.equals(ageRatings, game.ageRatings)) return false;
        if (!Objects.equals(storyLine, game.storyLine)) return false;
        if (!Objects.equals(genres, game.genres)) return false;
        if (!Objects.equals(themes, game.themes)) return false;
        if (!Objects.equals(franchises, game.franchises)) return false;
        if (!Objects.equals(gameModes, game.gameModes)) return false;
        if (!Objects.equals(playerPerspectives, game.playerPerspectives)) return false;
        if (!Objects.equals(localizations, game.localizations)) return false;
        if (!Objects.equals(involvedCompanies, game.involvedCompanies)) return false;
        if (!Objects.equals(platforms, game.platforms)) return false;
        return Objects.equals(requirements, game.requirements);
    }
}