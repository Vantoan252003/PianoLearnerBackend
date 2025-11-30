package com.piano.learn.PianoLearn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PianoLearnApplication {

	public static void main(String[] args) {
		SpringApplication.run(PianoLearnApplication.class, args);
	}

}
