package de.nopro200;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class HuggingFaceText {

    private static final String API_URL = "https://api-inference.huggingface.co/models/%s/v1/chat/completions";
    private final OkHttpClient client;
    private final String model;
    private final String apiKey;
    private final ObjectMapper objectMapper;

    private HuggingFaceText(String model, String apiKey) {
        this.model = model;
        this.apiKey = apiKey;
        this.client = new OkHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public String chat(List<Map<String, String>> messages, ChatOptions options) throws IOException {
        String url = String.format(API_URL, model);

        Map<String, Object> payload = new HashMap<>();
        payload.put("model", model);
        payload.put("messages", messages);

        if (options != null) {
            options.applyTo(payload);
        }

        String requestParams = objectMapper.writeValueAsString(payload);

        RequestBody requestBody = RequestBody.create(requestParams, MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + apiKey)
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                if (options != null && options.isStream()) {
                    StringBuilder fullResponse = new StringBuilder();
                    handleStreamingResponse(response, chunk -> {
                        fullResponse.append(chunk);
                    });
                    return fullResponse.toString();
                } else {
                    return extractContentFromJson(response.body().string());
                }
            } else {
                throw new IOException("Unexpected response code: " + response.code());
            }
        }
    }

    private void handleStreamingResponse(Response response, Consumer<String> chunkHandler) throws IOException {
        ResponseBody body = response.body();
        if (body == null) {
            throw new IOException("Response body is null");
        }

        try (BufferedReader reader = new BufferedReader(body.charStream())) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("data: ")) {
                    String jsonData = line.substring(6).trim();
                    if (jsonData.equals("[DONE]")) {
                        break;
                    }
                    String content = extractContentFromStreamJson(jsonData);
                    if (!content.isEmpty()) {
                        chunkHandler.accept(content);
                    }
                }
            }
        }
    }

    private String extractContentFromStreamJson(String jsonData) throws IOException {
        Map<String, Object> data = objectMapper.readValue(jsonData, new TypeReference<Map<String, Object>>() {
        });
        List<Map<String, Object>> choices = (List<Map<String, Object>>) data.get("choices");
        if (choices != null && !choices.isEmpty()) {
            Map<String, Object> choice = choices.get(0);
            Map<String, Object> delta = (Map<String, Object>) choice.get("delta");
            if (delta != null && delta.containsKey("content")) {
                return delta.get("content").toString();
            }
        }
        return "";
    }

    private String extractContentFromJson(String jsonData) throws IOException {
        Map<String, Object> data = objectMapper.readValue(jsonData, new TypeReference<Map<String, Object>>() {
        });
        List<Map<String, Object>> choices = (List<Map<String, Object>>) data.get("choices");
        if (choices != null && !choices.isEmpty()) {
            Map<String, Object> choice = choices.get(0);
            Map<String, Object> message = (Map<String, Object>) choice.get("message");
            if (message != null && message.containsKey("content")) {
                return message.get("content").toString();
            }
        }
        return "";
    }

    public static class Builder {
        private final String model;
        private final String apiKey;

        public Builder(String model, String apiKey) {
            this.model = model;
            this.apiKey = apiKey;
        }

        public HuggingFaceText build() {
            return new HuggingFaceText(model, apiKey);
        }
    }

    public static class ChatOptions {
        private Double temperature;
        private Integer maxTokens;
        private Double topP;
        private boolean stream;

        public ChatOptions setTemperature(Double temperature) {
            this.temperature = temperature;
            return this;
        }

        public ChatOptions setMaxTokens(Integer maxTokens) {
            this.maxTokens = maxTokens;
            return this;
        }

        public ChatOptions setTopP(Double topP) {
            this.topP = topP;
            return this;
        }

        public boolean isStream() {
            return stream;
        }

        public ChatOptions setStream(boolean stream) {
            this.stream = stream;
            return this;
        }

        void applyTo(Map<String, Object> payload) {
            if (temperature != null) payload.put("temperature", temperature);
            if (maxTokens != null) payload.put("max_tokens", maxTokens);
            if (topP != null) payload.put("top_p", topP);
            payload.put("stream", stream);
        }
    }
}