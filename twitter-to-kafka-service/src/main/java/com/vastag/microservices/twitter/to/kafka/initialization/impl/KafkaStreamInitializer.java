package com.vastag.microservices.twitter.to.kafka.initialization.impl;

import org.springframework.stereotype.Component;

import com.vastag.microservices.config.KafkaConfigData;
import com.vastag.microservices.kafka.admin.client.KafkaAdminClient;
import com.vastag.microservices.twitter.to.kafka.initialization.StreamInitializer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class KafkaStreamInitializer implements StreamInitializer {

	private final KafkaConfigData kafkaConfigData;
	private final KafkaAdminClient kafkaAdminClient;

	public KafkaStreamInitializer(KafkaConfigData kafkaConfigData, KafkaAdminClient kafkaAdminClient) {
		super();
		this.kafkaConfigData = kafkaConfigData;
		this.kafkaAdminClient = kafkaAdminClient;
	}

	@Override
	public void init() {
		kafkaAdminClient.createTopics();
		kafkaAdminClient.checkSchemaRegistry();

		log.info("Topics with name {} is ready for operations!", kafkaConfigData.getTopicNamesToCreate().toArray());
	}

}
