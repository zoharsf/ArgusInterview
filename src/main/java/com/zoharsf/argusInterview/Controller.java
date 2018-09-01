package com.zoharsf.argusInterview;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.zoharsf.argusInterview.model.IncomingMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.httpclient.util.HttpURLConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

@Slf4j
@RestController
public class Controller {

    @Autowired
    private
    ApplicationCache applicationCache;

    @Value("${application.neighbor.ip}")
    private String neighborIp;

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
            sendUpdateToNeighboringApplicationInstance(incomingMessage);
        }
    }

    private void sendUpdateToNeighboringApplicationInstance(IncomingMessage incomingMessage) {
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

            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            httpURLConnection.connect();

            ///////////////testing

            DataOutputStream dataOutputStream2 = new DataOutputStream(httpURLConnection.getOutputStream());
            //dataOutputStream2.write(jsonString.getBytes().length);
            //dataOutputStream2.write(jsonString.getBytes(), 0, jsonString.getBytes().length);
            dataOutputStream2.writeUTF("A");
            dataOutputStream2.flush();
            dataOutputStream2.close();

            ////////////////////////

            DataOutputStream dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream());
            //dataOutputStream.writeBytes(jsonString);
            dataOutputStream.write(jsonString.getBytes());
            dataOutputStream.flush();
            dataOutputStream.close();
        } catch (IOException | URISyntaxException e) {
            log.error("Failed to send update to neighboring instance: {}.", e.getMessage());
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
        } catch (JsonSyntaxException | IllegalStateException e) {
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