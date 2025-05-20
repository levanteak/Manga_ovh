package com.manga.ovh.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ChapterCreateRequest {
    private Integer number;
    private String title;
    private String releaseDate; // ISO строка
    private MultipartFile[] pages;
}
