//package com.zosh.service;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.jayway.jsonpath.JsonPath;
//import com.jayway.jsonpath.ReadContext;
//import com.zosh.model.CoinDTO;
//import com.zosh.response.ApiResponse;
//import com.zosh.response.FunctionResponse;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.*;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.HttpClientErrorException;
//import org.springframework.web.client.RestTemplate;
//
//import java.util.*;
//
///**
// * Rewritten ChatBotServiceImpl with:
// *  - Safe JSON construction using ObjectMapper
// *  - Correct Gemini "function_declarations" key
// *  - Robust parsing & error handling
// *  - Uses Jackson to serialize CoinDTO (so we don't rely on CoinDTO.toString())
// */
//@Service
//public class ChatBotServiceImpl implements ChatBotService {
//
//    private static final Logger logger = LoggerFactory.getLogger(ChatBotServiceImpl.class);
//
//    @Value("${gemini.api.key}")
//    private String API_KEY;
//
//    private final ObjectMapper mapper = new ObjectMapper();
//
//    private double convertToDoubleSafe(Object value) {
//        if (value == null) return 0d;
//        if (value instanceof Number) return ((Number) value).doubleValue();
//        // try parse if it's a string of a number
//        try {
//            return Double.parseDouble(value.toString());
//        } catch (NumberFormatException ex) {
//            logger.warn("Unable to parse number from value: {}", value);
//            return 0d;
//        }
//    }
//
//    private Integer convertToIntegerSafe(Object value) {
//        if (value == null) return 0;
//        if (value instanceof Number) return ((Number) value).intValue();
//        try {
//            return Integer.parseInt(value.toString());
//        } catch (NumberFormatException ex) {
//            logger.warn("Unable to parse int from value: {}", value);
//            return 0;
//        }
//    }
//
//    /**
//     * Fetches single coin details from CoinGecko and maps to CoinDTO.
//     * Returns null if not found or error occurs.
//     */
//    public CoinDTO makeApiRequest(String currencyName) {
//        if (currencyName == null || currencyName.trim().isEmpty()) {
//            logger.warn("makeApiRequest called with empty currencyName");
//            return null;
//        }
//
//        String id = currencyName.trim().toLowerCase();
//        String url = "https://api.coingecko.com/api/v3/coins/" + id;
//
//        RestTemplate restTemplate = new RestTemplate();
//
//        try {
//            ResponseEntity<Map> responseEntity = restTemplate.getForEntity(url, Map.class);
//            Map<String, Object> responseBody = responseEntity.getBody();
//            if (responseBody == null) {
//                logger.warn("CoinGecko returned null body for {}", id);
//                return null;
//            }
//
//            CoinDTO coinInfo = new CoinDTO();
//
//            // Basic fields
//            coinInfo.setId((String) responseBody.get("id"));
//            coinInfo.setSymbol((String) responseBody.get("symbol"));
//            coinInfo.setName((String) responseBody.get("name"));
//
//            // image.large is nested
//            Object imageObj = responseBody.get("image");
//            if (imageObj instanceof Map) {
//                Object large = ((Map<?, ?>) imageObj).get("large");
//                if (large != null) coinInfo.setImage(large.toString());
//            }
//
//            // market_data nested map
//            Object marketDataObj = responseBody.get("market_data");
//            if (marketDataObj instanceof Map) {
//                Map<String, Object> marketData = (Map<String, Object>) marketDataObj;
//
//                Object currentPriceObj = ((Map<?, ?>) marketData.getOrDefault("current_price", Collections.emptyMap())).get("usd");
//                coinInfo.setCurrentPrice(convertToDoubleSafe(currentPriceObj));
//
//                Object marketCapObj = ((Map<?, ?>) marketData.getOrDefault("market_cap", Collections.emptyMap())).get("usd");
//                coinInfo.setMarketCap(convertToDoubleSafe(marketCapObj));
//
//                coinInfo.setMarketCapRank(convertToIntegerSafe(responseBody.get("market_cap_rank")));
//
//                Object totalVolumeObj = ((Map<?, ?>) marketData.getOrDefault("total_volume", Collections.emptyMap())).get("usd");
//                coinInfo.setTotalVolume(convertToDoubleSafe(totalVolumeObj));
//
//                Object high24Obj = ((Map<?, ?>) marketData.getOrDefault("high_24h", Collections.emptyMap())).get("usd");
//                coinInfo.setHigh24h(convertToDoubleSafe(high24Obj));
//
//                Object low24Obj = ((Map<?, ?>) marketData.getOrDefault("low_24h", Collections.emptyMap())).get("usd");
//                coinInfo.setLow24h(convertToDoubleSafe(low24Obj));
//
//                coinInfo.setPriceChange24h(convertToDoubleSafe(marketData.get("price_change_24h")));
//                coinInfo.setPriceChangePercentage24h(convertToDoubleSafe(marketData.get("price_change_percentage_24h")));
//                coinInfo.setMarketCapChange24h(convertToDoubleSafe(marketData.get("market_cap_change_24h")));
//                coinInfo.setMarketCapChangePercentage24h(convertToDoubleSafe(marketData.get("market_cap_change_percentage_24h")));
//
//                // supply values may be in marketData or top-level - attempt both
//                coinInfo.setCirculatingSupply(convertToDoubleSafe(marketData.get("circulating_supply")));
//                coinInfo.setTotalSupply(convertToDoubleSafe(marketData.get("total_supply")));
//
//                // ATH/ATL
//                coinInfo.setAth((long) convertToDoubleSafe(marketData.get("ath")));
//                coinInfo.setAthChangePercentage((long) convertToDoubleSafe(marketData.get("ath_change_percentage")));
//                coinInfo.setAtl((long) convertToDoubleSafe(marketData.get("atl")));
//                coinInfo.setAtlChangePercentage((long) convertToDoubleSafe(marketData.get("atl_change_percentage")));
//
//                // last_updated - try market_data.last_updated then top-level last_updated
//                Object lastUpdated = marketData.get("last_updated");
//                if (lastUpdated == null) lastUpdated = responseBody.get("last_updated");
//                if (lastUpdated != null) {
//                    try {
//                        // set as Date via parsing if possible - keep as Date via ObjectMapper conversion
//                        Date d = mapper.convertValue(lastUpdated, Date.class);
//                        coinInfo.setLastUpdated(d);
//                    } catch (IllegalArgumentException ignored) {
//                    }
//                }
//            } else {
//                logger.warn("market_data not present or unexpected type for {}", id);
//            }
//
//            return coinInfo;
//        } catch (HttpClientErrorException.NotFound nf) {
//            logger.warn("Coin not found on CoinGecko: {}", id);
//        } catch (HttpClientErrorException httpEx) {
//            logger.error("HTTP error from CoinGecko for {} : status={} body={}", id, httpEx.getStatusCode(), httpEx.getResponseBodyAsString());
//        } catch (Exception ex) {
//            logger.error("Unexpected error calling CoinGecko for {}: {}", id, ex.getMessage(), ex);
//        }
//
//        return null;
//    }
//
//    /**
//     * Calls the Gemini model once with a function_declarations list to obtain a function call.
//     * Parses and returns the function call arguments (currencyName and currencyData) inside FunctionResponse.
//     */
//    public FunctionResponse getFunctionResponse(String prompt) {
//        String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + API_KEY;
//
//        // Build request body as Map -> JSON (avoids string concatenation)
//        Map<String, Object> body = new HashMap<>();
//
//        // contents: user prompt
//        Map<String, Object> userContent = new HashMap<>();
//        userContent.put("parts", Collections.singletonList(Collections.singletonMap("text", prompt)));
//        body.put("contents", Collections.singletonList(userContent));
//
//        // build function_declarations properly
//        Map<String, Object> functionDecl = new HashMap<>();
//        functionDecl.put("name", "getCoinDetails");
//        functionDecl.put("description", "Get the coin details from given currency object");
//        Map<String, Object> params = new HashMap<>();
//        params.put("type", "OBJECT");
//        Map<String, Object> props = new HashMap<>();
//        Map<String, Object> currencyNameProp = new HashMap<>();
//        currencyNameProp.put("type", "STRING");
//        currencyNameProp.put("description", "The currency name, id, symbol.");
//        props.put("currencyName", currencyNameProp);
//        Map<String, Object> currencyDataProp = new HashMap<>();
//        currencyDataProp.put("type", "STRING");
//        currencyDataProp.put("description", "Currency Data JSON as string.");
//        props.put("currencyData", currencyDataProp);
//        params.put("properties", props);
//        params.put("required", Arrays.asList("currencyName", "currencyData"));
//        functionDecl.put("parameters", params);
//
//        Map<String, Object> tool = new HashMap<>();
//        tool.put("function_declarations", Collections.singletonList(functionDecl));
//        body.put("tools", Collections.singletonList(tool));
//
//        String requestJson;
//        try {
//            requestJson = mapper.writeValueAsString(body);
//        } catch (JsonProcessingException e) {
//            logger.error("Failed to serialize getFunctionResponse body", e);
//            return null;
//        }
//
//        RestTemplate restTemplate = new RestTemplate();
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        HttpEntity<String> requestEntity = new HttpEntity<>(requestJson, headers);
//
//        try {
//            ResponseEntity<String> response = restTemplate.postForEntity(GEMINI_API_URL, requestEntity, String.class);
//            String responseBody = response.getBody();
//            if (responseBody == null) {
//                logger.warn("Empty response from Gemini for getFunctionResponse");
//                return null;
//            }
//
//            // parse safely using JsonPath
//            ReadContext ctx = JsonPath.parse(responseBody);
//            FunctionResponse fr = new FunctionResponse();
//
//            try {
//                String functionName = ctx.read("$.candidates[0].content.parts[0].functionCall.name");
//                fr.setFunctionName(functionName);
//            } catch (Exception ignored) {
//            }
//
//            String currencyName = null;
//            String currencyData = null;
//            try {
//                // args may be an object or a JSON string
//                Object argsObj = ctx.read("$.candidates[0].content.parts[0].functionCall.args");
//                if (argsObj != null) {
//                    // if argsObj is a Map-like, read fields; if it's a String, try to parse it
//                    if (argsObj instanceof Map) {
//                        Map<?, ?> argsMap = (Map<?, ?>) argsObj;
//                        Object cn = argsMap.get("currencyName");
//                        if (cn != null) currencyName = cn.toString();
//                        Object cd = argsMap.get("currencyData");
//                        if (cd != null) currencyData = cd.toString();
//                    } else {
//                        // argsObj could be a JSON String; convert to Map
//                        String argsJson = argsObj.toString();
//                        try {
//                            Map<?, ?> argsMap = mapper.readValue(argsJson, Map.class);
//                            Object cn = argsMap.get("currencyName");
//                            if (cn != null) currencyName = cn.toString();
//                            Object cd = argsMap.get("currencyData");
//                            if (cd != null) currencyData = cd.toString();
//                        } catch (Exception ex) {
//                            // fallback - try JsonPath specifically
//                            try {
//                                currencyName = ctx.read("$.candidates[0].content.parts[0].functionCall.args.currencyName");
//                            } catch (Exception e) { }
//                            try {
//                                currencyData = ctx.read("$.candidates[0].content.parts[0].functionCall.args.currencyData");
//                            } catch (Exception e) { }
//                        }
//                    }
//                }
//            } catch (Exception e) {
//                logger.warn("No functionCall.args present in Gemini response");
//            }
//
//            fr.setCurrencyName(currencyName);
//            fr.setCurrencyData(currencyData);
//
//            logger.info("getFunctionResponse -> functionName: {}, currencyName: {}", fr.getFunctionName(), fr.getCurrencyName());
//            return fr;
//
//        } catch (HttpClientErrorException.NotFound nf) {
//            logger.warn("Gemini model not found or unsupported for this API version: {}", nf.getResponseBodyAsString());
//        } catch (HttpClientErrorException he) {
//            logger.error("HTTP error calling Gemini in getFunctionResponse: status={}, body={}", he.getStatusCode(), he.getResponseBodyAsString());
//        } catch (Exception ex) {
//            logger.error("Unexpected error in getFunctionResponse: {}", ex.getMessage(), ex);
//        }
//
//        return null;
//    }
//
//    /**
//     * Main flow: call Gemini to get function args -> call CoinGecko -> send coin JSON back to Gemini as function response -> return final text
//     */
//    @Override
//    public ApiResponse getCoinDetails(String prompt) {
//        ApiResponse apiResponse = new ApiResponse();
//
//        FunctionResponse functionResponse = getFunctionResponse(prompt);
//        if (functionResponse == null || functionResponse.getCurrencyName() == null) {
//            apiResponse.setMessage("Could not determine currency name from model. Raw prompt was used.");
//            return apiResponse;
//        }
//
//        // 1) fetch coin data
//        CoinDTO coinDto = makeApiRequest(functionResponse.getCurrencyName());
//        if (coinDto == null) {
//            apiResponse.setMessage("Coin not found: " + functionResponse.getCurrencyName());
//            return apiResponse;
//        }
//
//        // 2) serialize coinDto as JSON (use Jackson; this ignores the DTO.toString)
//        String coinJson;
//        try {
//            coinJson = mapper.writeValueAsString(coinDto);
//        } catch (JsonProcessingException e) {
//            logger.error("Failed to serialize CoinDTO", e);
//            coinJson = "{}";
//        }
//
//        // 3) Build the conversation: user -> model functionCall -> function response (with coinJson)
//        String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + API_KEY;
//        Map<String, Object> body = new LinkedHashMap<>();
//
//        // user content
//        Map<String, Object> userContent = new HashMap<>();
//        userContent.put("role", "user");
//        userContent.put("parts", Collections.singletonList(Collections.singletonMap("text", prompt)));
//
//        // model content with functionCall (simulate the model asking for getCoinDetails)
//        Map<String, Object> functionCallPart = new HashMap<>();
//        Map<String, Object> functionCall = new HashMap<>();
//        functionCall.put("name", "getCoinDetails");
//        Map<String, Object> argsMap = new HashMap<>();
//        argsMap.put("currencyName", functionResponse.getCurrencyName());
//        argsMap.put("currencyData", functionResponse.getCurrencyData() != null ? functionResponse.getCurrencyData() : "");
//        try {
//            functionCall.put("args", mapper.writeValueAsString(argsMap)); // Gemini may accept stringified args
//        } catch (JsonProcessingException e) {
//            functionCall.put("args", argsMap);
//        }
//        functionCallPart.put("functionCall", functionCall);
//        Map<String, Object> modelContent = new HashMap<>();
//        modelContent.put("role", "model");
//        modelContent.put("parts", Collections.singletonList(functionCallPart));
//
//        // function content - here we provide the function's response (the coin JSON)
//        Map<String, Object> functionResponseContent = new HashMap<>();
//        Map<String, Object> functionResponseWrapper = new HashMap<>();
//        functionResponseWrapper.put("name", "getCoinDetails");
//
//        Map<String, Object> innerResponse = new HashMap<>();
//        innerResponse.put("name", "getCoinDetails");
//        // insert the coin JSON as a JSON object, not as CoinDTO.toString()
//        try {
//            // coinJson is valid JSON string; convert to Map so it becomes a real JSON object in the final request
//            Object coinAsObj = mapper.readValue(coinJson, Object.class);
//            innerResponse.put("content", coinAsObj);
//        } catch (Exception e) {
//            innerResponse.put("content", coinJson);
//        }
//
//        functionResponseWrapper.put("response", innerResponse);
//        functionResponseContent.put("role", "function");
//        functionResponseContent.put("parts", Collections.singletonList(Collections.singletonMap("functionResponse", functionResponseWrapper)));
//
//        // assemble contents
//        body.put("contents", Arrays.asList(userContent, modelContent, functionResponseContent));
//
//        // include tools with proper function_declarations (so model knows the function signature)
//        Map<String, Object> functionDecl = new HashMap<>();
//        functionDecl.put("name", "getCoinDetails");
//        functionDecl.put("description", "Get crypto currency data from given currency object.");
//        Map<String, Object> params = new HashMap<>();
//        params.put("type", "OBJECT");
//        Map<String, Object> props = new HashMap<>();
//        Map<String, Object> cnProp = new HashMap<>();
//        cnProp.put("type", "STRING");
//        cnProp.put("description", "The currency Name, id, symbol.");
//        props.put("currencyName", cnProp);
//        Map<String, Object> cdProp = new HashMap<>();
//        cdProp.put("type", "STRING");
//        cdProp.put("description", "The currency data id, symbol, current price, image, market cap etc.");
//        props.put("currencyData", cdProp);
//        params.put("properties", props);
//        params.put("required", Arrays.asList("currencyName", "currencyData"));
//        functionDecl.put("parameters", params);
//
//        Map<String, Object> tool = new HashMap<>();
//        tool.put("function_declarations", Collections.singletonList(functionDecl));
//        body.put("tools", Collections.singletonList(tool));
//
//        // Serialize final request
//        String requestJson;
//        try {
//            requestJson = mapper.writeValueAsString(body);
//        } catch (JsonProcessingException e) {
//            logger.error("Failed to serialize final Gemini request", e);
//            apiResponse.setMessage("Internal error preparing request to model");
//            return apiResponse;
//        }
//
//        // call Gemini for final text
//        RestTemplate restTemplate = new RestTemplate();
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        HttpEntity<String> requestEntity = new HttpEntity<>(requestJson, headers);
//
//        try {
//            ResponseEntity<String> response = restTemplate.postForEntity(GEMINI_API_URL, requestEntity, String.class);
//            String responseBody = response.getBody();
//            if (responseBody == null) {
//                apiResponse.setMessage("Empty response from model");
//                return apiResponse;
//            }
//
//            // safe read: try to get model final text (may be in candidates[].content.parts[].text)
//            ReadContext ctx = JsonPath.parse(responseBody);
//            String finalText = null;
//            try {
//                finalText = ctx.read("$.candidates[0].content.parts[0].text");
//            } catch (Exception ignored) {
//            }
//            if (finalText == null) {
//                // fallback: return the function response we provided (coinJson) or entire body
//                apiResponse.setMessage("Model did not provide final text. Returning coin JSON.");
//                apiResponse.setMessage(coinJson);
//                apiResponse.setStatus(true);
//                return apiResponse;
//            } else {
//                apiResponse.setMessage(finalText);
//                return apiResponse;
//            }
//
//        } catch (HttpClientErrorException.NotFound nf) {
//            logger.warn("Gemini model not found or unsupported for this API version: {}", nf.getResponseBodyAsString());
//            apiResponse.setMessage("Model not found: " + nf.getMessage());
//        } catch (HttpClientErrorException he) {
//            logger.error("HTTP error calling Gemini in getCoinDetails: status={}, body={}", he.getStatusCode(), he.getResponseBodyAsString());
//            apiResponse.setMessage("Model HTTP error: " + he.getStatusText());
//        } catch (Exception ex) {
//            logger.error("Unexpected error calling Gemini in getCoinDetails: {}", ex.getMessage(), ex);
//            apiResponse.setMessage("Unexpected error: " + ex.getMessage());
//        }
//
//        return apiResponse;
//    }
//
//    @Override
//    public CoinDTO getCoinByName(String coinName) {
//        return this.makeApiRequest(coinName);
//    }
//
//    @Override
//    public String simpleChat(String prompt) {
//        String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + API_KEY;
//
//
//        Map<String, Object> body = new HashMap<>();
//        Map<String, Object> userContent = new HashMap<>();
//        userContent.put("parts", Collections.singletonList(Collections.singletonMap("text", prompt)));
//        body.put("contents", Collections.singletonList(userContent));
//
//        String requestJson;
//        try {
//            requestJson = mapper.writeValueAsString(body);
//        } catch (JsonProcessingException e) {
//            logger.error("Failed to serialize simpleChat body", e);
//            return "Internal error";
//        }
//
//        RestTemplate restTemplate = new RestTemplate();
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        HttpEntity<String> requestEntity = new HttpEntity<>(requestJson, headers);
//
//        try {
//            ResponseEntity<String> response = restTemplate.postForEntity(GEMINI_API_URL, requestEntity, String.class);
//            String responseBody = response.getBody();
//            if (responseBody == null) return "";
//
//            // try to extract the model text
//            ReadContext ctx = JsonPath.parse(responseBody);
//            try {
//                return ctx.read("$.candidates[0].content.parts[0].text");
//            } catch (Exception e) {
//                // fallback to returning raw response
//                return responseBody;
//            }
//
//        } catch (HttpClientErrorException.NotFound nf) {
//            logger.warn("Gemini model not found: {}", nf.getResponseBodyAsString());
//            return "Model not found";
//        } catch (HttpClientErrorException he) {
//            logger.error("HTTP error calling Gemini in simpleChat: status={}, body={}", he.getStatusCode(), he.getResponseBodyAsString());
//            return "Model HTTP error";
//        } catch (Exception ex) {
//            logger.error("Unexpected error calling Gemini in simpleChat: {}", ex.getMessage(), ex);
//            return "Unexpected error";
//        }
//    }
//}

