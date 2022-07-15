package com.vastag.microservices.kafka.admin.client;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.admin.TopicListing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.retry.RetryContext;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import com.vastag.microservices.config.KafkaConfigData;
import com.vastag.microservices.config.RetryConfigData;
import com.vastag.microservices.kafka.admin.exceptions.KafkaClientException;

@Component
public class KafkaAdminClient {

	private static final Logger LOG = LoggerFactory.getLogger(KafkaAdminClient.class);

	private final KafkaConfigData kafkaConfigData;
	private final RetryConfigData retryConfigData;
	private final AdminClient adminClient;
	private final RetryTemplate retryTemplate;
	private final WebClient webClient;

	public KafkaAdminClient(KafkaConfigData kafkaConfigData, RetryConfigData retryConfigData, AdminClient adminClient,
			RetryTemplate retryTemplate, WebClient webClient) {
		super();
		this.kafkaConfigData = kafkaConfigData;
		this.retryConfigData = retryConfigData;
		this.adminClient = adminClient;
		this.retryTemplate = retryTemplate;
		this.webClient = webClient;
	}

	public void createTopics() {
		try {
			CreateTopicsResult createTopicsResult = retryTemplate.execute(this::doCreateTopics);
			LOG.info("Create topic result {}", createTopicsResult.values().values());
		} catch (Throwable t) {
			throw new KafkaClientException("Reached max number os retry for creating kafka topic(s)");
		}
		checkTopicsCreated();
	}

	private CreateTopicsResult doCreateTopics(RetryContext retryContext) {
		List<String> topicNames = kafkaConfigData.getTopicNamesToCreate();
		LOG.info("Creating {} topics(s), attempt {}", topicNames.size(), retryContext.getRetryCount());

		List<NewTopic> kafkaTopics = topicNames.stream().map(topic -> new NewTopic(topic.trim(),
				kafkaConfigData.getNumOfPartitions(), kafkaConfigData.getReplicationFactor()))
				.collect(Collectors.toList());

		return adminClient.createTopics(kafkaTopics);
	}

	public void checkTopicsCreated() {
		Collection<TopicListing> topics = getTopics();
		int retryCount = 1;
		Integer maxRetry = retryConfigData.getMaxAttempts();
		int multiplier = retryConfigData.getMultiplier().intValue();
		Long sleepTimeMs = retryConfigData.getSleepTimeMs();
		for (String topic : kafkaConfigData.getTopicNamesToCreate()) {
			while (!isTopicCreated(topics, topic)) {
				checkMaxRetry(retryCount++, maxRetry);
				sleep(sleepTimeMs);
				sleepTimeMs *= multiplier;
				topics = getTopics();
			}
		}
	}

	public void checkSchemaRegistry() {
		int retryCount = 1;
		Integer maxRetry = retryConfigData.getMaxAttempts();
		int multiplier = retryConfigData.getMultiplier().intValue();
		Long sleepTimeMs = retryConfigData.getSleepTimeMs();
		while (!getSchemaRegistryStatus().is2xxSuccessful()) {
			checkMaxRetry(retryCount++, maxRetry);
			sleep(sleepTimeMs);
			sleepTimeMs *= multiplier;
		}
	}

	@SuppressWarnings("deprecation")
	private HttpStatus getSchemaRegistryStatus() {
		try {
			return webClient.get().uri(kafkaConfigData.getSchemaRegistryUrl()).accept(MediaType.APPLICATION_JSON)
					.exchange().map(ClientResponse::statusCode).block();
		} catch (Exception e) {
			return HttpStatus.SERVICE_UNAVAILABLE;
		}
	}

	private Collection<TopicListing> getTopics() {
		Collection<TopicListing> topics;
		try {
			topics = retryTemplate.execute(this::doGetTopics);
		} catch (Throwable t) {
			throw new KafkaClientException("Reached max number of retry for reading kafka topic(s)!", t);
		}
		return topics;
	}

	private Collection<TopicListing> doGetTopics(RetryContext retryContext)
			throws ExecutionException, InterruptedException {
		LOG.info("Reading kafka topic {}, attempt {}", kafkaConfigData.getTopicNamesToCreate().toArray(),
				retryContext.getRetryCount());
		Collection<TopicListing> topics = adminClient.listTopics().listings().get();
		if (topics != null) {
			topics.forEach(topic -> LOG.debug("Topic with name {}", topic.name()));
		}
		return topics;
	}

	private void sleep(Long sleepTimeMs) {
		try {
			Thread.sleep(sleepTimeMs);
		} catch (InterruptedException e) {
			throw new KafkaClientException("Error while sleeping for waiting new created topics!!");
		}
	}

	private void checkMaxRetry(int retry, Integer maxRetry) {
		if (retry > maxRetry) {
			throw new KafkaClientException("Reached max number of retry for reading kafka topic(s)!");
		}
	}

	private boolean isTopicCreated(Collection<TopicListing> topics, String topicName) {
		if (topics == null) {
			return false;
		}
		return topics.stream().anyMatch(topic -> topic.name().equals(topicName));
	}
}
