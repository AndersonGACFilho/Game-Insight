package br.ufg.ceia.gameinsight.gameservice.repository;

import br.ufg.ceia.gameinsight.gameservice.domain.game.region.Region;
import br.ufg.ceia.gameinsight.gameservice.domain.platform.Platform;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * This interface represents the repository for the Platform entity.
 * <p>
 * This interface provides methods to access the platform data in the database.
 */
@Repository
public interface RegionRepository extends JpaRepository<Region, Integer> {

    /**
     * Find a Region by its name.
     * @param name
     * @return the Region
     */
    Region findByName(String name);

    /**
     * Find by id on IGDB.
     * @param igdbId
     * @return the Region
     */
    Region findByIgdbId(Integer igdbId);

}