package com.zosh.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;
import com.zosh.model.CoinDTO;
import com.zosh.response.ApiResponse;
import com.zosh.response.FunctionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * ChatBotServiceImpl (Option A - model may decide to call function)
 * - Uses gemini-2.5-pro on v1beta endpoint
 * - Sends function_declarations and lets model call function if needed
 * - If model calls function: fetch coin from CoinGecko and return final model text
 * - If model doesn't call function: return model text directly
 *
 * Minimal & backward-compatible with your CoinDTO and ApiResponse classes.
 */
@Service
public class ChatBotServiceImpl implements ChatBotService {

    private static final Logger logger = LoggerFactory.getLogger(ChatBotServiceImpl.class);

    @Value("${gemini.api.key}")
    private String API_KEY;

    // Use v1beta models path (your project lists gemini-2.5-pro under v1beta)
    private static final String GEMINI_BASE = "https://generativelanguage.googleapis.com/v1beta/models/";
    private static final String MODEL_NAME = "gemini-2.5-pro";
    private final ObjectMapper mapper = new ObjectMapper();

    private double convertToDoubleSafe(Object value) {
        if (value == null) return 0d;
        if (value instanceof Number) return ((Number) value).doubleValue();
        try {
            return Double.parseDouble(value.toString());
        } catch (NumberFormatException ex) {
            logger.warn("Unable to parse number from value: {}", value);
            return 0d;
        }
    }

