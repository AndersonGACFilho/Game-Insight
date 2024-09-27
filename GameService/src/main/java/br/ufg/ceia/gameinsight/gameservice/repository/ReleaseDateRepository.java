package br.ufg.ceia.gameinsight.gameservice.repository;

import br.ufg.ceia.gameinsight.gameservice.domain.game.Game;
import br.ufg.ceia.gameinsight.gameservice.domain.game.release_date.ReleaseDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReleaseDateRepository extends JpaRepository<ReleaseDate, Long> {
    ReleaseDate findByGameId(Long gameId);

    ReleaseDate findByGameIdAndPlatformId(Long gameId, Long platformId);

    List<ReleaseDate> findAllByGame(Game game);
}
