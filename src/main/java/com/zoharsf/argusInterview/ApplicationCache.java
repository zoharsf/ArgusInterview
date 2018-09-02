package com.zoharsf.argusInterview;

import com.zoharsf.argusInterview.model.IncomingMessage;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Component
public class ApplicationCache {
	private long timestamp;
	@Getter
	private String payload;
	
	public void updateCache(IncomingMessage incomingMessage) {
		if (incomingMessage.getTimestamp() > timestamp) {
			this.timestamp = System.currentTimeMillis();
			this.payload = incomingMessage.getPayload();
		}
	}
	
	public void updateCache(String payload) {
		this.timestamp = System.currentTimeMillis();
		this.payload = payload;
	}
}
