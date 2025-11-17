package com.zosh.repository;

import com.zosh.model.User;
import com.zosh.model.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import com.zosh.model.UserVideoProgress;

import java.util.List;
import java.util.Optional;

public interface UserVideoProgressRepositary extends JpaRepository<UserVideoProgress,Long> {
    Optional<UserVideoProgress> findByUserAndVideo(User user, Video video);
    List<UserVideoProgress> findByUser(User user);
    long countByUserAndCompletedTrue(User user);
}
