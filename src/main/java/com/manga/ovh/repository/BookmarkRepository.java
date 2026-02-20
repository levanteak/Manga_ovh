package com.manga.ovh.repository;

import com.manga.ovh.entity.Bookmark;
import com.manga.ovh.enums.BookmarkStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookmarkRepository extends JpaRepository<Bookmark, UUID> {

    Optional<Bookmark> findByUserIdAndMangaId(UUID userId, UUID mangaId);

    List<Bookmark> findByUserIdOrderByUpdatedAtDesc(UUID userId);

    List<Bookmark> findByUserIdAndStatusOrderByUpdatedAtDesc(UUID userId, BookmarkStatus status);

    void deleteByUserIdAndMangaId(UUID userId, UUID mangaId);

    boolean existsByUserIdAndMangaId(UUID userId, UUID mangaId);
}
