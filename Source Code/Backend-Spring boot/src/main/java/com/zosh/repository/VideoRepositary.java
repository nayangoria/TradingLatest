package com.zosh.repository;

import com.zosh.model.Video;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;

public interface VideoRepositary extends JpaRepository<Video,Long> {
    List<Video> findAllByOrderBySequenceOrderAsc();

}
