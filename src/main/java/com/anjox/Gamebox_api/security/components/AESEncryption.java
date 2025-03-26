package com.anjox.Gamebox_api.security.components;
import com.anjox.Gamebox_api.exeption.MessageErrorExeption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;


@Component
public class AESEncryption {

    @Value("${aes.secret.key}")
    private String aesSecret;
    private final Logger logger = LoggerFactory.getLogger(AESEncryption.class);

    public String encryptPassword(String unencryptedPassword){
        logger.info("Iniciando encriptaçao de senha");
        try {
            if (aesSecret.length() != 16) {
                logger.error("A chave AES deve ter 16 caracteres (128 bits).");
                throw new MessageErrorExeption("Erro ao encriptar senha.", HttpStatus.INTERNAL_SERVER_ERROR);
            }
            logger.info("iniciando o secretKeySpec e cipher.");
            SecretKeySpec secretKeySpec = new SecretKeySpec(aesSecret.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            byte[] encrypted = cipher.doFinal(unencryptedPassword.getBytes());
            logger.info("Retornando senha encriptada.");
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            logger.error("Erro ao encriptar senha.", e);
            throw new MessageErrorExeption("Erro ao encriptar senha", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}