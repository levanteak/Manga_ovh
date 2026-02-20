package com.manga.ovh.dto;

import com.manga.ovh.enums.BookmarkStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class BookmarkResponse {
    private UUID mangaId;
    private String mangaTitle;
    private String coverUrl;
    private BookmarkStatus status;
    private LocalDateTime updatedAt;
}
