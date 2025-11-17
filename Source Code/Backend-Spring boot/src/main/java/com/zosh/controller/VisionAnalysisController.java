package com.zosh.controller;

import com.zosh.DTO.VisionRequest;
import com.zosh.DTO.VisionResponse;
import com.zosh.service.VisionAnalysisService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/vision")
public class VisionAnalysisController {

    private final VisionAnalysisService visionAnalysisService;

    public VisionAnalysisController(VisionAnalysisService visionAnalysisService) {
        this.visionAnalysisService = visionAnalysisService;
    }

    /**
     * Analyze chart image with multipart file upload
     * POST /api/vision/analyze-chart
     * Content-Type: multipart/form-data
     * Body: image (file), prompt (optional text)
     */
    @PostMapping(value = "/analyze-chart", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public VisionResponse analyzeChart(
            @RequestParam("image") MultipartFile image,
            @RequestParam(value = "prompt", required = false) String prompt
    ) {
        return visionAnalysisService.analyzeChartImage(image, prompt);
    }

    /**
     * Analyze with base64 encoded image
     * POST /api/vision/analyze
     * Content-Type: application/json
     * Body: { "imageBase64": "...", "prompt": "..." }
     */
    @PostMapping("/analyze")
    public VisionResponse analyzeBase64(@RequestBody VisionRequest request) {
        return visionAnalysisService.analyzeWithCustomPrompt(request);
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public String health() {
        return "Vision analysis OK";
    }
}