    private Integer convertToIntegerSafe(Object value) {
        if (value == null) return 0;
        if (value instanceof Number) return ((Number) value).intValue();
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException ex) {
            logger.warn("Unable to parse int from value: {}", value);
            return 0;
        }
    }

    /**
     * Fetch coin data from CoinGecko and map to CoinDTO.
     */
    public CoinDTO makeApiRequest(String currencyName) {
        if (currencyName == null || currencyName.trim().isEmpty()) {
            logger.warn("makeApiRequest called with empty currencyName");
            return null;
        }

        String id = currencyName.trim().toLowerCase();
        String url = "https://api.coingecko.com/api/v3/coins/" + id;

        RestTemplate restTemplate = new RestTemplate();

        try {
            ResponseEntity<Map> responseEntity = restTemplate.getForEntity(url, Map.class);
            Map<String, Object> responseBody = responseEntity.getBody();
            if (responseBody == null) {
                logger.warn("CoinGecko returned null body for {}", id);
                return null;
            }

            CoinDTO coinInfo = new CoinDTO();

            coinInfo.setId((String) responseBody.get("id"));
            coinInfo.setSymbol((String) responseBody.get("symbol"));
            coinInfo.setName((String) responseBody.get("name"));

            Object imageObj = responseBody.get("image");
            if (imageObj instanceof Map) {
                Object large = ((Map<?, ?>) imageObj).get("large");
                if (large != null) coinInfo.setImage(large.toString());
            }

            Object marketDataObj = responseBody.get("market_data");
            if (marketDataObj instanceof Map) {
                Map<String, Object> marketData = (Map<String, Object>) marketDataObj;

                Object currentPriceObj = ((Map<?, ?>) marketData.getOrDefault("current_price", Collections.emptyMap())).get("usd");
                coinInfo.setCurrentPrice(convertToDoubleSafe(currentPriceObj));

                Object marketCapObj = ((Map<?, ?>) marketData.getOrDefault("market_cap", Collections.emptyMap())).get("usd");
                coinInfo.setMarketCap(convertToDoubleSafe(marketCapObj));

                coinInfo.setMarketCapRank(convertToIntegerSafe(responseBody.get("market_cap_rank")));

                Object totalVolumeObj = ((Map<?, ?>) marketData.getOrDefault("total_volume", Collections.emptyMap())).get("usd");
                coinInfo.setTotalVolume(convertToDoubleSafe(totalVolumeObj));

                Object high24Obj = ((Map<?, ?>) marketData.getOrDefault("high_24h", Collections.emptyMap())).get("usd");
                coinInfo.setHigh24h(convertToDoubleSafe(high24Obj));

                Object low24Obj = ((Map<?, ?>) marketData.getOrDefault("low_24h", Collections.emptyMap())).get("usd");
                coinInfo.setLow24h(convertToDoubleSafe(low24Obj));

                coinInfo.setPriceChange24h(convertToDoubleSafe(marketData.get("price_change_24h")));
                coinInfo.setPriceChangePercentage24h(convertToDoubleSafe(marketData.get("price_change_percentage_24h")));
                coinInfo.setMarketCapChange24h(convertToDoubleSafe(marketData.get("market_cap_change_24h")));
                coinInfo.setMarketCapChangePercentage24h(convertToDoubleSafe(marketData.get("market_cap_change_percentage_24h")));

                coinInfo.setCirculatingSupply(convertToDoubleSafe(marketData.get("circulating_supply")));
                coinInfo.setTotalSupply(convertToDoubleSafe(marketData.get("total_supply")));

                coinInfo.setAth((long) convertToDoubleSafe(marketData.get("ath")));
                coinInfo.setAthChangePercentage((long) convertToDoubleSafe(marketData.get("ath_change_percentage")));
                coinInfo.setAtl((long) convertToDoubleSafe(marketData.get("atl")));
                coinInfo.setAtlChangePercentage((long) convertToDoubleSafe(marketData.get("atl_change_percentage")));

                Object lastUpdated = marketData.get("last_updated");
                if (lastUpdated == null) lastUpdated = responseBody.get("last_updated");
                if (lastUpdated != null) {
                    try {
                        Date d = mapper.convertValue(lastUpdated, Date.class);
                        coinInfo.setLastUpdated(d);
                    } catch (IllegalArgumentException ignored) {
                    }
                }
            } else {
                logger.warn("market_data not present or unexpected type for {}", id);
            }

            return coinInfo;
        } catch (HttpClientErrorException.NotFound nf) {
            logger.warn("Coin not found on CoinGecko: {}", id);
        } catch (HttpClientErrorException httpEx) {
            logger.error("HTTP error from CoinGecko for {} : status={} body={}", id, httpEx.getStatusCode(), httpEx.getResponseBodyAsString());
        } catch (Exception ex) {
            logger.error("Unexpected error calling CoinGecko for {}: {}", id, ex.getMessage(), ex);
        }

        return null;
    }

    /**
     * Send a single request to Gemini asking it to use the function (function_declarations provided).
     * If Gemini returns a function_call, return parsed FunctionResponse with currencyName populated.
     * If Gemini returns plain text (no function_call), the returned FunctionResponse will contain currencyData=finalText and currencyName=null.
     */
    public FunctionResponse getFunctionResponse(String prompt) {
        String GEMINI_API_URL = GEMINI_BASE + MODEL_NAME + ":generateContent?key=" + API_KEY;

        Map<String, Object> body = new LinkedHashMap<>();

        // contents: single user prompt
        Map<String, Object> userContent = new HashMap<>();
        userContent.put("parts", Collections.singletonList(Collections.singletonMap("text", prompt)));
        body.put("contents", Collections.singletonList(userContent));

        // declare the function that model may call
        Map<String, Object> functionDecl = new HashMap<>();
        functionDecl.put("name", "getCoinDetails");
        functionDecl.put("description", "Get the coin details from given currency object (currencyName).");
        Map<String, Object> params = new HashMap<>();
        params.put("type", "OBJECT");
        Map<String, Object> props = new HashMap<>();
        Map<String, Object> cnProp = new HashMap<>();
        cnProp.put("type", "STRING");
        cnProp.put("description", "currency name/id/symbol");
        props.put("currencyName", cnProp);
        Map<String, Object> cdProp = new HashMap<>();
        cdProp.put("type", "STRING");
        cdProp.put("description", "optional prepopulated currencyData");
        props.put("currencyData", cdProp);
        params.put("properties", props);
        params.put("required", Collections.singletonList("currencyName"));
        functionDecl.put("parameters", params);

        Map<String, Object> tool = new HashMap<>();
        tool.put("function_declarations", Collections.singletonList(functionDecl));
        body.put("tools", Collections.singletonList(tool));

        String requestJson;
        try {
            requestJson = mapper.writeValueAsString(body);
        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize getFunctionResponse body", e);
            return null;
        }

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(requestJson, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(GEMINI_API_URL, requestEntity, String.class);
            String responseBody = response.getBody();
            if (responseBody == null) {
                logger.warn("Empty response from Gemini for getFunctionResponse");
                return null;
            }

            ReadContext ctx = JsonPath.parse(responseBody);
            FunctionResponse fr = new FunctionResponse();

            // Try to find function_call (support both keys function_call and functionCall)
            boolean foundFunctionCall = false;
            try {
                List<Map<String, Object>> parts = ctx.read("$.candidates[0].content.parts");
                StringBuilder textBuilder = new StringBuilder();

                for (Map<String, Object> part : parts) {
                    // 1) function_call (new style)
                    if (part.containsKey("function_call")) {
                        Map<?, ?> fc = (Map<?, ?>) part.get("function_call");
                        Object fname = fc.get("name");
                        fr.setFunctionName(fname == null ? null : fname.toString());

                        Object argsObj = fc.get("args");
                        // args may be Map or String
                        if (argsObj instanceof Map) {
                            Map<?, ?> argsMap = (Map<?, ?>) argsObj;
                            Object cn = argsMap.get("currencyName");
                            Object cd = argsMap.get("currencyData");
                            if (cn != null) fr.setCurrencyName(cn.toString());
                            if (cd != null) fr.setCurrencyData(cd.toString());
                        } else if (argsObj != null) {
                            try {
                                Map<?, ?> argsMap = mapper.readValue(argsObj.toString(), Map.class);
                                Object cn = argsMap.get("currencyName");
                                Object cd = argsMap.get("currencyData");
                                if (cn != null) fr.setCurrencyName(cn.toString());
                                if (cd != null) fr.setCurrencyData(cd.toString());
                            } catch (Exception e) {
                                logger.debug("Could not parse function_call.args as JSON string: {}", e.getMessage());
                            }
                        }
                        foundFunctionCall = true;
                    }
                    // 2) functionCall (older style, fallback)
                    else if (part.containsKey("functionCall")) {
                        Map<?, ?> fc = (Map<?, ?>) part.get("functionCall");
                        Object fname = fc.get("name");
                        fr.setFunctionName(fname == null ? null : fname.toString());

                        Object argsObj = fc.get("args");
                        if (argsObj instanceof Map) {
                            Map<?, ?> argsMap = (Map<?, ?>) argsObj;
                            Object cn = argsMap.get("currencyName");
                            Object cd = argsMap.get("currencyData");
                            if (cn != null) fr.setCurrencyName(cn.toString());
                            if (cd != null) fr.setCurrencyData(cd.toString());
                        } else if (argsObj != null) {
                            try {
                                Map<?, ?> argsMap = mapper.readValue(argsObj.toString(), Map.class);
                                Object cn = argsMap.get("currencyName");
                                Object cd = argsMap.get("currencyData");
                                if (cn != null) fr.setCurrencyName(cn.toString());
                                if (cd != null) fr.setCurrencyData(cd.toString());
                            } catch (Exception e) {
                                logger.debug("Could not parse functionCall.args as JSON string: {}", e.getMessage());
                            }
                        }
                        foundFunctionCall = true;
                    }

                    // collect any plain text if present
                    if (part.containsKey("text")) {
                        Object t = part.get("text");
                        if (t != null) textBuilder.append(t.toString());
                    }
                }

                String finalText = textBuilder.length() == 0 ? null : textBuilder.toString();
                if (!foundFunctionCall) {
                    // model didn't call function: return finalText in currencyData so caller can return it directly
                    fr.setCurrencyData(finalText);
                    fr.setCurrencyName(null);
                    fr.setFunctionName(null);
                    logger.info("No function_call returned by model; returning plain text as currencyData");
                } else {
                    logger.info("Function call detected: name={}, currencyName={}", fr.getFunctionName(), fr.getCurrencyName());
                }
            } catch (Exception e) {
                logger.warn("Failed to parse Gemini response for function_call: {}", e.getMessage());
            }

            return fr;

        } catch (HttpClientErrorException.NotFound nf) {
            logger.warn("Gemini model not found or unsupported for this API version: {}", nf.getResponseBodyAsString());
        } catch (HttpClientErrorException he) {
            logger.error("HTTP error calling Gemini in getFunctionResponse: status={}, body={}", he.getStatusCode(), he.getResponseBodyAsString());
        } catch (Exception ex) {
            logger.error("Unexpected error in getFunctionResponse: {}", ex.getMessage(), ex);
        }

        return null;
    }

    /**
     * Main flow for getCoinDetails:
     * - Ask model (getFunctionResponse). If model chooses function_call -> fetch coin -> send function response -> return final text.
     * - If model did not choose function_call, return model's direct text.
     */
    @Override
    public ApiResponse getCoinDetails(String prompt) {
        ApiResponse apiResponse = new ApiResponse();

        FunctionResponse functionResponse = getFunctionResponse(prompt);
        if (functionResponse == null) {
            apiResponse.setMessage("Unable to contact model.");
            apiResponse.setStatus(false);
            return apiResponse;
        }

        // Case 1: model answered directly (no function call) -> we returned text inside currencyData
        if (functionResponse.getCurrencyName() == null) {
            String direct = functionResponse.getCurrencyData();
            if (direct == null || direct.isEmpty()) {
                apiResponse.setMessage("Model did not provide an answer.");
                apiResponse.setStatus(false);
            } else {
                apiResponse.setMessage(direct);
                apiResponse.setStatus(true);
            }
            return apiResponse;
        }

        // Case 2: model asked to call getCoinDetails(function) -> fetch coin info
        String currencyName = functionResponse.getCurrencyName();
        CoinDTO coinDto = makeApiRequest(currencyName);
        if (coinDto == null) {
            apiResponse.setMessage("Coin not found: " + currencyName);
            apiResponse.setStatus(false);
            return apiResponse;
        }

        // Serialize coin DTO to JSON (ignore toString)
        String coinJson;
        try {
            coinJson = mapper.writeValueAsString(coinDto);
        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize CoinDTO", e);
            coinJson = "{}";
        }

        // Build follow-up conversation: user -> model(function_call) -> function(function_response with coinJson)
        String GEMINI_API_URL = GEMINI_BASE + MODEL_NAME + ":generateContent?key=" + API_KEY;
        Map<String, Object> body = new LinkedHashMap<>();

        // user content
        Map<String, Object> userContent = new HashMap<>();
        userContent.put("role", "user");
        userContent.put("parts", Collections.singletonList(Collections.singletonMap("text", prompt)));

        // model content: simulate the model's function_call (so model can see what it asked)
        Map<String, Object> functionCallPart = new HashMap<>();
        Map<String, Object> functionCall = new HashMap<>();
        functionCall.put("name", "getCoinDetails");
        Map<String, Object> argsMap = new HashMap<>();
        argsMap.put("currencyName", currencyName);
        argsMap.put("currencyData", functionResponse.getCurrencyData() != null ? functionResponse.getCurrencyData() : "");
        // put args as object (not stringified) — model should accept either
        functionCall.put("args", argsMap);
        functionCallPart.put("function_call", functionCall);
        Map<String, Object> modelContent = new HashMap<>();
        modelContent.put("role", "model");
        modelContent.put("parts", Collections.singletonList(functionCallPart));

        // function content: provide the function response (coin JSON)
        Map<String, Object> functionResponseContent = new HashMap<>();
        Map<String, Object> functionResponseWrapper = new HashMap<>();
        functionResponseWrapper.put("name", "getCoinDetails");

        Map<String, Object> innerResponse = new HashMap<>();
        innerResponse.put("name", "getCoinDetails");
        try {
            Object coinAsObj = mapper.readValue(coinJson, Object.class);
            innerResponse.put("content", coinAsObj);
        } catch (Exception e) {
            innerResponse.put("content", coinJson);
        }

        functionResponseWrapper.put("response", innerResponse);
        functionResponseContent.put("role", "function");
        functionResponseContent.put("parts", Collections.singletonList(Collections.singletonMap("function_response", functionResponseWrapper)));

        // assemble contents
        body.put("contents", Arrays.asList(userContent, modelContent, functionResponseContent));

        // include function declarations so model knows the signature (optional but helpful)
        Map<String, Object> functionDecl = new HashMap<>();
        functionDecl.put("name", "getCoinDetails");
        functionDecl.put("description", "Get crypto currency data from given currency object.");
        Map<String, Object> params = new HashMap<>();
        params.put("type", "OBJECT");
        Map<String, Object> props = new HashMap<>();
        Map<String, Object> cnProp = new HashMap<>();
        cnProp.put("type", "STRING");
        cnProp.put("description", "The currency Name, id, symbol.");
        props.put("currencyName", cnProp);
        Map<String, Object> cdProp = new HashMap<>();
        cdProp.put("type", "STRING");
        cdProp.put("description", "The currency data id, symbol, current price, image, market cap etc.");
        props.put("currencyData", cdProp);
        params.put("properties", props);
        params.put("required", Arrays.asList("currencyName", "currencyData"));
        functionDecl.put("parameters", params);

        Map<String, Object> tool = new HashMap<>();
        tool.put("function_declarations", Collections.singletonList(functionDecl));
        body.put("tools", Collections.singletonList(tool));

        // Serialize and call Gemini for final text
        String requestJson;
        try {
            requestJson = mapper.writeValueAsString(body);
        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize final Gemini request", e);
            apiResponse.setMessage("Internal error preparing request to model");
            apiResponse.setStatus(false);
            return apiResponse;
        }

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(requestJson, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(GEMINI_API_URL, requestEntity, String.class);
            String responseBody = response.getBody();
            if (responseBody == null) {
                apiResponse.setMessage("Empty response from model after function response");
                apiResponse.setStatus(false);
                return apiResponse;
            }

            // Extract text from model's final response
            ReadContext ctx = JsonPath.parse(responseBody);
            String finalText = null;
            try {
                finalText = ctx.read("$.candidates[0].content.parts[0].text");
            } catch (Exception ignored) {
            }

            if (finalText == null) {
                // fallback: return coin JSON as message
                apiResponse.setMessage(coinJson);
                apiResponse.setStatus(true);
            } else {
                apiResponse.setMessage(finalText);
                apiResponse.setStatus(true);
            }
            return apiResponse;

        } catch (HttpClientErrorException.NotFound nf) {
            logger.warn("Gemini model not found or unsupported for this API version: {}", nf.getResponseBodyAsString());
            apiResponse.setMessage("Model not found: " + nf.getMessage());
            apiResponse.setStatus(false);
        } catch (HttpClientErrorException he) {
            logger.error("HTTP error calling Gemini in getCoinDetails: status={}, body={}", he.getStatusCode(), he.getResponseBodyAsString());
            apiResponse.setMessage("Model HTTP error: " + he.getStatusText());
            apiResponse.setStatus(false);
        } catch (Exception ex) {
            logger.error("Unexpected error calling Gemini in getCoinDetails: {}", ex.getMessage(), ex);
            apiResponse.setMessage("Unexpected error: " + ex.getMessage());
            apiResponse.setStatus(false);
        }

        return apiResponse;
    }

    @Override
    public CoinDTO getCoinByName(String coinName) {
        return this.makeApiRequest(coinName);
    }

    /**
     * Simple chat endpoint (no function calling) - returns model text if present, fallback to raw response.
     */
    @Override
    public String simpleChat(String prompt) {
        String GEMINI_API_URL = GEMINI_BASE + MODEL_NAME + ":generateContent?key=" + API_KEY;

        Map<String, Object> body = new HashMap<>();
        Map<String, Object> userContent = new HashMap<>();
        userContent.put("parts", Collections.singletonList(Collections.singletonMap("text", prompt)));
        body.put("contents", Collections.singletonList(userContent));

        String requestJson;
        try {
            requestJson = mapper.writeValueAsString(body);
        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize simpleChat body", e);
            return "Internal error";
        }

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(requestJson, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(GEMINI_API_URL, requestEntity, String.class);
            String responseBody = response.getBody();
            if (responseBody == null) return "";

            ReadContext ctx = JsonPath.parse(responseBody);
            try {
                return ctx.read("$.candidates[0].content.parts[0].text");
            } catch (Exception e) {
                return responseBody;
            }

        } catch (HttpClientErrorException.NotFound nf) {
            logger.warn("Gemini model not found: {}", nf.getResponseBodyAsString());
            return "Model not found";
        } catch (HttpClientErrorException he) {
            logger.error("HTTP error calling Gemini in simpleChat: status={}, body={}", he.getStatusCode(), he.getResponseBodyAsString());
            return "Model HTTP error";
        } catch (Exception ex) {
            logger.error("Unexpected error calling Gemini in simpleChat: {}", ex.getMessage(), ex);
            return "Unexpected error";
        }
    }
}

