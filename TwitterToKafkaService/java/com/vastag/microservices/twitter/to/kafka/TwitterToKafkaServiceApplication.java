package com.vastag.microservices.twitter.to.kafka;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import com.vastag.microservices.config.TwitterToKafkaServiceConfigData;
import com.vastag.microservices.twitter.to.kafka.runner.StreamRunner;

import lombok.RequiredArgsConstructor;

@ComponentScan(basePackages = "com.vastag.microservices")
@RequiredArgsConstructor
@SpringBootApplication
public class TwitterToKafkaServiceApplication implements CommandLineRunner {

	private static final Logger LOG = LoggerFactory.getLogger(TwitterToKafkaServiceApplication.class);

	private final TwitterToKafkaServiceConfigData twitterToKafkaServiceConfigData;

	private final StreamRunner streamRunner;

	public static void main(String... args) {
		SpringApplication.run(TwitterToKafkaServiceApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		LOG.info("App starts...");
		LOG.info(Arrays.toString(twitterToKafkaServiceConfigData.getTwitterKeywords().toArray(new String[] {})));
		streamRunner.start();
	}
}
