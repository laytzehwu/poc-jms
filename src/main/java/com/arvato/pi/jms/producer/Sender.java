package com.arvato.pi.jms.producer;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;

import com.arvato.pi.data.PIDocument;
import com.arvato.pi.jms.PIDocTransistor;

public class Sender {
	private static final Logger LOGGER = LoggerFactory.getLogger(Sender.class);

	@Autowired
	private JmsTemplate jmsTemplate;
	
	public void send(String destination, String message) {
		LOGGER.info("Sending message = '{}' to destination = '{}'", message, destination);
		jmsTemplate.convertAndSend(destination, message);
	}
	
	public void sendPIDoc(String destination, PIDocument doc) throws IOException {
		send(destination, PIDocTransistor.docToString(doc));
	}
	
	public void resendPIDoc(String destination, PIDocument doc) throws IOException {
		sendPIDoc(destination, doc);
	}
}
