package com.zosh.service;

import com.zosh.client.OllamaClient;
import com.zosh.DTO.VisionRequest;
import com.zosh.DTO.VisionResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;

@Service
public class VisionAnalysisService {

    private final OllamaClient ollamaClient;

    public VisionAnalysisService(OllamaClient ollamaClient) {
        this.ollamaClient = ollamaClient;
    }

    /**
     * Analyze a chart/image with crypto analysis focus
     */
    public VisionResponse analyzeChartImage(MultipartFile image, String prompt) {
        VisionResponse response = new VisionResponse();

        try {
            // Validate image
            if (image.isEmpty()) {
                response.setOk(false);
                response.setError("Image file is empty");
                return response;
            }

            // Convert image to base64
            byte[] imageBytes = image.getBytes();
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);

            // Build crypto-specific prompt if not provided
            if (prompt == null || prompt.trim().isEmpty()) {
                prompt = buildDefaultCryptoPrompt();
            }

            // Call Ollama vision model
            String analysisResult = ollamaClient.analyzeImage(prompt, base64Image);

            response.setOk(true);
            response.setAnalysis(analysisResult);
            response.setPrompt(prompt);
            return response;

        } catch (IOException e) {
            response.setOk(false);
            response.setError("Error processing image: " + e.getMessage());
            return response;
        } catch (Exception e) {
            response.setOk(false);
            response.setError("Unexpected error: " + e.getMessage());
            return response;
        }
    }

    /**
     * Analyze chart with custom prompt
     */
    public VisionResponse analyzeWithCustomPrompt(VisionRequest request) {
        VisionResponse response = new VisionResponse();

        try {
            if (request.getImageBase64() == null || request.getImageBase64().isEmpty()) {
                response.setOk(false);
                response.setError("No image data provided");
                return response;
            }

            String prompt = request.getPrompt() != null
                    ? request.getPrompt()
                    : buildDefaultCryptoPrompt();

            String analysisResult = ollamaClient.analyzeImage(prompt, request.getImageBase64());

            response.setOk(true);
            response.setAnalysis(analysisResult);
            response.setPrompt(prompt);
            return response;

        } catch (Exception e) {
            response.setOk(false);
            response.setError("Error analyzing image: " + e.getMessage());
            return response;
        }
    }

    /**
     * Compare multiple charts
     */
    public VisionResponse compareCharts(String[] imagesBase64, String prompt) {
        VisionResponse response = new VisionResponse();

        try {
            if (imagesBase64 == null || imagesBase64.length == 0) {
                response.setOk(false);
                response.setError("No images provided");
                return response;
            }

            if (prompt == null || prompt.trim().isEmpty()) {
                prompt = "Compare these cryptocurrency charts. Identify key differences in trends, " +
                        "patterns, support/resistance levels, and provide insights about which " +
                        "shows stronger momentum. NOT FINANCIAL ADVICE.";
            }

            String analysisResult = ollamaClient.analyzeMultipleImages(prompt, imagesBase64);

            response.setOk(true);
            response.setAnalysis(analysisResult);
            response.setPrompt(prompt);
            return response;

        } catch (Exception e) {
            response.setOk(false);
            response.setError("Error comparing charts: " + e.getMessage());
            return response;
        }
    }

    private String buildDefaultCryptoPrompt() {
        return "Analyze this cryptocurrency chart or trading screenshot. Identify:\n" +
                "1. The main trend (bullish, bearish, or sideways)\n" +
                "2. Key support and resistance levels visible\n" +
                "3. Any chart patterns (triangles, head and shoulders, channels, etc.)\n" +
                "4. Volume patterns if visible\n" +
                "5. Potential entry/exit points\n" +
                "6. Risk assessment\n\n" +
                "Provide a clear, structured analysis. " +
                "Remember: This is NOT FINANCIAL ADVICE, only educational analysis.";
    }
}
