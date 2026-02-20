package com.manga.ovh.service;

import com.manga.ovh.dto.RatingResponse;
import com.manga.ovh.entity.Manga;
import com.manga.ovh.entity.Rating;
import com.manga.ovh.entity.User;
import com.manga.ovh.repository.MangaRepository;
import com.manga.ovh.repository.RatingRepository;
import com.manga.ovh.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RatingService {

    private final RatingRepository ratingRepository;
    private final MangaRepository mangaRepository;
    private final UserRepository userRepository;

    @Transactional
    public RatingResponse rate(UUID mangaId, String username, int score) {
        Manga manga = mangaRepository.findById(mangaId)
                .orElseThrow(() -> new RuntimeException("Манга не найдена"));
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        Rating rating = ratingRepository.findByUserIdAndMangaId(user.getId(), mangaId)
                .orElseGet(() -> Rating.builder().user(user).manga(manga).build());

        rating.setScore(score);
        ratingRepository.save(rating);

        // Пересчёт среднего рейтинга манги
        double avg = ratingRepository.findAverageScoreByMangaId(mangaId).orElse(0.0);
        long count = ratingRepository.countByMangaId(mangaId);

        manga.setAverageRating(Math.round(avg * 10.0) / 10.0);
        manga.setRatingCount(count);
        mangaRepository.save(manga);

        return RatingResponse.builder()
                .averageRating(manga.getAverageRating())
                .totalRatings(count)
                .userScore(score)
                .build();
    }

    public RatingResponse getRating(UUID mangaId, String username) {
        double avg = ratingRepository.findAverageScoreByMangaId(mangaId).orElse(0.0);
        long count = ratingRepository.countByMangaId(mangaId);

        Integer userScore = null;
        if (username != null) {
            userScore = userRepository.findByUsername(username)
                    .flatMap(user -> ratingRepository.findByUserIdAndMangaId(user.getId(), mangaId))
                    .map(Rating::getScore)
                    .orElse(null);
        }

        return RatingResponse.builder()
                .averageRating(Math.round(avg * 10.0) / 10.0)
                .totalRatings(count)
                .userScore(userScore)
                .build();
    }
}
