package com.zosh.controller;



import com.zosh.DTO.AiRequest;
import com.zosh.DTO.AiResponse;
import com.zosh.service.AiAnalysisService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
public class AiAnalysisController {

    private final AiAnalysisService aiAnalysisService;

    public AiAnalysisController(AiAnalysisService aiAnalysisService) {
        this.aiAnalysisService = aiAnalysisService;
    }

    @PostMapping("/analyze-coin")
    public AiResponse analyzeCoin(@RequestBody AiRequest request) {
        return aiAnalysisService.analyzeCoin(request);
    }

    @GetMapping("/health")
    public String health() {
        return "AI analysis OK";
    }
}