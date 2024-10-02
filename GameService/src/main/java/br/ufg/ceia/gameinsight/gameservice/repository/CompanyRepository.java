package br.ufg.ceia.gameinsight.gameservice.repository;

import br.ufg.ceia.gameinsight.gameservice.domain.company.Company;
import br.ufg.ceia.gameinsight.gameservice.domain.company.company_game.CompanyGame;
import br.ufg.ceia.gameinsight.gameservice.domain.game.age_rating.AgeRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * This interface represents the repository for the Company entity.
 * <p>
 * This interface provides methods to access the Company data in the database.
 */
@Repository
public interface CompanyRepository extends JpaRepository<Company, Integer> {

    /**
     * Find a Company by its name.
     * @param name
     * @return the Company
     */
    Company findByName(String name);

    /**
     * Find by igdb id.
     * @param igdbId
     * @return the Company
     */
    Company findByIgdbId(Integer igdbId);

    List<Company> findAllByIgdbIdIn(Collection<Integer> igdbId);
}
