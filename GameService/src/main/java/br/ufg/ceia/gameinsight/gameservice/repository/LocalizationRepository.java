package br.ufg.ceia.gameinsight.gameservice.repository;

import br.ufg.ceia.gameinsight.gameservice.domain.game.localization.Localization;
import br.ufg.ceia.gameinsight.gameservice.domain.platform.Platform;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * This interface represents the repository for the Localization entity.
 * <p>
 * This interface provides methods to access the Localization data in the database.
 */
@Repository
public interface LocalizationRepository extends JpaRepository<Localization, Long> {

    /**
     * Find a Localization by its name.
     * @param name
     * @return the Localization
     */
    Localization findByName(String name);

}
