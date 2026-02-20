package com.manga.ovh.repository;

import com.manga.ovh.entity.ReadingHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReadingHistoryRepository extends JpaRepository<ReadingHistory, UUID> {

    Optional<ReadingHistory> findByUserIdAndMangaId(UUID userId, UUID mangaId);

    List<ReadingHistory> findByUserIdOrderByUpdatedAtDesc(UUID userId);

    void deleteByUserIdAndMangaId(UUID userId, UUID mangaId);
}
