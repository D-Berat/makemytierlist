package com.example.sae_tierlist.api;

import com.example.sae_tierlist.api.pojo.Result;
import com.example.sae_tierlist.api.pojo.ResultGame;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class APIManager {

    private static final String API_KEY = System.getenv("RAWG_API_KEY");

    public Result[] rechercher(String motCle) throws ExceptImageNotFound {
        if (API_KEY == null || API_KEY.isBlank()) { throw new ExceptImageNotFound(); }
        String motCleEncode = "";
        try {
            motCleEncode = URLEncoder.encode(motCle, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.rawg.io/api/games"
                        + "?key=" + API_KEY
                        + "&search=" + motCleEncode
                        + "&page_size=10"))
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = null;
        try {
            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException | IOException e) {
            Thread.currentThread().interrupt();
            throw new ExceptImageNotFound();
        }

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        try {
            ResultGame result = objectMapper.readValue(response.body(), ResultGame.class);
            if (result.getResults() == null || result.getResults().length == 0) {
                throw new ExceptImageNotFound();
            }
            return result.getResults();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new ExceptImageNotFound();
        }
    }


}