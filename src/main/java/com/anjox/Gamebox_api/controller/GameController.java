package com.anjox.Gamebox_api.controller;


import com.anjox.Gamebox_api.dto.RequestCreateGameDto;
import com.anjox.Gamebox_api.dto.RequestUpdateGameDto;
import com.anjox.Gamebox_api.dto.ResponseGameDto;
import com.anjox.Gamebox_api.dto.ResponsePaginationGameDto;
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

    @PostMapping("/create")
    public ResponseEntity<?> createGame(@RequestBody @Valid RequestCreateGameDto requestCreateGameDto) {
        gameService.createGame(requestCreateGameDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{gameId}")
    public ResponseEntity<ResponseGameDto> getGameById(@PathVariable("gameId") Long gameid) {
        String usernameFromToken = SecurityContextHolder.getContext().getAuthentication().getName();
        ResponseGameDto dto = gameService.getGameById(gameid, usernameFromToken);
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
        String usernameFromToken = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok().body(gameService.filterGamesByGenre( userId ,genre, usernameFromToken, size, page));
    }

    @GetMapping("user/{userId}")
    public ResponseEntity<ResponsePaginationGameDto> getAllGamesByUserId(@PathVariable("userId") Long userId ,
                                                                         @RequestParam(defaultValue = "0") int page,
                                                                         @RequestParam(defaultValue = "10") int size) {
        String usernameFromToken = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok().body(gameService.getGamesByUserId(userId , usernameFromToken , page, size));
    }

    @PutMapping("/update/{gameId}")
    public ResponseEntity<?> updateGame(@PathVariable("gameId") Long gameId, @RequestBody RequestUpdateGameDto requestUpdateGameDto) {
        String usernameFromToken = SecurityContextHolder.getContext().getAuthentication().getName();
        gameService.updateGameById(gameId, requestUpdateGameDto, usernameFromToken);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete/{gameId}")
    public ResponseEntity<?> deleteGameById(@PathVariable("gameId") Long gameId) {
        String usernameFromToken = SecurityContextHolder.getContext().getAuthentication().getName();
        gameService.deleteGameById(gameId, usernameFromToken);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/deleteAll/{userId}")
    public ResponseEntity<?> deleteAllGamesByUserId(@PathVariable("userId") Long userId) {
        String usernameFromToken = SecurityContextHolder.getContext().getAuthentication().getName();
        gameService.deleteAllGamesByUserId(userId, usernameFromToken);
        return ResponseEntity.ok().build();
    }


}
