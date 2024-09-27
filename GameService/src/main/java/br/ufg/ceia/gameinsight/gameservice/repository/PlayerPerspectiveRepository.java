package br.ufg.ceia.gameinsight.gameservice.repository;

import br.ufg.ceia.gameinsight.gameservice.domain.game.player_perspective.PlayerPerspective;
import br.ufg.ceia.gameinsight.gameservice.domain.platform.Platform;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * This interface represents the repository for the Platform entity.
 * <p>
 * This interface provides methods to access the platform data in the database.
 */
@Repository
public interface PlayerPerspectiveRepository extends JpaRepository<PlayerPerspective, Long> {

    /**
     * Find a PlayerPerspective by its name.
     * @param name
     * @return the platform
     */
    PlayerPerspective findByName(String name);

}
