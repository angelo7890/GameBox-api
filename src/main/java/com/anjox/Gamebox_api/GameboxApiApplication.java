package com.anjox.Gamebox_api;

import com.anjox.Gamebox_api.config.RabbitMQConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GameboxApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(GameboxApiApplication.class, args);
	}

}
