package ru.runyk.meteostation_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling  // Для периодических задач (Telegram, чтение Arduino)
public class HomeMeteostationApplication {
	public static void main(String[] args) {
		SpringApplication.run(HomeMeteostationApplication.class, args);
	}
}