package com.anjox.Gamebox_api.util;

import java.util.UUID;

public class GeneratorNewPassword {
    public static String generateNewPassword() {
            UUID uuid = UUID.randomUUID();
            String truncatedUUID;
            return truncatedUUID = uuid.toString().replace("-", "").substring(0, 15);
    }
}
