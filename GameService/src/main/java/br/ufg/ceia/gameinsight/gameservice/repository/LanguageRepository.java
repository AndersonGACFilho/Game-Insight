package br.ufg.ceia.gameinsight.gameservice.repository;

import br.ufg.ceia.gameinsight.gameservice.domain.game.game_mode.GameMode;
import br.ufg.ceia.gameinsight.gameservice.domain.game.languages.Language;
import br.ufg.ceia.gameinsight.gameservice.domain.game.languages.LanguageSupport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * This interface represents the repository for the Language entity.
 * <p>
 * This interface provides methods to access the Language data in the database.
 */
@Repository
public interface LanguageRepository extends JpaRepository<Language, Integer> {

    /**
     * Find a Language by igdbId.
     * @param igdbId
     * @return the Language
     */
    Language findByIgdbId(Integer igdbId);


    List<Language> findAllByIgdbIdIn(Collection<Integer> igdbId);
}
