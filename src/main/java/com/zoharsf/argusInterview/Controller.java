package com.zoharsf.argusInterview;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.zoharsf.argusInterview.model.IncomingMessage;
import com.zoharsf.argusInterview.outgoing.ApplicationSyncHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class Controller {
	
	@Autowired
	private ApplicationCache applicationCache;
	
	@Autowired
	private ApplicationSyncHandler applicationSyncHandler;
	
	private static Gson gson = new GsonBuilder().create();
	
	//Incoming message from client. Update cache and send to neighboring application.
	@RequestMapping(value = "/api/resource", method = RequestMethod.POST)
	public void handleIncomingPostMessageFromClient(@RequestBody String payload) {
		log.info("Received POST message from client: {}.", payload);
		if (payload != null) {
			//update cache
			applicationCache.updateCache(payload);
			//create incoming message
			IncomingMessage incomingMessage = new IncomingMessage(payload);
			//update neighboring application instance
			applicationSyncHandler.sendUpdateToNeighboringApplicationInstance(incomingMessage);
		}
	}
	
	//Incoming message from neighboring application. Update cache only.
	@RequestMapping(value = "/api/sync", method = RequestMethod.POST)
	public void handleIncomingPostMessageFromApplication(@RequestBody String payload) {
		//parse
		if (payload != null) {
			log.info("Received sync message from neighboring application: {}.", payload);
			IncomingMessage incomingMessage = parse(payload);
			//update cache
			if (incomingMessage != null) {
				applicationCache.updateCache(incomingMessage);
				log.info("Updated cache with received sync message from neighboring application: {}.", incomingMessage);
			}
		}
	}
	
	//Parse incoming message from neighboring application
	private IncomingMessage parse(String payload) {
		//TODO this isn't working - parsing is failing
		IncomingMessage incomingMessage = null;
		try {
			incomingMessage = gson.fromJson(payload, IncomingMessage.class);
		}
		catch (JsonSyntaxException | IllegalStateException e) {
			log.error("Error parsing incoming message from neighboring application: {}.", e.getMessage());
		}
		log.info("Parsed message: {}", incomingMessage);
		return incomingMessage;
	}
	
	//Respond to GET request made by client
	@RequestMapping(value = "/api/resource", method = RequestMethod.GET)
	public String handleIncomingGetMessage() {
		String payload = applicationCache.getPayload();
		log.info("Received GET message from client. Responding with: {}.", payload);
		return payload;
	}
	
	@RequestMapping(value = "*")
	public String allFallback() {
		return "Nothing to see here.";
	}
}