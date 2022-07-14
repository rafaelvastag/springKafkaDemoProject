package com.vastag.microservices.twitter.to.kafka.runner.impl;

import java.util.Arrays;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

import com.vastag.microservices.twitter.to.kafka.config.TwitterToKafkaServiceConfigData;
import com.vastag.microservices.twitter.to.kafka.listener.TwitterKafkaStatusListener;
import com.vastag.microservices.twitter.to.kafka.runner.StreamRunner;

import twitter4j.FilterQuery;
import twitter4j.TwitterException;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;

@Component
@ConditionalOnExpression("${twitter-to-kafka-service.enable-mock-tweets} && not ${twitter-to-kafka-service.enable-v2-tweets}")
public class TwitterKafkaStreamRunner implements StreamRunner {

	private static final Logger LOG = LoggerFactory.getLogger(TwitterKafkaStreamRunner.class);

	private final TwitterToKafkaServiceConfigData twitterToKafkaServiceConfigData;

	private final TwitterKafkaStatusListener twitterKafkaStatusListener;

	private TwitterStream twitterStream;

	public TwitterKafkaStreamRunner(TwitterToKafkaServiceConfigData configData,
			TwitterKafkaStatusListener statusListener) {
		this.twitterToKafkaServiceConfigData = configData;
		this.twitterKafkaStatusListener = statusListener;
	}

	@Override
	public void start() throws TwitterException {
		twitterStream = new TwitterStreamFactory().getInstance();
		twitterStream.addListener(twitterKafkaStatusListener);
		addFilter();
	}

	@PreDestroy
	public void shutdown() {
		if (twitterStream != null) {
			LOG.info("Closing twitter stream!");
			twitterStream.shutdown();
		}
	}

	private void addFilter() {
		String[] keywords = twitterToKafkaServiceConfigData.getTwitterKeywords().toArray(new String[0]);
		FilterQuery filterQuery = new FilterQuery(keywords);
		twitterStream.filter(filterQuery);
		LOG.info("Started filtering twitter stream for keywords {}", Arrays.toString(keywords));
	}
}
