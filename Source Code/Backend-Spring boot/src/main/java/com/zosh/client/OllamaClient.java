//package com.zosh.client;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.node.ObjectNode;
//import jakarta.annotation.PostConstruct;
//import okhttp3.*;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//
//
//import java.io.IOException;
//import java.time.Duration;
//
//@Component
//public class OllamaClient {
//    private final ObjectMapper mapper = new ObjectMapper();
//
//    @Value("${ollama.url}")
//    private String ollamaBaseUrl;
//
//    @Value("${ollama.model}")
//    private String model;
//
//    @Value("${ollama.timeout:300000}") // 5 minutes default
//    private int timeoutMs;
//
//    private OkHttpClient client;
//
//    // Initialize client AFTER Spring injects properties
//    @PostConstruct
//    public void init() {
//        this.client = new OkHttpClient.Builder()
//                .callTimeout(Duration.ofMillis(timeoutMs))
//                .connectTimeout(Duration.ofMillis(30000))  // 30 seconds to connect
//                .readTimeout(Duration.ofMillis(timeoutMs)) // Long read for streaming
//                .writeTimeout(Duration.ofMillis(30000))    // 30 seconds to write
//                .build();
//    }
//
//    public String generate(String prompt, int maxTokens) throws IOException {
//        String url = ollamaBaseUrl + "/api/generate";
//
//        // Build request with correct Ollama parameters
//        ObjectNode requestNode = mapper.createObjectNode();
//        requestNode.put("model", model);
//        requestNode.put("prompt", prompt);
//        requestNode.put("stream", true); // Enable streaming
//
//        // Add options for token limit
//        ObjectNode options = mapper.createObjectNode();
//        options.put("num_predict", maxTokens); // Ollama uses num_predict, not max_tokens
//        requestNode.set("options", options);
//
//        String json = requestNode.toString();
//
//        RequestBody body = RequestBody.create(json, MediaType.get("application/json; charset=utf-8"));
//        Request request = new Request.Builder()
//                .url(url)
//                .post(body)
//                .build();
//
//        try (Response response = client.newCall(request).execute()) {
//            if (!response.isSuccessful()) {
//                String err = response.body() != null ? response.body().string() : "";
//                throw new IOException("Ollama error: " + response.code() + " - " + err);
//            }
//
//            // Read response as streaming JSON
//            StringBuilder fullText = new StringBuilder();
//            ResponseBody responseBody = response.body();
//
//            if (responseBody == null) {
//                throw new IOException("Empty response body");
//            }
//
//            try (okio.BufferedSource source = responseBody.source()) {
//                while (!source.exhausted()) {
//                    String line = source.readUtf8Line();
//                    if (line == null || line.trim().isEmpty()) continue;
//
//                    JsonNode node = mapper.readTree(line);
//                    if (node.has("response")) {
//                        fullText.append(node.get("response").asText());
//                    }
//                    if (node.has("done") && node.get("done").asBoolean()) {
//                        break;
//                    }
//                }
//            }
//
//            return fullText.toString();
//        }
//    }
//}
package com.zosh.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;


import java.io.IOException;
import java.time.Duration;
import java.util.Base64;

@Component
public class OllamaClient {
    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${ollama.url}")
    private String ollamaBaseUrl;

    @Value("${ollama.model}")
    private String model;

    @Value("${ollama.vision.model:llava:7b}")
    private String visionModel;

    @Value("${ollama.timeout:300000}")
    private int timeoutMs;

    private OkHttpClient client;

    @PostConstruct
    public void init() {
        this.client = new OkHttpClient.Builder()
                .callTimeout(Duration.ofMillis(timeoutMs))
                .connectTimeout(Duration.ofMillis(30000))
                .readTimeout(Duration.ofMillis(timeoutMs))
                .writeTimeout(Duration.ofMillis(30000))
                .build();
    }

    /**
     * Text-only generation (for coin analysis)
     */
    public String generate(String prompt, int maxTokens) throws IOException {
        String url = ollamaBaseUrl + "/api/generate";

        ObjectNode requestNode = mapper.createObjectNode();
        requestNode.put("model", model);
        requestNode.put("prompt", prompt);
        requestNode.put("stream", true);

        ObjectNode options = mapper.createObjectNode();
        options.put("num_predict", maxTokens);
        requestNode.set("options", options);

        String json = requestNode.toString();
        RequestBody body = RequestBody.create(json, MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder().url(url).post(body).build();

        return executeStreamingRequest(request);
    }

    /**
     * Vision analysis - analyze image with text prompt
     * @param prompt The question/prompt about the image
     * @param imageBase64 Base64 encoded image (without data:image prefix)
     * @return AI response analyzing the image
     */
    public String analyzeImage(String prompt, String imageBase64) throws IOException {
        String url = ollamaBaseUrl + "/api/generate";

        ObjectNode requestNode = mapper.createObjectNode();
        requestNode.put("model", visionModel);
        requestNode.put("prompt", prompt);
        requestNode.put("stream", true);

        // Add images array for vision
        ArrayNode imagesArray = mapper.createArrayNode();
        imagesArray.add(imageBase64);
        requestNode.set("images", imagesArray);

        ObjectNode options = mapper.createObjectNode();
        options.put("num_predict", 500); // Max tokens for image analysis
        requestNode.set("options", options);

        String json = requestNode.toString();
        RequestBody body = RequestBody.create(json, MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder().url(url).post(body).build();

        return executeStreamingRequest(request);
    }

    /**
     * Vision analysis with multiple images
     */
    public String analyzeMultipleImages(String prompt, String[] imagesBase64) throws IOException {
        String url = ollamaBaseUrl + "/api/generate";

        ObjectNode requestNode = mapper.createObjectNode();
        requestNode.put("model", visionModel);
        requestNode.put("prompt", prompt);
        requestNode.put("stream", true);

        ArrayNode imagesArray = mapper.createArrayNode();
        for (String imageBase64 : imagesBase64) {
            imagesArray.add(imageBase64);
        }
        requestNode.set("images", imagesArray);

        ObjectNode options = mapper.createObjectNode();
        options.put("num_predict", 500);
        requestNode.set("options", options);

        String json = requestNode.toString();
        RequestBody body = RequestBody.create(json, MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder().url(url).post(body).build();

        return executeStreamingRequest(request);
    }

    /**
     * Helper method to execute streaming requests
     */
    private String executeStreamingRequest(Request request) throws IOException {
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String err = response.body() != null ? response.body().string() : "";
                throw new IOException("Ollama error: " + response.code() + " - " + err);
            }

            StringBuilder fullText = new StringBuilder();
            ResponseBody responseBody = response.body();

            if (responseBody == null) {
                throw new IOException("Empty response body");
            }

            try (okio.BufferedSource source = responseBody.source()) {
                while (!source.exhausted()) {
                    String line = source.readUtf8Line();
                    if (line == null || line.trim().isEmpty()) continue;

                    JsonNode node = mapper.readTree(line);
                    if (node.has("response")) {
                        fullText.append(node.get("response").asText());
                    }
                    if (node.has("done") && node.get("done").asBoolean()) {
                        break;
                    }
                }
            }

            return fullText.toString();
        }
    }

    /**
     * Utility method to convert byte array to base64 (for image uploads)
     */
    public static String bytesToBase64(byte[] imageBytes) {
        return Base64.getEncoder().encodeToString(imageBytes);
    }
}