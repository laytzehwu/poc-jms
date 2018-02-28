package com.arvato.pi.jms.consumer;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;

import com.arvato.pi.data.PIDocument;
import com.arvato.pi.jms.PIDocTransistor;
import com.arvato.pi.jms.producer.Sender;

public class SubmitHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(SubmitHandler.class);
	@Value("${activemq.queue.cool-time}")
	private int cool_time;
	
	@Value("${activemq.queue.pi-doc}")
	private String queueName;

	@Autowired
	private Sender sender;
	
	@Autowired
	private Submitter submitter;

	@Value("${activemq.retry-limit}")
	private int retry_limit;
	
	public void resendPIDocument(PIDocument doc) throws SubmitRetryLimitException {
		if (!canPIDocumentRetry(doc)) {
			SubmitRetryLimitException ex = new SubmitRetryLimitException(doc, "PIDocument submission hit retry limit");
			throw ex;
		}
		try {
			doc.logRetry();
			sender.resendPIDoc(queueName, doc);
		} catch (IOException ex) {
			LOGGER.error("Something went wrong when re-queue PIDocument");
			LOGGER.error(ex.getMessage());
		}
	}

	@JmsListener(destination="${activemq.queue.pi-doc}")
	public void receiveMessage(String message) {
		
		PIDocument doc;
		try {
			doc = PIDocTransistor.decodePIDocument(message);
		} catch (ClassNotFoundException ex) {
			LOGGER.error("Fail to decode PIDocument");
			LOGGER.error(ex.getMessage());
			return;
		} catch (IOException ex) {
			LOGGER.error("Fail to decode PIDocument");
			LOGGER.error(ex.getMessage());
			return;
		}
		
		boolean needToRetry = true;
		try {
			coolDown(cool_time);
			submitter.submitPIDocument(doc);
			needToRetry = false;// To stop further action
		} catch (SubmitFailException e) {
			LOGGER.warn("PIDocument submit fail. Retry later(" + doc.getRetry() + ")");
		} catch (Exception ex) {
			LOGGER.warn("Something wrong when submit PIDocument. Retry later(" + doc.getRetry() + ")");
			LOGGER.error(ex.getMessage());
		}
		
		if (needToRetry) {
			try {
				resendPIDocument(doc);
			} catch (SubmitRetryLimitException ex) {
				LOGGER.error("Exceeded retry limit of submitting PIDocument");
				LOGGER.error(ex.getMessage());
				// TODO Start reporting process for missing doc
			}
		}

	}
	
	private void coolDown(int iMinSecond) {
		try {
			Thread.sleep(iMinSecond);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public int getRetryLimit() {
		return retry_limit;
	}
	
	public boolean canPIDocumentRetry(PIDocument doc) {
		return doc.getRetry() <= retry_limit;
	}
}
