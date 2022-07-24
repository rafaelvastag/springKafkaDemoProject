package com.vastag.microservices.twitter.to.kafka;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import com.vastag.microservices.twitter.to.kafka.initialization.StreamInitializer;
import com.vastag.microservices.twitter.to.kafka.runner.StreamRunner;

@ComponentScan(basePackages = "com.vastag.microservices")
@SpringBootApplication
public class TwitterToKafkaServiceApplication implements CommandLineRunner {

	private final StreamRunner streamRunner;
	
    private final StreamInitializer streamInitializer;

	public TwitterToKafkaServiceApplication(StreamInitializer streamInitializer, StreamRunner streamRunner) {
		this.streamInitializer = streamInitializer;
		this.streamRunner = streamRunner;
	}

	public static void main(String... args) {
		SpringApplication.run(TwitterToKafkaServiceApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
        streamInitializer.init();
		streamRunner.start();
	}
}
