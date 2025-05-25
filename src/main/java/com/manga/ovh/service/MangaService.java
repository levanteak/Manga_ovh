package com.manga.ovh.service;

import com.manga.ovh.dto.MangaCreateRequest;
import com.manga.ovh.dto.MangaDto;
import com.manga.ovh.entity.*;
import com.manga.ovh.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
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
    private final S3Service s3Service;

    public Manga create(MangaCreateRequest request) {
        // 1. Обработка жанров
        Set<Genre> genres = request.getGenres() != null
                ? request.getGenres().stream()
                .map(name -> genreRepository.findByName(name)
                        .orElseGet(() -> genreRepository.save(
                                Genre.builder().name(name).build())))
                .collect(Collectors.toSet())
                : Set.of();

        // 2. Обработка издательства
        Publisher publisher = publisherRepository.findByName(request.getPublisherName())
                .orElseGet(() -> publisherRepository.save(
                        Publisher.builder().name(request.getPublisherName()).build()));

        // 3. Загрузка обложки в S3
        String coverUrl = null;
        MultipartFile coverFile = request.getCover();

        if (coverFile != null && !coverFile.isEmpty()) {
            try {
                coverUrl = s3Service.uploadFile(coverFile, "covers");
                System.out.println("✅ Успешная загрузка обложки, URL: " + coverUrl);
            } catch (Exception e) {
                System.err.println("❌ Ошибка при загрузке обложки в S3: " + e.getMessage());
                throw new RuntimeException("Не удалось загрузить обложку в S3", e);
            }
        } else {
            System.out.println("⚠️ Обложка отсутствует или файл пустой");
        }

        // 4. Сбор и сохранение Manga
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

    @Cacheable(value = "manga", key = "#id")
    public MangaDto getDtoById(UUID id) {
        System.out.println("🔥 FETCH FROM DB: " + id);
        return mangaRepository.findById(id)
                .map(MangaDto::from)
                .orElseThrow(() -> new RuntimeException("Манга с ID " + id + " не найдена"));
    }


    public List<MangaDto> getAll() {
        return mangaRepository.findAll().stream()
                .map(MangaDto::from)
                .toList();
    }

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

        // Обновить обложку
        MultipartFile cover = request.getCover();
        if (cover != null && !cover.isEmpty()) {
            String coverUrl = s3Service.uploadFile(cover, "covers");
            manga.setCoverUrl(coverUrl);
        }

        // Обновить жанры
        Set<Genre> genres = request.getGenres() != null
                ? request.getGenres().stream()
                .map(name -> genreRepository.findByName(name)
                        .orElseGet(() -> genreRepository.save(Genre.builder().name(name).build())))
                .collect(Collectors.toSet())
                : Set.of();
        manga.setGenres(genres);

        // Обновить издателя
        Publisher publisher = publisherRepository.findByName(request.getPublisherName())
                .orElseGet(() -> publisherRepository.save(Publisher.builder().name(request.getPublisherName()).build()));
        manga.setPublisher(publisher);

        return MangaDto.from(mangaRepository.save(manga));
    }

    public void delete(UUID id) {
        Manga manga = mangaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Манга не найдена"));
        mangaRepository.delete(manga);
    }


}
