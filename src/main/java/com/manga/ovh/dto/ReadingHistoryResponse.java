package com.manga.ovh.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class ReadingHistoryResponse {
    private UUID mangaId;
    private String mangaTitle;
    private String coverUrl;
    private UUID chapterId;
    private Integer chapterNumber;
    private Integer lastPage;
    private LocalDateTime updatedAt;
}
