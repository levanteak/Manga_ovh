package com.manga.ovh.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CommentResponse {
    private String username;
    private String content;
    private LocalDateTime createdAt;
}

