package com.anjox.Gamebox_api.cloudinary.service;

import com.anjox.Gamebox_api.dto.ResponseUrlPictureDto;
import com.anjox.Gamebox_api.exeption.MessageErrorExeption;
import com.anjox.Gamebox_api.security.components.UserRequestAuthorizationManager;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final Logger logger = LoggerFactory.getLogger(CloudinaryService.class);

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public ResponseUrlPictureDto sendPictureFromCloud(MultipartFile picture) {
        logger.info("Iniciando Upload de foto");
        try {
            logger.info("Convertendo multipart file em file");
            File file = convertPictureToFile(picture);
            Map uploadResult = cloudinary.uploader().upload(file, ObjectUtils.emptyMap());
            String url = (String) uploadResult.get("url");
            String imageId = (String) uploadResult.get("public_id");
            file.delete();
            logger.info("Retornando o response picture dto");
            return new ResponseUrlPictureDto(url, imageId);
        } catch (IOException e) {
            logger.error("Error ao fazer upload de foto: "+e.getMessage());
            throw new MessageErrorExeption("Erro ao fazer upload da img", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public Map deletePictureByIdFromCloud(String id) {
        logger.info("Iniciando o deletar foto");
        try{
            return cloudinary.uploader().destroy(id, ObjectUtils.emptyMap());
        } catch (IOException e) {
            logger.error("Error ao deletar foto: "+e.getMessage());
            throw new MessageErrorExeption("Erro ao deletar picture", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private File convertPictureToFile(MultipartFile multipartFile){
        logger.info("Iniciando a conversao de multipartFile para file");
        try {
            File file = new File(multipartFile.getOriginalFilename());
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(multipartFile.getBytes());
            fos.close();
            logger.info("Retornando file");
            return file;
        } catch (IOException e) {
            logger.error("Error ao converter multipartFile: "+e.getMessage());
            throw new MessageErrorExeption("Erro ao converter multipartFile para Fila", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
