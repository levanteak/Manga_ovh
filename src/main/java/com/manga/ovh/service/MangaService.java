package com.manga.ovh.service;

import com.manga.ovh.dto.MangaCreateRequest;
import com.manga.ovh.entity.*;
import com.manga.ovh.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MangaService {

    private final MangaRepository mangaRepository;
    private final GenreRepository genreRepository;
    private final PublisherRepository publisherRepository;
    private final S3Service s3Service;

    public Manga create(MangaCreateRequest request) {
        Set<Genre> genres = request.getGenres() != null
                ? request.getGenres().stream()
                .map(name -> genreRepository.findByName(name)
                        .orElseGet(() -> genreRepository.save(Genre.builder().name(name).build())))
                .collect(Collectors.toSet())
                : Set.of();

        Publisher publisher = publisherRepository.findByName(request.getPublisherName())
                .orElseGet(() -> publisherRepository.save(Publisher.builder().name(request.getPublisherName()).build()));

        String coverUrl = request.getCover() != null
                ? s3Service.uploadFile(request.getCover(), "covers")
                : null;

        Manga manga = Manga.builder()
                .title(request.getTitle())
                .originalTitle(request.getOriginalTitle())
                .description(request.getDescription())
                .status(request.getStatus())
                .category(request.getCategory())
                .releaseYear(request.getReleaseYear())
                .country(request.getCountry())
                .author(request.getAuthor())
                .artist(request.getArtist())
                .publisher(publisher)
                .coverUrl(coverUrl)
                .genres(genres)
                .build();

        return mangaRepository.save(manga);
    }

    public Manga getById(UUID id) {
        return mangaRepository.findById(id).orElseThrow();
    }
}
