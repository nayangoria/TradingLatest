package com.zosh.controller;



import com.zosh.DTO.VideoProgressDTO;
import com.zosh.DTO.VideoRequest;
import com.zosh.model.User;
import com.zosh.service.VideoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/learning-path")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Configure properly for production
public class LearningPathController {

    private final VideoService videoService;

    /**
     * Get all videos with user's progress
     */
    @GetMapping("/videos")
    public ResponseEntity<List<VideoProgressDTO>> getVideosWithProgress(
            @AuthenticationPrincipal User currentUser) {
        List<VideoProgressDTO> videos = videoService.getVideosWithProgress(currentUser);
        return ResponseEntity.ok(videos);
    }

    /**
     * Mark a video as completed
     */
    @PostMapping("/videos/{videoId}/complete")
    public ResponseEntity<Void> markVideoComplete(
            @PathVariable Long videoId,
            @AuthenticationPrincipal User currentUser) {
        videoService.markVideoAsComplete(videoId, currentUser);
        return ResponseEntity.ok().build();
    }

    /**
     * Update last watched timestamp
     */
    @PostMapping("/videos/{videoId}/watch")
    public ResponseEntity<Void> updateLastWatched(
            @PathVariable Long videoId,
            @AuthenticationPrincipal User currentUser) {
        videoService.updateLastWatched(videoId, currentUser);
        return ResponseEntity.ok().build();
    }

    /**
     * Create a new video (Admin only)
     */
    @PostMapping("/videos")
    public ResponseEntity<VideoProgressDTO> createVideo(
            @Valid @RequestBody VideoRequest videoRequest) {
        VideoProgressDTO video = videoService.createVideo(videoRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(video);
    }

    /**
     * Update a video (Admin only)
     */
    @PutMapping("/videos/{videoId}")
    public ResponseEntity<VideoProgressDTO> updateVideo(
            @PathVariable Long videoId,
            @Valid @RequestBody VideoRequest videoRequest) {
        VideoProgressDTO video = videoService.updateVideo(videoId, videoRequest);
        return ResponseEntity.ok(video);
    }

    /**
     * Delete a video (Admin only)
     */
    @DeleteMapping("/videos/{videoId}")
    public ResponseEntity<Void> deleteVideo(@PathVariable Long videoId) {
        videoService.deleteVideo(videoId);
        return ResponseEntity.noContent().build();
    }
}
