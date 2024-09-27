package br.ufg.ceia.gameinsight.gameservice.repository;

import br.ufg.ceia.gameinsight.gameservice.domain.game.franchise.Franchise;
import br.ufg.ceia.gameinsight.gameservice.domain.platform.Platform;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * This interface represents the repository for the Franchise entity.
 * <p>
 * This interface provides methods to access the Franchise data in the database.
 */
@Repository
public interface FranchiseRepository extends JpaRepository<Franchise, Long> {

    /**
     * Find a Franchise by its name.
     * @param name
     * @return the Franchise
     */
    Franchise findByName(String name);

}
