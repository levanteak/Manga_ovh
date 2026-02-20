package com.manga.ovh.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class ReadingProgressRequest {

    @NotNull(message = "Manga ID is required")
    private UUID mangaId;

    @NotNull(message = "Chapter ID is required")
    private UUID chapterId;

    private Integer lastPage;
}
