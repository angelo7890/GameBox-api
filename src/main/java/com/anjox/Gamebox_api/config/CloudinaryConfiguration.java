package com.anjox.Gamebox_api.config;


import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfiguration {

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(
                ObjectUtils.asMap(
                        "cloud_name", "dlsnut9o1",
                        "api_key", "519759576274498",
                        "api_secret", "Zp4UxQxSfyLSiM48wvRj0VKWHo8"
                ));
    }
}
