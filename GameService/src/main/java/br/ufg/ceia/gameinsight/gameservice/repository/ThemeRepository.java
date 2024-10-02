package br.ufg.ceia.gameinsight.gameservice.repository;

import br.ufg.ceia.gameinsight.gameservice.domain.game.game_theme.GameTheme;
import br.ufg.ceia.gameinsight.gameservice.domain.platform.Platform;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.ui.context.Theme;

import java.util.Collection;
import java.util.List;

/**
 * This interface represents the repository for the Theme entity.
 * <p>
 * This interface provides methods to access the GameTheme data in the database.
 */
@Repository
public interface ThemeRepository extends JpaRepository<GameTheme, Integer> {

    /**
     * Find a GameTheme by its name.
     * @param name
     * @return the platform
     */
    GameTheme findByName(String name);

    /**
     * Find a GameTheme by the igdb identifier.
     * @param igdbId
     * @return the platform
     */
    GameTheme findByIgdbId(Integer igdbId);

    List<GameTheme> findAllByIgdbIdIn(Collection<Integer> igdbId);

}
