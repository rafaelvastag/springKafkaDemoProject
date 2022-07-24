package com.vastag.microservices.twitter.to.kafka.transformer;

import org.springframework.stereotype.Component;

import com.vastag.microservices.demo.kafka.avro.model.TwitterAvroModel;

import twitter4j.Status;

@Component
public class TwitterStatusToAvroTransformer {

	public TwitterAvroModel getTwitterAvroModelFromStatus(Status status) {
		return TwitterAvroModel
				 .newBuilder()
                .setId(status.getId())
                .setUserId(status.getUser().getId())
                .setText(status.getText())
                .setCreatedAt(status.getCreatedAt().getTime())
                .build();
	}
}
