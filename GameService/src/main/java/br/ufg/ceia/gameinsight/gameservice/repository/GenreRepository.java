package br.ufg.ceia.gameinsight.gameservice.repository;

import br.ufg.ceia.gameinsight.gameservice.domain.game.game_mode.GameMode;
import br.ufg.ceia.gameinsight.gameservice.domain.game.genre.Genre;
import br.ufg.ceia.gameinsight.gameservice.domain.game.region.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * This interface represents the repository for the Genre entity.
 * <p>
 * This interface provides methods to access the genre data in the database.
 */
@Repository
public interface GenreRepository extends JpaRepository<Genre, Integer> {

    /**
     * Find a genre by its name.
     * @param name
     * @return the genre
     */
    Genre findByName(String name);

    /**
     * Find a genre by the igdb identifier.
     * @param igdbId
     * @return the genre
     */
    Genre findByIgdbId(Integer igdbId);

    List<Genre> findAllByIgdbIdIn(Collection<Integer> igdbId);
}
