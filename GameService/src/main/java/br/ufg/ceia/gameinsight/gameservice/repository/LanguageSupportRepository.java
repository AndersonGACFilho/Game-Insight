package br.ufg.ceia.gameinsight.gameservice.repository;

import br.ufg.ceia.gameinsight.gameservice.domain.game.languages.Language;
import br.ufg.ceia.gameinsight.gameservice.domain.game.languages.LanguageSupport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * This interface represents the repository for the Language Support entity.
 * <p>
 * This interface provides methods to access the Language Support data in the database.
 */
@Repository
public interface LanguageSupportRepository extends JpaRepository<LanguageSupport, Integer> {

    /**
     * Find a Language Support by igdbId.
     * @param igdbId
     * @return the LanguageSupport
     */
    LanguageSupport findByIgdbId(Integer igdbId);

    List<LanguageSupport> findAllByIgdbIdIn(Collection<Integer> igdbId);
}
