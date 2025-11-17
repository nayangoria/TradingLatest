package com.zosh.DTO;



import java.util.List;

public class MarketData {
    // Example: OHLC or price list; keep simple for demo
    private String symbol;
    private List<Double> prices;   // e.g., last N closing prices
    private Double marketCap;
    private Double change24h;
    private Double rsi;           // optional RSI value

    // getters & setters
    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }
    public List<Double> getPrices() { return prices; }
    public void setPrices(List<Double> prices) { this.prices = prices; }
    public Double getMarketCap() { return marketCap; }
    public void setMarketCap(Double marketCap) { this.marketCap = marketCap; }
    public Double getChange24h() { return change24h; }
    public void setChange24h(Double change24h) { this.change24h = change24h; }
    public Double getRsi() { return rsi; }
    public void setRsi(Double rsi) { this.rsi = rsi; }
}
