package com.manga.ovh.repository;

import com.manga.ovh.entity.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ChapterRepository extends JpaRepository<Chapter, UUID> {
    List<Chapter> findByMangaId(UUID mangaId);
}
