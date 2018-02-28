package com.arvato.pi.jms.consumer;

import com.arvato.pi.data.PIDocument;

public class SubmitFailException extends Exception {
	private static final long serialVersionUID = 1L;
	private PIDocument doc;
	
	public SubmitFailException(PIDocument doc, String message) {
		super(message);
		this.doc = doc;
	}
	
	public PIDocument getDoc() {
		
		return doc;
	}
}
