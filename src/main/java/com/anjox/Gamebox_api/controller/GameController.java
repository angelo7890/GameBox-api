package com.anjox.Gamebox_api.controller;


import com.anjox.Gamebox_api.dto.ResponseUrlPictureDto;
import com.anjox.Gamebox_api.service.GameService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/game")
public class GameController {
    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping("/upload")
    public ResponseEntity<ResponseUrlPictureDto> uploadFile(@RequestParam MultipartFile file) {
        ResponseUrlPictureDto dto = gameService.sendPictureFromCloud(file);
        return ResponseEntity.ok(dto);
    }

}
