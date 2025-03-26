package com.anjox.Gamebox_api.controller;


import com.anjox.Gamebox_api.dto.*;
import com.anjox.Gamebox_api.service.GameService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/game")
public class GameController {
    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping()
    public ResponseEntity<?> createGame( @Valid @RequestBody RequestCreateGameDto requestCreateGameDto) {
        gameService.createGame(requestCreateGameDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{gameId}")
    public ResponseEntity<ResponseGameDto> getGameById(@PathVariable("gameId") Long gameId) {
        ResponseGameDto dto = gameService.getGameById(gameId);
        return ResponseEntity.ok().body(dto);
    }

    @GetMapping("/findAll")
    public ResponseEntity<ResponsePaginationGameDto> getAllGames(@RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok().body(gameService.getAllGames(size, page));
    }

    @GetMapping("/filter/{userId}/{genre}")
    public ResponseEntity<ResponsePaginationGameDto> filterGames(@PathVariable("userId") Long userId,
                                                                 @PathVariable("genre") String genre ,
                                                                 @RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok().body(gameService.filterGamesByGenre( userId ,genre, size, page));
    }

    @GetMapping("user/{userId}")
    public ResponseEntity<ResponsePaginationGameDto> getAllGamesByUserId(@PathVariable("userId") Long userId ,
                                                                         @RequestParam(defaultValue = "0") int page,
                                                                         @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok().body(gameService.getGamesByUserId(userId, page, size));
    }

    @PutMapping("/update/{gameId}")
    public ResponseEntity<?> updateGame(@PathVariable("gameId") Long gameId, @RequestBody RequestUpdateGameDto requestUpdateGameDto) {
        gameService.updateGameById(gameId, requestUpdateGameDto);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/update/picture/{gameId}")
    public ResponseEntity<?> updatePicture(@PathVariable("gameId") Long gameId, @RequestBody RequestUpdatePictureDto requestUpdatePictureDto) {
        gameService.updatePictureForGameById(gameId, requestUpdatePictureDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{gameId}")
    public ResponseEntity<?> deleteGameById(@PathVariable("gameId") Long gameId) {
        gameService.deleteGameById(gameId);
        return ResponseEntity.ok().build();
    }
}
