package com.zosh.DTO;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoProgressDTO {
    private Long id;
    private String title;
    private String description;
    private String videoUrl;
    private String duration;
    private Integer sequenceOrder;
    private Boolean completed;
    private LocalDateTime completedAt;
    private LocalDateTime lastWatchedAt;
}
