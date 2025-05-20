package com.manga.ovh.service;

import com.manga.ovh.entity.Chapter;
import com.manga.ovh.entity.Manga;
import com.manga.ovh.repository.ChapterRepository;
import com.manga.ovh.repository.MangaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChapterService {

    private final ChapterRepository chapterRepository;
    private final MangaRepository mangaRepository;

    public Chapter create(UUID mangaId, Chapter chapter) {
        Manga manga = mangaRepository.findById(mangaId).orElseThrow();
        chapter.setManga(manga);
        chapter.setCreatedAt(LocalDateTime.now());
        return chapterRepository.save(chapter);
    }

    public List<Chapter> getAllByManga(UUID mangaId) {
        return chapterRepository.findByMangaId(mangaId);
    }
}
