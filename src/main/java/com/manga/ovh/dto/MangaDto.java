package com.manga.ovh.dto;

import com.manga.ovh.entity.Manga;
import com.manga.ovh.enums.MangaCategory;
import com.manga.ovh.enums.MangaStatus;
import lombok.Builder;

import java.util.UUID;

@Builder
public record MangaDto(
        UUID id,
        String title,
        String originalTitle,
        String description,
        MangaStatus status,
        MangaCategory category,
        Integer releaseYear,
        String country,
        String author,
        String artist,
        String coverUrl,
        String publisherName
) {
    public static MangaDto from(Manga manga) {
        return MangaDto.builder()
                .id(manga.getId())
                .title(manga.getTitle())
                .originalTitle(manga.getOriginalTitle())
                .description(manga.getDescription())
                .status(manga.getStatus())
                .category(manga.getCategory())
                .releaseYear(manga.getReleaseYear())
                .country(manga.getCountry())
                .author(manga.getAuthor())
                .artist(manga.getArtist())
                .coverUrl(manga.getCoverUrl())
                .publisherName(
                        manga.getPublisher() != null
                                ? manga.getPublisher().getName()
                                : null
                )
                .build();
    }
}
