package com.anjox.Gamebox_api.controller;


import com.anjox.Gamebox_api.dto.ResponseUrlPictureDto;
import com.anjox.Gamebox_api.service.CloudinaryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/picture")
public class CloudinaryController {

    private final CloudinaryService cloudinaryService;

    public CloudinaryController(CloudinaryService cloudinaryService) {
        this.cloudinaryService = cloudinaryService;
    }

    @PostMapping("/upload")
    public ResponseEntity<ResponseUrlPictureDto> uploadFile(@RequestParam MultipartFile file) {
        ResponseUrlPictureDto dto = cloudinaryService.sendPictureFromCloud(file);
        return ResponseEntity.ok(dto);
    }
}
