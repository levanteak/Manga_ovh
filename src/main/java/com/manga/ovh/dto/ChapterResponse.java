package com.manga.ovh.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChapterResponse {
    private UUID id;
    private String title;
    private Integer number;
    private LocalDateTime releaseDate;
    private Integer views;
}

