package br.ufg.ceia.gameinsight.gameservice.repository;

import br.ufg.ceia.gameinsight.gameservice.domain.game.game_mode.GameMode;
import br.ufg.ceia.gameinsight.gameservice.domain.platform.Platform;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * This interface represents the repository for the GameMode entity.
 * <p>
 * This interface provides methods to access the GameMode data in the database.
 */
@Repository
public interface GameModeRepository extends JpaRepository<GameMode, Long> {

    /**
     * Find a GameMode by its name.
     * @param name
     * @return the GameMode
     */
    GameMode findByName(String name);

}
