package com.anjox.Gamebox_api.service;
import com.anjox.Gamebox_api.cloudinary.service.CloudinaryService;
import com.anjox.Gamebox_api.dto.*;
import com.anjox.Gamebox_api.entity.GameEntity;
import com.anjox.Gamebox_api.entity.UserEntity;
import com.anjox.Gamebox_api.enums.UserEnum;
import com.anjox.Gamebox_api.exeption.MessageErrorExeption;
import com.anjox.Gamebox_api.rabbitmq.producer.RabbitMQUserProducer;
import com.anjox.Gamebox_api.repository.GameRepository;
import com.anjox.Gamebox_api.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class GameService {

    private final GameRepository gameRepository;
    private final UserRepository userRepository;
    private final CloudinaryService cloudinaryService;
    private final Logger logger = LoggerFactory.getLogger(GameService.class);

    public GameService(GameRepository gameRepository, UserRepository userRepository, CloudinaryService cloudinaryService) {
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
        this.cloudinaryService = cloudinaryService;
    }

    public void createGame(RequestCreateGameDto dto){
        logger.info("iniciando a criaçao de jogo");
        if(gameRepository.existsByUserIdAndTitle(dto.userId(), dto.title())){
            logger.error("nome do jogo ja existente na lista de jogos");
            throw new MessageErrorExeption("ja existe um jogo com esse nome na sua lista de jogos", HttpStatus.CONFLICT);
        }
        if (dto.price().compareTo(BigDecimal.ZERO) < 0) {
            logger.error("Preço do jogo nao pode ser negativo");
            throw new MessageErrorExeption("O preço não pode ser negativo", HttpStatus.BAD_REQUEST);
        }
        logger.info("criando objeto game entity");
        GameEntity game = new GameEntity(
                dto.userId(),
                dto.title(),
                dto.description(),
                dto.genre(),
                dto.price(),
                dto.imageUrl(),
                dto.imageId()
        );
        logger.info("salvando jogo");
        gameRepository.save(game);
    }

    public ResponseGameDto getGameById(Long id){
        logger.info("iniciando a bunca de jogo por id");
        GameEntity game =  gameRepository.findByid(id);
        if(game!=null){
                logger.info("Retornando jogo");
                return new ResponseGameDto(
                        game.getId(),
                        game.getTitle(),
                        game.getDescription(),
                        game.getGenre(),
                        game.getPrice(),
                        game.getImageUrl()
                );
        }
        logger.info("jogo nao encontrado");
        throw new MessageErrorExeption("game nao encontrado", HttpStatus.NOT_FOUND);
    }

    public ResponsePaginationGameDto getAllGames(int size, int page){
        logger.info("iniciando a busca de todos os jogos");
         Pageable pageable = PageRequest.of(page, size);
         logger.info("criando paginaçao");
         Page<GameEntity> games = gameRepository.findAll(pageable);
         logger.info("retornando paginaçao");
        return getResponsePaginationGameDto(games);
    }

    public ResponsePaginationGameDto filterGamesByGenre(Long userId , String genre, int size, int page){
        logger.info("iniciando a buca de jogo por genero");
        UserEntity user = userRepository.findByid(userId);
        if(user == null){
            logger.error("impossivel filtrar, pois o usuario do jogo esta vazio ou nao foi encontrado");
            throw new MessageErrorExeption("impossivel filtrar, pois o usuario do jogo esta vazio ou nao foi encontrado", HttpStatus.BAD_REQUEST);
        }
        logger.info("criando a paginaçao");
        Pageable pageable = PageRequest.of(page, size);
        Page<GameEntity> games = gameRepository.findByGenreAndUserId(genre, user.getId(), pageable);
        logger.info("retornando paginaçao");
        return getResponsePaginationGameDto(games);
    }

    public ResponsePaginationGameDto getGamesByUserId(Long userId, int page, int size){
        logger.info("iniciando a buca de jogo por usuario");
        UserEntity user = userRepository.findByid(userId);
        if(user != null){
                Pageable pageable  = PageRequest.of(page, size);
                Page<GameEntity> gamesByUserId=  gameRepository.findByUserId(userId, pageable);
                logger.info("Retornando paginaçao");
                return getResponsePaginationGameDto(gamesByUserId);
        }
        logger.error("id de usuario nao encontrado");
        throw new MessageErrorExeption("Id de usuario nao encontrado", HttpStatus.NOT_FOUND);
    }

    @Transactional
    public void updateGameById(Long gameId, RequestUpdateGameDto dto) {
        logger.info("iniciando a atualizçao de jogo");

        logger.info("buscando jogo");
        GameEntity game = gameRepository.findByid(gameId);

        if (game == null) {
            logger.error("jogo nao encontrado");
            throw new MessageErrorExeption("Game não encontrado", HttpStatus.BAD_REQUEST);
        }

        if (gameRepository.existsByUserIdAndTitle(game.getUserId(), dto.title())) {
            logger.info("usuario ja possui um jogo com esse titulo");
            throw new MessageErrorExeption("Você já possui um jogo com esse título", HttpStatus.CONFLICT);
        }
        game.setTitle(dto.title());

        game.setDescription(dto.description());

        game.setGenre(dto.genre());

        logger.info("verificando preço");
        if (dto.price().compareTo(BigDecimal.ZERO) < 0) {
            logger.error("preço invalido");
            throw new MessageErrorExeption("O preço não pode ser negativo", HttpStatus.BAD_REQUEST);
        }

        game.setPrice(dto.price());
        logger.info("salvando jogo");
        gameRepository.save(game);
    }

    @Transactional
    public void updatePictureForGameById(Long gameId,RequestUpdatePictureDto dto){
        logger.info("Iniciando a atualizaçao de foto");
        logger.info("buscando jogo");
        GameEntity game =  gameRepository.findByid(gameId);
        if(game!=null){
            if(game.getImageUrl()!=null){
                logger.info("Excluindo foto antiga");
                cloudinaryService.deletePictureByIdFromCloud(game.getImageId());
            }
            logger.info("salvando as novas informaçoes");
            game.setImageUrl(dto.url());
            game.setImageId(dto.pictureId());
            gameRepository.save(game);
            logger.info("salvo com sucesso");
        }
        logger.info("jogo nao encontrado");
    }

    @Transactional
    public void deleteGameById(Long id ){
        logger.info("deletando jogo");
        gameRepository.deleteById(id);
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
