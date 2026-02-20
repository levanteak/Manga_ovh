package com.manga.ovh.dto;

import com.manga.ovh.entity.Manga;
import com.manga.ovh.enums.MangaCategory;
import com.manga.ovh.enums.MangaStatus;
import lombok.Builder;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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
        String publisherName,
        Integer views,
        Integer likes,
        Double averageRating,
        Long ratingCount,
        Set<String> genres,
        Set<String> tags
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
                .publisherName(manga.getPublisher() != null ? manga.getPublisher().getName() : null)
                .views(manga.getViews())
                .likes(manga.getLikes())
                .averageRating(manga.getAverageRating())
                .ratingCount(manga.getRatingCount())
                .genres(manga.getGenres() != null
                        ? manga.getGenres().stream().map(g -> g.getName()).collect(Collectors.toSet())
                        : Set.of())
                .tags(manga.getTags() != null
                        ? manga.getTags().stream().map(t -> t.getName()).collect(Collectors.toSet())
                        : Set.of())
                .build();
    }
}
