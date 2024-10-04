package com.client.liveowl;

import javafx.application.Application;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LiveowlApplication {

	public static void main(String[] args) {
		SpringApplication.run(LiveowlApplication.class, args);
		Application.launch(JavaFxApplication.class, args);
	}

}
