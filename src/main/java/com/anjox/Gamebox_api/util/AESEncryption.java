package com.anjox.Gamebox_api.util;

import com.anjox.Gamebox_api.exeption.MessageErrorExeption;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class AESEncryption {

    @Value("${aes.secret.key}")
    private String aesSecret;

    private static String secretKey;

    @PostConstruct
    private void init() {
        secretKey = aesSecret;
    }

    public static String encryptPassword(String unencryptedPassword){
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            byte[] encrypted = cipher.doFinal(unencryptedPassword.getBytes());
            return Base64.getEncoder().encodeToString(encrypted);
        }catch (Exception e){
            throw new MessageErrorExeption("Erro ao encriptar senha", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
