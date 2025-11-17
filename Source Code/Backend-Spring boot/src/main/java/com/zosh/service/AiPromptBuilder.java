//package com.zosh.service;
//
//
//
//import java.util.List;
//import java.util.Locale;
//import java.util.StringJoiner;
//
//public class AiPromptBuilder {
//
//    public static String buildPrompt(String userMessage, String coinId, List<Double> prices) {
//        StringBuilder sb = new StringBuilder();
//
//        sb.append("You are a helpful conservative trading assistant. Do not give financial advice. " +
//                "All actionable suggestions must be labeled 'NOT FINANCIAL ADVICE'.\n\n");
//
//        sb.append("User request: ").append(userMessage).append("\n\n");
//        sb.append(String.format(Locale.ROOT, "Real data for %s (oldest -> newest) - %d points:\n", coinId, prices.size()));
//        sb.append("PRICES: [");
//        StringJoiner sj = new StringJoiner(", ");
//        for (Double p : prices) sj.add(String.format(Locale.ROOT, "%.2f", p));
//        sb.append(sj.toString()).append("]\n\n");
//
//        sb.append("Using the data above, produce EXACTLY this OUTPUT format:\n\n");
//        sb.append("INSIGHTS:\n");
//        sb.append("- insight 1\n");
//        sb.append("- insight 2\n");
//        sb.append("RISK: <low|medium|high> - one sentence\n");
//        sb.append("SUPPORT/RESISTANCE: list probable levels (comma separated)\n");
//        sb.append("SUGGESTED (example): Entry: ..., Stop: ..., Target: ...\n");
//        sb.append("SHORT NOTE: All suggestions are NOT FINANCIAL ADVICE.\n");
//
//        return sb.toString();
//    }
//}
//

package com.zosh.service;

import java.util.List;
import java.util.Locale;
import java.util.StringJoiner;

public class AiPromptBuilder {

    public static String buildPrompt(String userMessage, String coinId, List<Double> prices) {
        StringBuilder sb = new StringBuilder();

        // Make it crystal clear which coin we're analyzing
        String coinName = getCoinDisplayName(coinId);

        sb.append("CRITICAL: You are analyzing ").append(coinName.toUpperCase())
                .append(" cryptocurrency. DO NOT analyze Bitcoin or any other coin.\n\n");

        sb.append("You are a helpful conservative trading assistant. Do not give financial advice. " +
                "All actionable suggestions must be labeled 'NOT FINANCIAL ADVICE'.\n\n");

        sb.append("User request: ").append(userMessage).append("\n\n");

        // Emphasize the coin multiple times
        sb.append("=== ANALYZING: ").append(coinName.toUpperCase()).append(" ===\n");
        sb.append(String.format(Locale.ROOT, "Cryptocurrency: %s (ID: %s)\n", coinName, coinId));
        sb.append(String.format(Locale.ROOT, "Price data points: %d (oldest -> newest)\n\n", prices.size()));

        // Calculate basic stats to help the model
        double firstPrice = prices.get(0);
        double lastPrice = prices.get(prices.size() - 1);
        double priceChange = ((lastPrice - firstPrice) / firstPrice) * 100;
        double maxPrice = prices.stream().mapToDouble(Double::doubleValue).max().orElse(0);
        double minPrice = prices.stream().mapToDouble(Double::doubleValue).min().orElse(0);

        sb.append(String.format(Locale.ROOT, "Current Price: $%.2f\n", lastPrice));
        sb.append(String.format(Locale.ROOT, "Price Change: %.2f%%\n", priceChange));
        sb.append(String.format(Locale.ROOT, "Period High: $%.2f\n", maxPrice));
        sb.append(String.format(Locale.ROOT, "Period Low: $%.2f\n\n", minPrice));

        sb.append("PRICE DATA: [");
        StringJoiner sj = new StringJoiner(", ");
        for (Double p : prices) sj.add(String.format(Locale.ROOT, "%.2f", p));
        sb.append(sj.toString()).append("]\n\n");

        sb.append("IMPORTANT: Base your analysis ONLY on the ").append(coinName.toUpperCase())
                .append(" data provided above. Do not mention Bitcoin unless the coin being analyzed IS Bitcoin.\n\n");

        sb.append("Produce EXACTLY this OUTPUT format:\n\n");
        sb.append("COIN: ").append(coinName.toUpperCase()).append("\n\n");
        sb.append("INSIGHTS:\n");
        sb.append("- insight 1 about ").append(coinName).append("\n");
        sb.append("- insight 2 about ").append(coinName).append("\n");
        sb.append("- insight 3 about ").append(coinName).append("\n\n");
        sb.append("RISK: <low|medium|high> - brief explanation\n\n");
        sb.append("SUPPORT/RESISTANCE LEVELS:\n");
        sb.append("Support: [list levels based on the data]\n");
        sb.append("Resistance: [list levels based on the data]\n\n");
        sb.append("SUGGESTED ENTRY POINT (NOT FINANCIAL ADVICE):\n");
        sb.append("Entry: $[price level]\n");
        sb.append("Stop Loss: $[price level]\n");
        sb.append("Target: $[price level]\n\n");
        sb.append("SHORT NOTE: This analysis is for ").append(coinName)
                .append(" only and does NOT constitute financial advice. " +
                        "Always do your own research before making investment decisions.\n");

        return sb.toString();
    }

    /**
     * Convert CoinGecko ID to display name
     */
    private static String getCoinDisplayName(String coinId) {
        switch (coinId.toLowerCase()) {
            case "bitcoin": return "Bitcoin (BTC)";
            case "ethereum": return "Ethereum (ETH)";
            case "solana": return "Solana (SOL)";
            case "dogecoin": return "Dogecoin (DOGE)";
            case "binancecoin": return "Binance Coin (BNB)";
            case "cardano": return "Cardano (ADA)";
            case "ripple": return "Ripple (XRP)";
            case "polkadot": return "Polkadot (DOT)";
            case "avalanche-2": return "Avalanche (AVAX)";
            case "chainlink": return "Chainlink (LINK)";
            case "polygon": return "Polygon (MATIC)";
            case "uniswap": return "Uniswap (UNI)";
            case "litecoin": return "Litecoin (LTC)";
            case "stellar": return "Stellar (XLM)";
            case "tron": return "Tron (TRX)";
            default: return coinId.substring(0, 1).toUpperCase() + coinId.substring(1);
        }
    }
}
