package com.anjox.Gamebox_api.cloudinary.config;


import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfiguration {

    private static final Dotenv dotenv = Dotenv.load();
    private static final String CLOUD_NAME = dotenv.get("CLOUD_NAME");
    private static final String API_KEY = dotenv.get("API_KEY");
    private static final String API_SECRET = dotenv.get("API_SECRET");

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(
                ObjectUtils.asMap(
                        "cloud_name", CLOUD_NAME,
                        "api_key", API_KEY,
                        "api_secret", API_SECRET
                ));
    }
}
