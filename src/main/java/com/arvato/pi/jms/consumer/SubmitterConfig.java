package com.arvato.pi.jms.consumer;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;

@Configuration
@EnableJms
public class SubmitterConfig {
	@Value("${activemq.broker-url}")
	private String brokerUrl;
	@Value("${activemq.queue.concurrency}")
	private String concurrency;
	
	@Bean
	public ActiveMQConnectionFactory activeMQConnectionFactory() {
		ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory();
		activeMQConnectionFactory.setBrokerURL(brokerUrl);
		return activeMQConnectionFactory;
	}
	
	@Bean
	public DefaultJmsListenerContainerFactory jmsListenerContainerFactory() {
		DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
		factory.setConnectionFactory(activeMQConnectionFactory());
		factory.setConcurrency(concurrency);
		return factory;
	}
	
	@Bean
	public Submitter submitter() {
		return new Submitter();
	}
	
	@Bean
	public SubmitHandler submitterHandler() {
		return new SubmitHandler();
	}
}
