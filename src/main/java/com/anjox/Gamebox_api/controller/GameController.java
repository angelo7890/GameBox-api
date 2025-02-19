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

    @GetMapping("/{idGame}")
    public ResponseEntity<ResponseGameDto> getGameById(@PathVariable("idGame") Long idGame) {
        String usernameFromToken = SecurityContextHolder.getContext().getAuthentication().getName();
        ResponseGameDto dto = gameService.getGameById(idGame, usernameFromToken);
        return ResponseEntity.ok().body(dto);
    }

    @GetMapping("/findAll")
    public ResponseEntity<ResponsePaginationGameDto> getAllGames(@RequestParam int page, @RequestParam int size) {
        return ResponseEntity.ok().body(gameService.getAllGames(size, page));
    }

    @GetMapping("/filter/{idUser}")
    public ResponseEntity<ResponsePaginationGameDto> filterGames(@PathVariable("idUser") Long idUser, @RequestParam String genre , @RequestParam int page, @RequestParam int size) {
        String usernameFromToken = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok().body(gameService.filterGamesByGenre( idUser ,genre, usernameFromToken, size, page));
    }

    @GetMapping("user/{idUser}")
    public ResponseEntity<ResponsePaginationGameDto> getAllGamesByUserId(@PathVariable("idUser") Long idUser , @RequestParam int page, @RequestParam int size) {
        String usernameFromToken = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok().body(gameService.getGamesByUserId(idUser , usernameFromToken , page, size));
    }

    @PutMapping("/update/{idGame}")
    public ResponseEntity<?> updateGame(@PathVariable("idGame") Long idGame, @RequestBody RequestUpdateGameDto requestUpdateGameDto) {
        String usernameFromToken = SecurityContextHolder.getContext().getAuthentication().getName();
        gameService.updateGameById(idGame, requestUpdateGameDto, usernameFromToken);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete/{idGame}")
    public ResponseEntity<?> deleteGameById(@PathVariable("idGame") Long idGame) {
        String usernameFromToken = SecurityContextHolder.getContext().getAuthentication().getName();
        gameService.deleteGameById(idGame, usernameFromToken);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/deleteAll/{userId}")
    public ResponseEntity<?> deleteAllGamesByUserId(@PathVariable("userId") Long userId) {
        String usernameFromToken = SecurityContextHolder.getContext().getAuthentication().getName();
        gameService.deleteAllGamesByUserId(userId, usernameFromToken);
        return ResponseEntity.ok().build();
    }


}
