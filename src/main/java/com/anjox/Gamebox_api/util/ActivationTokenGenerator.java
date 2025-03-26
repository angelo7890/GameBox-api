package com.anjox.Gamebox_api.util;

import com.anjox.Gamebox_api.security.components.UserRequestAuthorizationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;

public class ActivationTokenGenerator {
    public static String accountActivatorTokenGenerator() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder token = new StringBuilder();
        for (int i = 0; i < 64; i++) {
            int index = random.nextInt(characters.length());
            token.append(characters.charAt(index));
        }
        return token.toString();
    }
}
