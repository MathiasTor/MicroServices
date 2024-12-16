package no.micro.livesearch_service2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class LivesearchService2Application {

	public static void main(String[] args) {
		SpringApplication.run(LivesearchService2Application.class, args);
	}

}
