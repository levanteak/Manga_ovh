package com.manga.ovh.dto;

import com.manga.ovh.entity.Chapter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChapterResponse {
    private UUID id;
    private UUID mangaId;
    private String title;
    private Integer number;
    private LocalDateTime releaseDate;
    private Integer views;

    public static ChapterResponse from(Chapter chapter) {
        return ChapterResponse.builder()
                .id(chapter.getId())
                .mangaId(chapter.getManga() != null ? chapter.getManga().getId() : null)
                .title(chapter.getTitle())
                .number(chapter.getNumber())
                .releaseDate(chapter.getReleaseDate())
                .views(chapter.getViews())
                .build();
    }
}
