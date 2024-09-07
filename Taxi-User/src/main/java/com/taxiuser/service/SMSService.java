package com.taxiuser.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class SMSService {

    final HttpClient httpClient;
    final ObjectMapper objectMapper;

    @Value("${sms-api.url.like-to-like}")
    String likeToLikeUrl;
    @Value("${sms-api.config.line-number}")
    String lineNumber;
    @Value("${sms-api.config.X-API-KEY}")
    String apiKey;

    public HttpResponse<String> sendSMS(String phoneNumber, String message) throws IOException, InterruptedException {
        List<String> mobiles = List.of(phoneNumber);
        Map<String, String> params = new HashMap<>();
        params.put("lineNumber", lineNumber);
        params.put("MessageText", message);
        params.put("Mobiles", objectMapper.writeValueAsString(mobiles));
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(likeToLikeUrl))
                .headers("Content-Type", "application/json")
                .headers("Accept", "text/plain")
                .headers("X-API-KEY", apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(params)))
                .build();
        return httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
    }

}
