package com.anjox.Gamebox_api.service;
import com.anjox.Gamebox_api.dto.ResponseUrlPictureDto;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

@Service
public class GameService {

    private final Cloudinary cloudinary;

    public GameService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public ResponseUrlPictureDto sendPictureFromCloud(MultipartFile picture ) {
        try {
            File file = convertToFile(picture);
            Map uploadResult = cloudinary.uploader().upload(file, ObjectUtils.emptyMap());
            String url = (String) uploadResult.get("url");
            file.delete();
            return new ResponseUrlPictureDto(url);
        } catch (IOException e) {
            throw new RuntimeException("erro ao fazer upload da img"+e.getMessage());
        }
    }

    private File convertToFile(MultipartFile multipartFile){
    try {
        File file = new File(multipartFile.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(multipartFile.getBytes());
        fos.close();
        return file;
    } catch (IOException e) {
        throw new RuntimeException(e);
    }
    }
}
