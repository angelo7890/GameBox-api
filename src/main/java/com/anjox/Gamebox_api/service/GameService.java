package com.anjox.Gamebox_api.service;
import com.anjox.Gamebox_api.dto.RequestCreateGameDto;
import com.anjox.Gamebox_api.dto.RequestUpdatePictureDto;
import com.anjox.Gamebox_api.dto.ResponseGameDto;
import com.anjox.Gamebox_api.dto.ResponsePaginationGameDto;
import com.anjox.Gamebox_api.entity.GameEntity;
import com.anjox.Gamebox_api.entity.UserEntity;
import com.anjox.Gamebox_api.exeption.MessageErrorExeption;
import com.anjox.Gamebox_api.repository.GameRepository;
import com.anjox.Gamebox_api.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class GameService {

    private final GameRepository gameRepository;
    private final UserRepository userRepository;
    private final CloudinaryService cloudinaryService;

    public GameService(GameRepository gameRepository, UserRepository userRepository, CloudinaryService cloudinaryService) {
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
        this.cloudinaryService = cloudinaryService;
    }

    public void createGame(RequestCreateGameDto dto){
        if(gameRepository.existsByUserIdAndTitle(dto.userId(), dto.title())){
            throw new MessageErrorExeption("ja existe um jogo com esse nome na sua lista de jogos", HttpStatus.CONFLICT);
        }
        GameEntity game = new GameEntity(
                dto.userId(),
                dto.title(),
                dto.description(),
                dto.genre(),
                dto.price(),
                dto.imageUrl(),
                dto.imageId()
        );
        gameRepository.save(game);
    }

    public ResponseGameDto getGameById(Long id){
        GameEntity game =  gameRepository.findByid(id);
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
        throw new MessageErrorExeption("game nao encontrado", HttpStatus.BAD_REQUEST);
    }

    public ResponsePaginationGameDto getAllGames(int size, int page){
         Pageable pageable = PageRequest.of(page, size);
         Page<GameEntity> games = gameRepository.findAll(pageable);
        return getResponsePaginationGameDto(games);
    }

    public ResponsePaginationGameDto filterGamesByGenre(String genre , String username , int size, int page){
        UserEntity user = userRepository.findByUsername(username);
        if(user == null){
            throw new MessageErrorExeption("impossivel filtrar, pois o usuario do jogo esta vazio ou nao foi encontrado", HttpStatus.BAD_REQUEST);
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<GameEntity> games = gameRepository.findByGenreAndUserId(genre, user.getId(), pageable);
        return getResponsePaginationGameDto(games);

    }

    public ResponsePaginationGameDto getGamesByUserId(Long userId, int page, int size){
        Pageable pageable  = PageRequest.of(page, size);
        Page<GameEntity> gamesByUserId=  gameRepository.findByUserId(userId, pageable);
        return getResponsePaginationGameDto(gamesByUserId);
    }

    public void updatePictureForGameById(RequestUpdatePictureDto dto){
        GameEntity game =  gameRepository.findByid(dto.gameId());
        if(game!=null){
            if(game.getImageUrl()!=null){
                cloudinaryService.deletePictureByIdFromCloud(game.getImageId());
            }
            game.setImageUrl(dto.url());
            game.setImageId(dto.pictureId());
            gameRepository.save(game);
        }
    }

    public void deleteGameById(Long id){
        GameEntity game =  gameRepository.findByid(id);
        if(game!=null){
            if(game.getImageId()!=null){
                cloudinaryService.deletePictureByIdFromCloud(game.getImageId());
            }
            gameRepository.delete(game);
        }
        throw new MessageErrorExeption("game nao encontrado", HttpStatus.BAD_REQUEST);
    }

    public void deleteAllGamesByUserId(Long userId){
        List<GameEntity> games = gameRepository.findByUserId(userId);
        if (games != null && !games.isEmpty()) {
            for (GameEntity game : games) {
                if (game.getImageId() != null) {
                    cloudinaryService.deletePictureByIdFromCloud(game.getImageId());
                    gameRepository.deleteById(game.getId());
                }
            }
        }
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
