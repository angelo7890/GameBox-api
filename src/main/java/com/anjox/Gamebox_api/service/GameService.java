package com.anjox.Gamebox_api.service;
import com.anjox.Gamebox_api.dto.RequestCreateGameDto;
import com.anjox.Gamebox_api.entity.GameEntity;
import com.anjox.Gamebox_api.repository.GameRepository;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
public class GameService {

    private final GameRepository gameRepository;

    public GameService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public GameEntity creategame(RequestCreateGameDto dto){
        if(gameRepository.existsByUserIdAndTitle(dto.userId(), dto.title())){
            throw new RuntimeException("ja existe um jogo com esse nome na sua lista de jogos");
        }
        GameEntity game = new GameEntity(
                dto.userId(),
                dto.title(),
                dto.description(),
                dto.genre(),
                dto.price(),
                dto.imageUrl()
        );
        return gameRepository.save(game);
    }

    public GameEntity getGameById(Long id){
        return gameRepository.findById(id).orElse(null);
    }

    public List<GameEntity> getAllGames(){
        return gameRepository.findAll();
    }

    public List<GameEntity> getGamesByUserId(Long userId){
        return gameRepository.findByUserId(userId);
    }

    public void deleteGameById(Long id){
        gameRepository.deleteById(id);
    }

    public void deleteAllGamesByUserId(Long userId){
        gameRepository.deleteAllByUserId(userId);
    }
}
