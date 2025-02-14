package com.anjox.Gamebox_api.service;

import com.anjox.Gamebox_api.dto.ResponseUrlPictureDto;
import com.anjox.Gamebox_api.exeption.MessageErrorExeption;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {
    private final Cloudinary cloudinary;

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public ResponseUrlPictureDto sendPictureFromCloud(MultipartFile picture ) {
        try {
            File file = convertPictureToFile(picture);
            Map uploadResult = cloudinary.uploader().upload(file, ObjectUtils.emptyMap());
            String url = (String) uploadResult.get("url");
            String imageid = (String) uploadResult.get("public_id");
            file.delete();
            return new ResponseUrlPictureDto(url, imageid);
        } catch (IOException e) {
            throw new MessageErrorExeption("erro ao fazer upload da img", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public Map deletePictureByIdFromCloud(String id) {
        try{
            return cloudinary.uploader().destroy(id, ObjectUtils.emptyMap());
        } catch (IOException e) {
            throw new MessageErrorExeption("erro ao fazer deletar picture", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private File convertPictureToFile(MultipartFile multipartFile){
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
