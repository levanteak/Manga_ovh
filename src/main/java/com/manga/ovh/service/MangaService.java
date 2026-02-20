package com.manga.ovh.service;

import com.manga.ovh.dto.MangaCreateRequest;
import com.manga.ovh.dto.MangaDto;
import com.manga.ovh.dto.MangaFilter;
import com.manga.ovh.entity.*;
import com.manga.ovh.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MangaService {

    private final MangaRepository mangaRepository;
    private final GenreRepository genreRepository;
    private final PublisherRepository publisherRepository;
    private final TagRepository tagRepository;
    private final S3Service s3Service;

    public Manga create(MangaCreateRequest request) {
        Set<Genre> genres = request.getGenres() != null
                ? request.getGenres().stream()
                .map(name -> genreRepository.findByName(name)
                        .orElseGet(() -> genreRepository.save(Genre.builder().name(name).build())))
                .collect(Collectors.toSet())
                : Set.of();

        Set<Tag> tags = request.getTags() != null
                ? request.getTags().stream()
                .map(name -> tagRepository.findByName(name)
                        .orElseGet(() -> tagRepository.save(Tag.builder().name(name).build())))
                .collect(Collectors.toSet())
                : Set.of();

        Publisher publisher = publisherRepository.findByName(request.getPublisherName())
                .orElseGet(() -> publisherRepository.save(
                        Publisher.builder().name(request.getPublisherName()).build()));

        String coverUrl = null;
        MultipartFile coverFile = request.getCover();
        if (coverFile != null && !coverFile.isEmpty()) {
            coverUrl = s3Service.uploadFile(coverFile, "covers");
        }

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
                .tags(tags)
                .views(0)
                .likes(0)
                .averageRating(0.0)
                .ratingCount(0L)
                .build();

        return mangaRepository.save(manga);
    }

    @Cacheable(value = "manga", key = "#id")
    public MangaDto getDtoById(UUID id) {
        return mangaRepository.findById(id)
                .map(MangaDto::from)
                .orElseThrow(() -> new RuntimeException("Манга с ID " + id + " не найдена"));
    }

    public List<MangaDto> getAll() {
        return mangaRepository.findAll().stream()
                .map(MangaDto::from)
                .toList();
    }

    public Page<MangaDto> getFiltered(MangaFilter filter) {
        Sort sort = Sort.by(
                "ASC".equalsIgnoreCase(filter.getSortDir())
                        ? Sort.Direction.ASC
                        : Sort.Direction.DESC,
                resolveSortField(filter.getSortBy())
        );
        Pageable pageable = PageRequest.of(filter.getPage(), filter.getSize(), sort);
        return mangaRepository.findAll(MangaSpecification.withFilter(filter), pageable)
                .map(MangaDto::from);
    }

    @CacheEvict(value = "manga", key = "#id")
    public MangaDto update(UUID id, MangaCreateRequest request) {
        Manga manga = mangaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Манга не найдена"));

        manga.setTitle(request.getTitle());
        manga.setOriginalTitle(request.getOriginalTitle());
        manga.setDescription(request.getDescription());
        manga.setStatus(request.getStatus());
        manga.setCategory(request.getCategory());
        manga.setReleaseYear(request.getReleaseYear());
        manga.setCountry(request.getCountry());
        manga.setAuthor(request.getAuthor());
        manga.setArtist(request.getArtist());

        MultipartFile cover = request.getCover();
        if (cover != null && !cover.isEmpty()) {
            manga.setCoverUrl(s3Service.uploadFile(cover, "covers"));
        }

        Set<Genre> genres = request.getGenres() != null
                ? request.getGenres().stream()
                .map(name -> genreRepository.findByName(name)
                        .orElseGet(() -> genreRepository.save(Genre.builder().name(name).build())))
                .collect(Collectors.toSet())
                : Set.of();
        manga.setGenres(genres);

        Set<Tag> tags = request.getTags() != null
                ? request.getTags().stream()
                .map(name -> tagRepository.findByName(name)
                        .orElseGet(() -> tagRepository.save(Tag.builder().name(name).build())))
                .collect(Collectors.toSet())
                : Set.of();
        manga.setTags(tags);

        Publisher publisher = publisherRepository.findByName(request.getPublisherName())
                .orElseGet(() -> publisherRepository.save(
                        Publisher.builder().name(request.getPublisherName()).build()));
        manga.setPublisher(publisher);

        return MangaDto.from(mangaRepository.save(manga));
    }

    @CacheEvict(value = "manga", key = "#id")
    public void delete(UUID id) {
        Manga manga = mangaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Манга не найдена"));
        mangaRepository.delete(manga);
    }

    private String resolveSortField(String sortBy) {
        return switch (sortBy) {
            case "views" -> "views";
            case "averageRating" -> "averageRating";
            case "title" -> "title";
            default -> "createdAt";
        };
    }
}
