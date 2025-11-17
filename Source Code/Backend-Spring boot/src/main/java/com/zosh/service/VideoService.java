package com.zosh.service;

import com.zosh.DTO.VideoProgressDTO;
import com.zosh.DTO.VideoRequest;
import com.zosh.model.User;

import java.util.List;

public interface VideoService {
    List<VideoProgressDTO> getVideosWithProgress(User user);
    void markVideoAsComplete(Long videoId, User user);
    void updateLastWatched(Long videoId, User user);
    VideoProgressDTO createVideo(VideoRequest videoRequest);
    VideoProgressDTO updateVideo(Long videoId, VideoRequest videoRequest);
    void deleteVideo(Long videoId);
}
