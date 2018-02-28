package com.arvato.pi.jms.consumer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.arvato.pi.data.PIDocument;

public class SubmitRetryLimitException extends SubmitFailException {
	private static final long serialVersionUID = 1L;

	public SubmitRetryLimitException(PIDocument doc, String message) {
		super(doc, message);
		// TODO Auto-generated constructor stub
	}
	
	public String getMessage() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		String message = super.getMessage();
		PIDocument doc = this.getDoc();
		List<LocalDateTime> retryTimes = doc.getRetryTimes();
		for(int i=0;i<retryTimes.size();i++) {
			LocalDateTime tm = retryTimes.get(i);
			message += "\n Retry at ";
			message += tm.format(formatter);
		}
		return message;
	}

}
