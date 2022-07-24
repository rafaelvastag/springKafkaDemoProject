package com.vastag.microservices.twitter.to.kafka.listener;

import org.springframework.stereotype.Component;

import com.vastag.microservices.config.KafkaConfigData;
import com.vastag.microservices.demo.kafka.avro.model.TwitterAvroModel;
import com.vastag.microservices.demo.kafka.producer.service.KafkaProducer;
import com.vastag.microservices.twitter.to.kafka.transformer.TwitterStatusToAvroTransformer;

import lombok.extern.slf4j.Slf4j;
import twitter4j.Status;
import twitter4j.StatusAdapter;

@Slf4j
@Component
public class TwitterKafkaStatusListener extends StatusAdapter {

	private final KafkaConfigData kafkaConfigData;
	private final KafkaProducer<Long, TwitterAvroModel> kafkaProducer;
	private final TwitterStatusToAvroTransformer twitterStatusToAvroTransformer;
	
	public TwitterKafkaStatusListener(KafkaConfigData kafkaConfigData,
			KafkaProducer<Long, TwitterAvroModel> kafkaProducer,
			TwitterStatusToAvroTransformer twitterStatusToAvroTransformer) {
		super();
		this.kafkaConfigData = kafkaConfigData;
		this.kafkaProducer = kafkaProducer;
		this.twitterStatusToAvroTransformer = twitterStatusToAvroTransformer;
	}



	@Override
	public void onStatus(Status status) {
		log.info("Received status text {} sending to kafka topic {}", status.getText(), kafkaConfigData.getTopicName());
		TwitterAvroModel twitterAvroModel = twitterStatusToAvroTransformer.getTwitterAvroModelFromStatus(status);
		kafkaProducer.send(kafkaConfigData.getTopicName(), twitterAvroModel.getUserId(), twitterAvroModel);
	}
}
