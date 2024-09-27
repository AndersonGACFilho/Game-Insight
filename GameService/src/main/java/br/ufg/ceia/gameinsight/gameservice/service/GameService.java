package br.ufg.ceia.gameinsight.gameservice.service;

import br.ufg.ceia.gameinsight.gameservice.domain.company.Company;
import br.ufg.ceia.gameinsight.gameservice.domain.company.company_game.CompanyGame;
import br.ufg.ceia.gameinsight.gameservice.domain.game.Game;
import br.ufg.ceia.gameinsight.gameservice.domain.platform.Platform;
import br.ufg.ceia.gameinsight.gameservice.exception.ResourceNotFoundException;
import br.ufg.ceia.gameinsight.gameservice.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameService {

    private final GameRepository gameRepository;

    @Autowired
    public GameService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public List<Game> getAllGames() {
        return gameRepository.findAll();
    }

    public Game getGameById(Integer id) {
        return gameRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Game not found with id: " + id));
    }

    public List<Game> getGamesByTitle(String title) {
        return gameRepository.findByTitleContaining(title);
    }

    public List<Game> getGamesByPlatform(String platformName) {
        // Retornar jogos que estejam associados a uma determinada plataforma
        return gameRepository.findByPlatforms_Name(platformName);
    }

    public List<Game> getGamesByCompany(String companyName) {
        // Retornar jogos que estejam associados a uma determinada empresa
        return gameRepository.findByInvolvedCompanies_Company_Name(companyName);
    }

    public Game createGame(Game game) {
        // Adicionar lógica para associar plataformas e empresas ao jogo
        associateGameWithPlatformsAndCompanies(game);
        return gameRepository.save(game);
    }

    public Game updateGame(Integer id, Game gameDetails) {
        Game game = getGameById(id);

        // Atualizar os detalhes do jogo
        game.setTitle(gameDetails.getTitle());
        game.setCover(gameDetails.getCover());
        game.setReleaseDates(gameDetails.getReleaseDates());
        game.setAgeRatings(gameDetails.getAgeRatings());
        game.setSummary(gameDetails.getSummary());
        game.setGenres(gameDetails.getGenres());
        game.setThemes(gameDetails.getThemes());
        game.setFranchises(gameDetails.getFranchises());
        game.setGameModes(gameDetails.getGameModes());
        game.setPlayerPerspectives(gameDetails.getPlayerPerspectives());
        game.setLocalizations(gameDetails.getLocalizations());
        game.setRating(gameDetails.getRating());
        game.setRatingCount(gameDetails.getRatingCount());

        // Atualizar plataformas e empresas
        associateGameWithPlatformsAndCompanies(gameDetails);
        game.setPlatforms(gameDetails.getPlatforms());
        game.setInvolvedCompanies(gameDetails.getInvolvedCompanies());

        return gameRepository.save(game);
    }

    public void deleteGame(Integer id) {
        Game game = getGameById(id);
        gameRepository.delete(game);
    }

    private void associateGameWithPlatformsAndCompanies(Game game) {
        // Lógica para associar plataformas e empresas ao jogo
        List<Platform> platforms = game.getPlatforms();
        List<CompanyGame> companyGames = game.getInvolvedCompanies();

        // Adicionar lógica de validação se necessário
    }
}