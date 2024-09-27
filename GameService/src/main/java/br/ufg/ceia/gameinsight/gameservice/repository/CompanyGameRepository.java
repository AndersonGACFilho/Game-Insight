package br.ufg.ceia.gameinsight.gameservice.repository;

import br.ufg.ceia.gameinsight.gameservice.domain.company.company_game.CompanyGame;
import br.ufg.ceia.gameinsight.gameservice.domain.game.genre.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * This interface represents the repository for the CompanyGame entity.
 * <p>
 * This interface provides methods to access the genre data in the database.
 */
@Repository
public interface CompanyGameRepository extends JpaRepository<CompanyGame, Integer> {

    /**
     * Find by igdb id.
     * @param igdbId
     * @return the CompanyGame
     */
    CompanyGame findByIgdbId(Integer igdbId);

}
