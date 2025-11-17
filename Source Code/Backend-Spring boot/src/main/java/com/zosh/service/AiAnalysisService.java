package com.zosh.service;



import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zosh.client.OllamaClient;
import com.zosh.DTO.AiRequest;
import com.zosh.DTO.AiResponse;
import com.zosh.service.CoinService; // your interface
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class AiAnalysisService {

    private final CoinService coinService;   // your existing CoinService
    private final ObjectMapper objectMapper;
    private final OllamaClient ollamaClient;

    public AiAnalysisService(CoinService coinService, ObjectMapper objectMapper, OllamaClient ollamaClient) {
        this.coinService = coinService;
        this.objectMapper = objectMapper;
        this.ollamaClient = ollamaClient;
    }

    public AiResponse analyzeCoin(AiRequest req) {

        AiResponse resp = new AiResponse();
        try {
            String message = (req.getMessage() == null) ? "" : req.getMessage().toLowerCase(Locale.ROOT);

            // Determine coin id (prefer explicit coin in request.marketCoin)
            String coinId = "bitcoin"; // default
            if (req.getMarketCoin() != null && req.getMarketCoin().getId() != null) {
                coinId = req.getMarketCoin().getId();
            } else {
                if (message.contains("ethereum") || message.contains("eth")) coinId = "ethereum";
                else if (message.contains("solana") || message.contains("sol")) coinId = "solana";
                else if (message.contains("dogecoin") || message.contains("doge")) coinId = "dogecoin";
                else if (message.contains("binance") || message.contains("bnb")) coinId = "binancecoin";
                else if (message.contains("btc") || message.contains("bitcoin")) coinId = "bitcoin";
            }

            int days = extractDays(message, 7);

            // Call your existing coinService to get market chart JSON
            String marketJson = coinService.getMarketChart(coinId, days);

            // Parse the CoinGecko response to extract prices (prices array: [ [ts, price], ... ])
            List<Double> prices = parsePricesFromMarketChart(marketJson, days);

            if (prices.isEmpty()) {
                resp.setOk(false);
                resp.setError("Could not extract prices from CoinGecko response");
                return resp;
            }

            // Build prompt and call Ollama
            String prompt = AiPromptBuilder.buildPrompt(req.getMessage(), coinId, prices);
            String raw = ollamaClient.generate(prompt, 600);

            // try to extract readable text from raw JSON
            String reply = extractTextFromOllamaRaw(raw);

            resp.setOk(true);
            resp.setRaw(raw);
            resp.setReply(reply);
            return resp;

        } catch (Exception e) {
            resp.setOk(false);
            resp.setError(e.getMessage());
            return resp;
        }
    }

    private int extractDays(String message, int defaultDays) {
        if (message.contains("7 days") || message.contains("last 7 days") || message.contains("7 day") || message.contains("7-day")) return 7;
        if (message.contains("3 days")) return 3;
        if (message.contains("14 days")) return 14;
        if (message.contains("30 days") || message.contains("1 month")) return 30;
        if (message.contains("24 hours") || message.contains("24 hour") || message.contains("1 day")) return 1;
        return defaultDays;
    }

    private List<Double> parsePricesFromMarketChart(String marketJson, int expectedPoints) throws Exception {
        List<Double> prices = new ArrayList<>();
        JsonNode root = objectMapper.readTree(marketJson);
        if (root.has("prices")) {
            JsonNode pricesNode = root.get("prices");
            for (JsonNode arr : pricesNode) {
                if (arr.isArray() && arr.size() >= 2) {
                    double price = arr.get(1).asDouble();
                    prices.add(price);
                }
            }
        }
        // If API returned more points than requested, keep last `expectedPoints` (most recent)
        if (prices.size() > expectedPoints) {
            int start = Math.max(0, prices.size() - expectedPoints);
            return prices.subList(start, prices.size());
        }
        return prices;
    }

    private String extractTextFromOllamaRaw(String raw) {
        // Try a few heuristics: often response has "text" or choices[0].text or content
        try {
            JsonNode node = objectMapper.readTree(raw);
            if (node.has("text")) return node.get("text").asText();
            if (node.has("content")) return node.get("content").asText();
            if (node.has("choices") && node.get("choices").isArray() && node.get("choices").size() > 0) {
                JsonNode first = node.get("choices").get(0);
                if (first.has("text")) return first.get("text").asText();
                if (first.has("message") && first.get("message").has("content")) {
                    return first.get("message").get("content").asText();
                }
            }
        } catch (Exception ignored) {}
        // fallback — return raw (frontend will show it)
        return raw;
    }
}

