package com.manga.ovh.service;

import com.manga.ovh.entity.Like;
import com.manga.ovh.entity.Manga;
import com.manga.ovh.entity.User;
import com.manga.ovh.repository.LikeRepository;
import com.manga.ovh.repository.MangaRepository;
import com.manga.ovh.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final MangaRepository mangaRepository;
    private final UserRepository userRepository;

    public void like(UUID mangaId, String username) {
        Manga manga = mangaRepository.findById(mangaId).orElseThrow();
        User user = userRepository.findByUsername(username).orElseThrow();

        boolean alreadyLiked = likeRepository.existsByUserIdAndMangaId(user.getId(), mangaId);
        if (!alreadyLiked) {
            likeRepository.save(Like.builder().user(user).manga(manga).build());
        }
    }

    public long countLikes(UUID mangaId) {
        return likeRepository.countByMangaId(mangaId);
    }
}
