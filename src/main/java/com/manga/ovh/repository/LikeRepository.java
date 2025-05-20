package com.manga.ovh.repository;

import com.manga.ovh.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LikeRepository extends JpaRepository<Like, UUID> {
    boolean existsByUserIdAndMangaId(UUID userId, UUID mangaId);
    long countByMangaId(UUID mangaId);
}
