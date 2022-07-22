package com.vastag.microservices.twitter.to.kafka;

import com.vastag.microservices.config.TwitterToKafkaServiceConfigData;
import com.vastag.microservices.twitter.to.kafka.initialization.StreamInitializer;
import com.vastag.microservices.twitter.to.kafka.runner.StreamRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import java.util.Arrays;

@ComponentScan(basePackages = "com.vastag.microservices")
@SpringBootApplication
public class TwitterToKafkaServiceApplication implements CommandLineRunner {

	private static final Logger LOG = LoggerFactory.getLogger(TwitterToKafkaServiceApplication.class);

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
