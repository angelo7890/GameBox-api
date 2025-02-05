package com.anjox.Gamebox_api.service;
import com.anjox.Gamebox_api.dto.RequestCreateGameDto;
import com.anjox.Gamebox_api.dto.ResponseGameDto;
import com.anjox.Gamebox_api.dto.ResponsePaginationGameDto;
import com.anjox.Gamebox_api.entity.GameEntity;
import com.anjox.Gamebox_api.repository.GameRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;


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

    public ResponseGameDto getGameById(Long id){
        GameEntity game =  gameRepository.findById(id).orElse(null);
        if(game!=null){
            return new ResponseGameDto(
                    game.getId(),
                    game.getTitle(),
                    game.getDescription(),
                    game.getGenre(),
                    game.getPrice(),
                    game.getImageUrl()
            ) ;
        }
        throw new RuntimeException("game nao encontrado");
    }

    public ResponsePaginationGameDto getAllGames(int size, int page){
         Pageable pageable = PageRequest.of(page, size);
         Page<GameEntity> games = gameRepository.findAll(pageable);
        return getResponsePaginationGameDto(games);
    }

    public ResponsePaginationGameDto getGamesByUserId(Long userId, int page, int size){
        Pageable pageable  = PageRequest.of(page, size);
        Page<GameEntity> gamesByUserId=  gameRepository.findByUserId(userId, pageable);
        return getResponsePaginationGameDto(gamesByUserId);
    }

    public void deleteGameById(Long id){
        if(gameRepository.existsById(id)){
            gameRepository.deleteById(id);
        }
        throw new RuntimeException("game nao encontrado");
    }

    public void deleteAllGamesByUserId(Long userId){
        gameRepository.deleteAllByUserId(userId);
    }

    private ResponsePaginationGameDto getResponsePaginationGameDto(Page<GameEntity> gamesByUserId) {
        List<ResponseGameDto> games = gamesByUserId.getContent().stream().map(
                g -> new ResponseGameDto(
                        g.getId(),
                        g.getTitle(),
                        g.getDescription(),
                        g.getGenre(),
                        g.getPrice(),
                        g.getImageUrl()
                )
        ).collect(Collectors.toList());
        return new ResponsePaginationGameDto(
                games,
                gamesByUserId.getTotalPages(),
                gamesByUserId.getTotalElements(),
                gamesByUserId.getSize(),
                gamesByUserId.getNumber()
        );
    }
}
