package com.anjox.Gamebox_api.controller;


import com.anjox.Gamebox_api.dto.RequestCreateGameDto;
import com.anjox.Gamebox_api.dto.ResponseGameDto;
import com.anjox.Gamebox_api.dto.ResponsePaginationGameDto;
import com.anjox.Gamebox_api.service.GameService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/game")
public class GameController {
    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping("/createGame")
    public ResponseEntity<?> createGame(@RequestBody @Valid RequestCreateGameDto requestCreateGameDto) {
        return null;
    }

    @GetMapping("/{id]")
    public ResponseEntity<ResponseGameDto> getGameById(@PathVariable("id") String id) {
        return null;
    }

    @GetMapping("/allGames")
    public ResponseEntity<ResponsePaginationGameDto> getAllGames(@RequestParam int page, @RequestParam int size) {
        return null;
    }

    @GetMapping("user/{id}")
    public ResponseEntity<ResponsePaginationGameDto> getAllGamesByUserId(@PathVariable("id") Long id , @RequestParam int page, @RequestParam int size) {
        return null;
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteGameById(@PathVariable("id") Long id) {
        return null;
    }

    @DeleteMapping("/deleteAll/{userId]")
    public ResponseEntity<?> deleteAllGamesByUserId(@PathVariable("userId") Long userId) {
        return null;
    }


}
