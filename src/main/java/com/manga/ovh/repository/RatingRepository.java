package com.manga.ovh.repository;

import com.manga.ovh.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface RatingRepository extends JpaRepository<Rating, UUID> {

    Optional<Rating> findByUserIdAndMangaId(UUID userId, UUID mangaId);

    @Query("SELECT AVG(r.score) FROM Rating r WHERE r.manga.id = :mangaId")
    Optional<Double> findAverageScoreByMangaId(@Param("mangaId") UUID mangaId);

    long countByMangaId(UUID mangaId);
}
