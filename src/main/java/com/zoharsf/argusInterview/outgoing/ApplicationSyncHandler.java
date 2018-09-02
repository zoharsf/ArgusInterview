package com.zoharsf.argusInterview.outgoing;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zoharsf.argusInterview.model.IncomingMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

@Slf4j
@Component
public class ApplicationSyncHandler {
	
	@Value("${application.neighbor.ip}")
	private String neighborIp;
	
	private static Gson gson = new GsonBuilder().create();
	
	public void sendUpdateToNeighboringApplicationInstance(IncomingMessage incomingMessage) {
		//TODO this isn't working - TCP connection is being established but POST isn't being sent
		log.info("Sending sync to neighboring instance: {}.", incomingMessage);
		try {
			String jsonString = gson.toJson(incomingMessage);
			log.info("Built POST message to send to neighboring instance: {}.", jsonString);
			
			String protocol = "http";
			String host = neighborIp;
			int port = 8080;
			String path = "/api/sync";
			URI uri = new URI(protocol, null, host, port, path, null, null);
			URL url = uri.toURL();
			
//			HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
//			httpURLConnection.setRequestMethod("POST");
//			httpURLConnection.setDoOutput(true);
//			httpURLConnection.setRequestProperty("Content-Type", "application/json");
//			httpURLConnection.connect();
			
			///////////////testing1
			
			HttpURLConnection httpURLConnection1 = (HttpURLConnection) url.openConnection();
			httpURLConnection1.setRequestMethod("POST");
			httpURLConnection1.setDoOutput(true);
			httpURLConnection1.setRequestProperty("Content-Type", "application/json");
			httpURLConnection1.connect();
			
			//DataOutputStream dataOutputStream1 = new DataOutputStream(httpURLConnection1.getOutputStream());
			//dataOutputStream1.write(jsonString.getBytes().length);
			//dataOutputStream1.write(jsonString.getBytes(), 0, jsonString.getBytes().length);
			//dataOutputStream1.writeUTF("A");
			httpURLConnection1.getOutputStream().write(jsonString.getBytes());
			httpURLConnection1.getOutputStream().flush();
			httpURLConnection1.getOutputStream().close();
			
			////////////////////////
			
			/////testing 2
//			HttpURLConnection httpURLConnection2 = (HttpURLConnection) url.openConnection();
//			httpURLConnection2.setRequestMethod("POST");
//			httpURLConnection2.setDoOutput(true);
//			httpURLConnection2.setRequestProperty("Content-Type", "application/json");
//			httpURLConnection2.connect();
//
//			byte[] postData = jsonString.getBytes(StandardCharsets.UTF_8);
//			try (DataOutputStream dataOutputStream2 = new DataOutputStream(httpURLConnection2.getOutputStream())) {
//				dataOutputStream2.write(postData);
//				dataOutputStream2.flush();
//			}
			
			//////
			
			///testing 3
			
//			conn.getOutputStream().write(postDataBytes);
			
			///////////
			
//			DataOutputStream dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream());
//			//dataOutputStream.writeBytes(jsonString);
//			dataOutputStream.write(jsonString.getBytes());
//			dataOutputStream.flush();
//			dataOutputStream.close();
		}
		catch (IOException | URISyntaxException e) {
			log.error("Failed to send update to neighboring instance: {}.", e.getMessage());
		}
	}
}
