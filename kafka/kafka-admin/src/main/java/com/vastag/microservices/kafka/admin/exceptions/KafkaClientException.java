package com.vastag.microservices.kafka.admin.exceptions;

public class KafkaClientException extends RuntimeException{
	private static final long serialVersionUID = 1L;

	public KafkaClientException() {
	}
	
	public KafkaClientException(String message) {
		super(message);
	}
	
	public KafkaClientException(String message, Throwable cause) {
		super(message, cause);
	}
}
