package com.arvato.pi.jms.consumer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.arvato.pi.data.PIDocument;

public class Submitter {
	private static final Logger LOGGER = LoggerFactory.getLogger(Submitter.class);

	public void submitPIDocument(PIDocument doc) throws SubmitFailException {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd H:m:s");
		LocalDateTime docTm = doc.getReceivedTime();
		
		// Below code is just a demo sample to show how raise SubmitFailException
		if (doc.getPayload().length() > 100) {
			throw new SubmitFailException(doc, "PIDocument is fail to submit!");
		}
		
		String sTm = docTm.format(formatter);
		LOGGER.info("Submitting doc; Time:" + sTm);
		LOGGER.info("Submitting doc; payload:" + doc.getPayload());
	}

}
