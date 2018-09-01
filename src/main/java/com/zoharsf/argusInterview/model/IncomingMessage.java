package com.zoharsf.argusInterview.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class IncomingMessage implements Serializable {
    @JsonProperty("timestamp")
    private long timestamp;
    @JsonProperty("payload")
    private String payload;

    public IncomingMessage(String payload) {
        this.timestamp = System.currentTimeMillis();
        this.payload = payload;
    }

    @Override
    public String toString() {
        return "{" + "timestamp: " + this.timestamp + ", " +
                "payload: " + this.payload + "}";
    }
}
