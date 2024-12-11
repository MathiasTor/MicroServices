package com.example.playpalleaderboardservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PlaypalLeaderboardServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(PlaypalLeaderboardServiceApplication.class, args);
	}
}
