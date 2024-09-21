package br.ufg.ceia.gameinsight.gameservice.service;

import br.ufg.ceia.gameinsight.gameservice.entities.Game;
import br.ufg.ceia.gameinsight.gameservice.exception.ResourceNotFoundException;
import br.ufg.ceia.gameinsight.gameservice.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

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

    public Game getGameById(Long id) {
        return gameRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Game not found with id: " + id));
    }

    public List<Game> getGamesByTitle(String title) {
        return gameRepository.findByTitleContaining(title);
    }

    public List<Game> getGamesByPlatform(String platform) {
        return gameRepository.findByPlatform(platform);
    }

    public Game createGame(Game game) {
        return gameRepository.save(game);
    }

    public Game updateGame(Long id, Game gameDetails) {
        Game game = getGameById(id);
        game.setTitle(gameDetails.getTitle());
        game.setPlatform(gameDetails.getPlatform());
        game.setDescription(gameDetails.getDescription());
        game.setPublisher(gameDetails.getPublisher());
        game.setSystemRequirements(gameDetails.getSystemRequirements());
        return gameRepository.save(game);
    }

    public void deleteGame(Long id) {
        Game game = getGameById(id);
        gameRepository.delete(game);
    }
}
