package com.zoharsf.argusInterview.outgoing;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zoharsf.argusInterview.model.IncomingMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import sun.net.www.protocol.http.HttpURLConnection;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class ApplicationSyncHandler {

    @Value("${application.neighbor.ip}")
    private String neighborIp;

    private static Gson gson = new GsonBuilder().create();

    public void sendUpdateToNeighboringApplicationInstance(IncomingMessage incomingMessage) {
        log.info("Sending sync to neighboring instance: {}.", incomingMessage);
        try {
            String jsonString = gson.toJson(incomingMessage);
            log.info("Built POST message to send to neighboring instance({}): {}.", neighborIp, jsonString);

            URL url = new URL("http://" + neighborIp + ":8080/api/sync");
            URLConnection urlConnection = url.openConnection();
            HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);

            byte[] out = jsonString.getBytes(StandardCharsets.UTF_8);
            //int length = out.length;

            httpURLConnection.setFixedLengthStreamingMode(out.length);
            httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            httpURLConnection.connect();
            try (OutputStream outputStream = httpURLConnection.getOutputStream()) {
                outputStream.write(out);
            }

        } catch (IOException e) {
            log.error("Failed to send update to neighboring instance: {}.", e.getMessage());
        }
    }
}
