package com.anjox.Gamebox_api.controller;


import com.anjox.Gamebox_api.dto.RequestCreateGameDto;
import com.anjox.Gamebox_api.dto.ResponseUrlPictureDto;
import com.anjox.Gamebox_api.service.GameService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    public ResponseEntity<?> getGameById(@PathVariable("id") String id) {
        return null;
    }

    @GetMapping("/allGames")
    public ResponseEntity<?> getAllGames() {
        return null;
    }

    @GetMapping("user/{id}")
    public ResponseEntity<?> getGameByUserId(@PathVariable("id") Long id) {
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
