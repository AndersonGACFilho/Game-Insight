package br.ufg.ceia.gameinsight.gameservice.repository;

import br.ufg.ceia.gameinsight.gameservice.domain.game.Game;
import br.ufg.ceia.gameinsight.gameservice.domain.game.region.Region;
import br.ufg.ceia.gameinsight.gameservice.domain.game.release_date.ReleaseDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ReleaseDateRepository extends JpaRepository<ReleaseDate, Integer> {
    ReleaseDate findByGameId(Integer gameId);

    ReleaseDate findByGameIdAndPlatformId(Integer gameId, Integer platformId);

    List<ReleaseDate> findAllByGame(Game game);

    ReleaseDate findByIgdbId(Integer igdbId);

    List<ReleaseDate> findAllByIgdbIdIn(Collection<Integer> igdbId);

}
