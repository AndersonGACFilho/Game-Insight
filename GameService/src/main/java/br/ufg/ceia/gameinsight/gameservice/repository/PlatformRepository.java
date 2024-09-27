package br.ufg.ceia.gameinsight.gameservice.repository;

import br.ufg.ceia.gameinsight.gameservice.domain.game.Game;
import br.ufg.ceia.gameinsight.gameservice.domain.platform.Platform;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * This interface represents the repository for the Platform entity.
 * <p>
 * This interface provides methods to access the platform data in the database.
 */
@Repository
public interface PlatformRepository extends JpaRepository<Platform, Long> {

    /**
     * Find a platform by its name.
     * @param name
     * @return the platform
     */
    Platform findByName(String name);

}
