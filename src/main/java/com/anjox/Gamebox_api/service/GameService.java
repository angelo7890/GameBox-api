package com.anjox.Gamebox_api.service;
import com.anjox.Gamebox_api.dto.*;
import com.anjox.Gamebox_api.entity.GameEntity;
import com.anjox.Gamebox_api.entity.UserEntity;
import com.anjox.Gamebox_api.enums.UserEnum;
import com.anjox.Gamebox_api.exeption.MessageErrorExeption;
import com.anjox.Gamebox_api.repository.GameRepository;
import com.anjox.Gamebox_api.repository.UserRepository;
import org.apache.catalina.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.ErrorResponseException;

import java.math.BigDecimal;
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
        if (dto.price().compareTo(BigDecimal.ZERO) < 0) {
            throw new MessageErrorExeption("O preço não pode ser negativo", HttpStatus.BAD_REQUEST);
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

    public ResponseGameDto getGameById(Long id , String usernameFromToken){
        GameEntity game =  gameRepository.findByid(id);
        UserEntity user = userRepository.findByUsername(usernameFromToken);
        if(game!=null){
            if(game.getUserId().equals(user.getId()) || user.getType().equals(UserEnum.ADM)){
                return new ResponseGameDto(
                        game.getId(),
                        game.getTitle(),
                        game.getDescription(),
                        game.getGenre(),
                        game.getPrice(),
                        game.getImageUrl()
                ) ;
            }
            throw new MessageErrorExeption("Voce nao pode acessar as informaçoes de outra pessoa", HttpStatus.FORBIDDEN);
        }
        throw new MessageErrorExeption("game nao encontrado", HttpStatus.NOT_FOUND);
    }

    public ResponsePaginationGameDto getAllGames(int size, int page){
         Pageable pageable = PageRequest.of(page, size);
         Page<GameEntity> games = gameRepository.findAll(pageable);
        return getResponsePaginationGameDto(games);
    }

    public ResponsePaginationGameDto filterGamesByGenre(Long userId , String genre , String usernameFromToken , int size, int page){
        UserEntity user = userRepository.findByid(userId);
        UserEntity userFromUsername = userRepository.findByUsername(usernameFromToken);
        if(user == null){
            throw new MessageErrorExeption("impossivel filtrar, pois o usuario do jogo esta vazio ou nao foi encontrado", HttpStatus.BAD_REQUEST);
        }
        if(user.getId().equals(userFromUsername.getId()) || userFromUsername.getType().equals(UserEnum.ADM)){
            Pageable pageable = PageRequest.of(page, size);
            Page<GameEntity> games = gameRepository.findByGenreAndUserId(genre, user.getId(), pageable);
            return getResponsePaginationGameDto(games);
        }
        throw new MessageErrorExeption("Voce nao filtrar jogos de outra pessoa", HttpStatus.FORBIDDEN);

    }

    public ResponsePaginationGameDto getGamesByUserId(Long userId, String usernameFromToken, int page, int size){
        UserEntity userFromToken = userRepository.findByUsername(usernameFromToken);
        UserEntity user = userRepository.findByid(userId);
        if(user != null){
            if(userFromToken.getId().equals(user.getId()) || userFromToken.getType().equals(UserEnum.ADM)){
                Pageable pageable  = PageRequest.of(page, size);
                Page<GameEntity> gamesByUserId=  gameRepository.findByUserId(userId, pageable);
                return getResponsePaginationGameDto(gamesByUserId);
            }
            throw new MessageErrorExeption("Voce nao pode ver os jogos de outra pessoa", HttpStatus.FORBIDDEN);
        }
        throw new MessageErrorExeption("Id de usuario nao encontrado", HttpStatus.NOT_FOUND);

    }

    @Transactional
    public void updateGameById(Long gameId, RequestUpdateGameDto dto, String usernameFromToken) {
        UserEntity user = userRepository.findByUsername(usernameFromToken);
        GameEntity game = gameRepository.findByid(gameId);

        if (user == null || game == null) {
            throw new MessageErrorExeption("Game ou Usuário não encontrado", HttpStatus.BAD_REQUEST);
        }

        if (!game.getUserId().equals(user.getId()) && !user.getType().equals(UserEnum.ADM)) {
            throw new MessageErrorExeption("Você não pode alterar as informações de um jogo de outra pessoa", HttpStatus.UNAUTHORIZED);
        }

        if (dto.title() != null && !dto.title().isEmpty()) {
            if (gameRepository.existsByUserIdAndTitle(user.getId(), dto.title())) {
                throw new MessageErrorExeption("Você já possui um jogo com esse título", HttpStatus.CONFLICT);
            }
            game.setTitle(dto.title());
        }

        if (dto.description() != null && !dto.description().isEmpty()) {
            game.setDescription(dto.description());
        }

        if (dto.genre() != null && !dto.genre().isEmpty()) {
            game.setGenre(dto.genre());
        }

        if (dto.price() != null) {
            if (dto.price().compareTo(BigDecimal.ZERO) < 0) {
                throw new MessageErrorExeption("O preço não pode ser negativo", HttpStatus.BAD_REQUEST);
            }
            game.setPrice(dto.price());
        }

        gameRepository.save(game);
    }

    @Transactional
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

    @Transactional
    public void deleteGameById(Long id , String usernameFromToken){
        UserEntity user = userRepository.findByUsername(usernameFromToken);
        GameEntity game =  gameRepository.findByid(id);
        if(game!=null){
            if(game.getUserId().equals(user.getId()) || user.getType().equals(UserEnum.ADM)){
                if(game.getImageId()!=null){
                    cloudinaryService.deletePictureByIdFromCloud(game.getImageId());
                }
                gameRepository.delete(game);
            }
            throw new MessageErrorExeption("Voce nao pode apagar jogos de outra pessoa", HttpStatus.FORBIDDEN);
        }
        throw new MessageErrorExeption("game nao encontrado", HttpStatus.BAD_REQUEST);
    }

    @Transactional
    public void deleteAllGamesByUserId(Long userId , String usernameFromToken){
        UserEntity user = userRepository.findByUsername(usernameFromToken);
        if (user.getId().equals(userId) || user.getType().equals(UserEnum.ADM)) {
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
        throw new MessageErrorExeption("Voce no pode excluir os jogos de outra pessoa", HttpStatus.FORBIDDEN);
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
