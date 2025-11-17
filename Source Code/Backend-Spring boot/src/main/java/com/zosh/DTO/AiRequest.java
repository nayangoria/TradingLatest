package com.zosh.DTO;



import com.zosh.model.Coin;

import java.util.Map;

public class AiRequest {
    private String userId;
    private String message;
    private Map<String, Object> meta;
    private Coin marketCoin; // optional if frontend provides coin info

    // getters / setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public Map<String, Object> getMeta() { return meta; }
    public void setMeta(Map<String, Object> meta) { this.meta = meta; }
    public Coin getMarketCoin() { return marketCoin; }
    public void setMarketCoin(Coin marketCoin) { this.marketCoin = marketCoin; }
}
