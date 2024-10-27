package de.nopro200;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;

public class HuggingFaceImage {
    private static final String API_URL = "https://api-inference.huggingface.co/models/";
    private final OkHttpClient client;
    private final String model;
    private final String apiKey;
    private final ObjectMapper objectMapper;

    private HuggingFaceImage(String model, String apiKey) {
        this.model = model;
        this.apiKey = apiKey;
        this.client = new OkHttpClient.Builder()
                .connectTimeout(100, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(300, java.util.concurrent.TimeUnit.SECONDS)
                .build();
        this.objectMapper = new ObjectMapper();
    }

    // Method to generate an image and return as byte[]
    public byte[] image(String prompt) throws IOException {
        String url = API_URL + model;

        Map<String, Object> payload = new HashMap<>();
        payload.put("model", model);
        payload.put("inputs", prompt);

        String requestParams = objectMapper.writeValueAsString(payload);

        RequestBody requestBody = RequestBody.create(requestParams, MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + apiKey)
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                return response.body().bytes();
            } else if (response.code() == 500) {
                throw new IOException("Internal Server Error: " + response.message());
            } else {
                throw new IOException("Unexpected response code: " + response.code());
            }
        } catch (SocketTimeoutException e) {
            throw new IOException("Request timed out", e);
        }
    }

    public static class Builder {
        private final String model;
        private final String apiKey;

        public Builder(String model, String apiKey) {
            this.model = model;
            this.apiKey = apiKey;
        }

        public HuggingFaceImage build() {
            return new HuggingFaceImage(model, apiKey);
        }
    }
}