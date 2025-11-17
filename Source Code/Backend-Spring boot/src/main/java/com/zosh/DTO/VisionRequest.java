
package com.zosh.DTO;

// VisionRequest.java
public class VisionRequest {
    private String prompt;
    private String imageBase64;

    public VisionRequest() {}

    public VisionRequest(String prompt, String imageBase64) {
        this.prompt = prompt;
        this.imageBase64 = imageBase64;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public String getImageBase64() {
        return imageBase64;
    }

    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }
}