package com.arvato.pi.data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class PIDocument implements Serializable {
	private static final long serialVersionUID = -2950111455153223532L;
	private LocalDateTime receivedTime;
	private String payload;
	private int retry = 0;
	private ArrayList<LocalDateTime> retryTimes;
	
	public LocalDateTime getReceivedTime() {
		return receivedTime;
	}

	public void setReceivedTime(LocalDateTime receivedTime) {
		this.receivedTime = receivedTime;
	}

	public String getPayload() {
		return payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}

	public PIDocument() {
		this("");
	}
	
	public PIDocument(String payload) {
		receivedTime = LocalDateTime.now();
		setRetryTimes(new ArrayList<LocalDateTime>());
		setPayload(payload);
	}
	
	public boolean equals(PIDocument toBeChecked) {
		if (!toBeChecked.getPayload().equals(getPayload())) {
			return false;
		}
		if (!toBeChecked.getReceivedTime().equals(getReceivedTime())) {
			return false;
		}
		return true;
	}

	public int getRetry() {
		return retry;
	}

	public void setRetry(int retry) {
		this.retry = retry;
	}
	
	public void logRetry() {
		retry ++;
		retryTimes.add(LocalDateTime.now());
	}

	public ArrayList<LocalDateTime> getRetryTimes() {
		return retryTimes;
	}

	public void setRetryTimes(ArrayList<LocalDateTime> retryTimes) {
		this.retryTimes = retryTimes;
	}
}
