package com.zosh.service;


import com.zosh.DTO.VideoProgressDTO;
import com.zosh.DTO.VideoRequest;
import com.zosh.model.User;
import com.zosh.model.Video;
import com.zosh.repository.UserVideoProgressRepositary;
import com.zosh.repository.VideoRepositary;
import com.zosh.model.UserVideoProgress;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VideoServiceImpl implements VideoService {

    private final VideoRepositary videoRepository;
    private final UserVideoProgressRepositary progressRepository;

    @Override
    @Transactional(readOnly = true)
    public List<VideoProgressDTO> getVideosWithProgress(User user) {
        List<Video> videos = videoRepository.findAllByOrderBySequenceOrderAsc();
        List<UserVideoProgress> userProgress = progressRepository.findByUser(user);

        // Create a map for quick lookup
        Map<Long, UserVideoProgress> progressMap = userProgress.stream()
                .collect(Collectors.toMap(
                        progress -> progress.getVideo().getId(),
                        progress -> progress
                ));

        // Map videos to DTOs with progress information
        return videos.stream()
                .map(video -> {
                    UserVideoProgress progress = progressMap.get(video.getId());
                    return new VideoProgressDTO(
                            video.getId(),
                            video.getTitle(),
                            video.getDescription(),
                            video.getVideoUrl(),
                            video.getDuration(),
                            video.getSequenceOrder(),
                            progress != null && progress.getCompleted(),
                            progress != null ? progress.getCompletedAt() : null,
                            progress != null ? progress.getLastWatchedAt() : null
                    );
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void markVideoAsComplete(Long videoId, User user) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new RuntimeException("Video not found with id: " + videoId));

        UserVideoProgress progress = progressRepository.findByUserAndVideo(user, video)
                .orElse(new UserVideoProgress());

        progress.setUser(user);
        progress.setVideo(video);
        progress.setCompleted(true);
        progress.setCompletedAt(LocalDateTime.now());
        progress.setLastWatchedAt(LocalDateTime.now());

        progressRepository.save(progress);
    }

    @Override
    @Transactional
    public void updateLastWatched(Long videoId, User user) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new RuntimeException("Video not found with id: " + videoId));

        UserVideoProgress progress = progressRepository.findByUserAndVideo(user, video)
                .orElse(new UserVideoProgress());

        progress.setUser(user);
        progress.setVideo(video);
        progress.setLastWatchedAt(LocalDateTime.now());

        progressRepository.save(progress);
    }

    @Override
    @Transactional
    public VideoProgressDTO createVideo(VideoRequest videoRequest) {
        Video video = new Video();
        video.setTitle(videoRequest.getTitle());
        video.setDescription(videoRequest.getDescription());
        video.setVideoUrl(videoRequest.getVideoUrl());
        video.setDuration(videoRequest.getDuration());
        video.setSequenceOrder(videoRequest.getSequenceOrder());

        Video savedVideo = videoRepository.save(video);

        return new VideoProgressDTO(
                savedVideo.getId(),
                savedVideo.getTitle(),
                savedVideo.getDescription(),
                savedVideo.getVideoUrl(),
                savedVideo.getDuration(),
                savedVideo.getSequenceOrder(),
                false,
                null,
                null
        );
    }

    @Override
    @Transactional
    public VideoProgressDTO updateVideo(Long videoId, VideoRequest videoRequest) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new RuntimeException("Video not found with id: " + videoId));

        video.setTitle(videoRequest.getTitle());
        video.setDescription(videoRequest.getDescription());
        video.setVideoUrl(videoRequest.getVideoUrl());
        video.setDuration(videoRequest.getDuration());
        video.setSequenceOrder(videoRequest.getSequenceOrder());

        Video updatedVideo = videoRepository.save(video);

        return new VideoProgressDTO(
                updatedVideo.getId(),
                updatedVideo.getTitle(),
                updatedVideo.getDescription(),
                updatedVideo.getVideoUrl(),
                updatedVideo.getDuration(),
                updatedVideo.getSequenceOrder(),
                false,
                null,
                null
        );
    }

    @Override
    @Transactional
    public void deleteVideo(Long videoId) {
        if (!videoRepository.existsById(videoId)) {
            throw new RuntimeException("Video not found with id: " + videoId);
        }
        videoRepository.deleteById(videoId);
    }
}
