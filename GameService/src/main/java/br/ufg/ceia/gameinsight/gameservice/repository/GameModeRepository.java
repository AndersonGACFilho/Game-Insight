package br.ufg.ceia.gameinsight.gameservice.repository;

import br.ufg.ceia.gameinsight.gameservice.domain.game.age_rating.AgeRating;
import br.ufg.ceia.gameinsight.gameservice.domain.game.game_mode.GameMode;
import br.ufg.ceia.gameinsight.gameservice.domain.platform.Platform;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * This interface represents the repository for the GameMode entity.
 * <p>
 * This interface provides methods to access the GameMode data in the database.
 */
@Repository
public interface GameModeRepository extends JpaRepository<GameMode, Integer> {

    /**
     * Find a GameMode by its name.
     * @param name
     * @return the GameMode
     */
    GameMode findByName(String name);

    /**
     * Find a GameMode by the igdb identifier.
     * @param igdbId
     * @return the GameMode
     */
    GameMode findByIgdbId(Integer igdbId);

    List<GameMode> findAllByIgdbIdIn(Collection<Integer> igdbId);
}
