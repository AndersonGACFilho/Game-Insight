package br.ufg.ceia.gameinsight.gameservice.repository;

import br.ufg.ceia.gameinsight.gameservice.domain.game.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {

    // Buscar jogos por título parcial
    List<Game> findByTitleContaining(String title);

    // Buscar jogos por nome da plataforma
    List<Game> findByPlatforms_Name(String platformName);

    // Buscar jogos por nome da empresa envolvida
    List<Game> findByInvolvedCompanies_Company_Name(String companyName);
}