package com.vastag.microservices.kafka.admin.config;

import java.util.Map;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.admin.AdminClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;

import com.vastag.microservices.config.KafkaConfigData;

@EnableRetry
@Configuration
public class KafkaAdminConfig {

	private final KafkaConfigData kafkaConfigData;

	public KafkaAdminConfig(KafkaConfigData kafkaConfigData) {
		super();
		this.kafkaConfigData = kafkaConfigData;
	}

	public AdminClient adminClient() {
		return AdminClient
				.create(Map.of(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, kafkaConfigData.getBootstrapServers()));
	}

}
