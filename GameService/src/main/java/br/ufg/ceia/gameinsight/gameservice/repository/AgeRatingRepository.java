package br.ufg.ceia.gameinsight.gameservice.repository;

import br.ufg.ceia.gameinsight.gameservice.domain.game.Game;
import br.ufg.ceia.gameinsight.gameservice.domain.game.age_rating.AgeRating;
import br.ufg.ceia.gameinsight.gameservice.domain.game.release_date.ReleaseDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface AgeRatingRepository extends JpaRepository<AgeRating, Integer> {

    AgeRating findByIgdbId(Integer igdbId);

    List<AgeRating> findAllByIgdbIdIn(Collection<Integer> igdbId);
}
