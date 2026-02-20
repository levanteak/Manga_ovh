package com.manga.ovh.service;

import com.manga.ovh.dto.ReadingHistoryResponse;
import com.manga.ovh.dto.ReadingProgressRequest;
import com.manga.ovh.entity.Chapter;
import com.manga.ovh.entity.Manga;
import com.manga.ovh.entity.ReadingHistory;
import com.manga.ovh.entity.User;
import com.manga.ovh.repository.ChapterRepository;
import com.manga.ovh.repository.MangaRepository;
import com.manga.ovh.repository.ReadingHistoryRepository;
import com.manga.ovh.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReadingHistoryService {

    private final ReadingHistoryRepository historyRepository;
    private final UserRepository userRepository;
    private final MangaRepository mangaRepository;
    private final ChapterRepository chapterRepository;

    @Transactional
    public void saveProgress(String username, ReadingProgressRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        Manga manga = mangaRepository.findById(request.getMangaId())
                .orElseThrow(() -> new RuntimeException("Манга не найдена"));
        Chapter chapter = chapterRepository.findById(request.getChapterId())
                .orElseThrow(() -> new RuntimeException("Глава не найдена"));

        ReadingHistory history = historyRepository
                .findByUserIdAndMangaId(user.getId(), manga.getId())
                .orElseGet(() -> ReadingHistory.builder().user(user).manga(manga).build());

        history.setChapter(chapter);
        history.setLastPage(request.getLastPage());
        historyRepository.save(history);
    }

    public List<ReadingHistoryResponse> getHistory(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        return historyRepository.findByUserIdOrderByUpdatedAtDesc(user.getId())
                .stream()
                .map(h -> ReadingHistoryResponse.builder()
                        .mangaId(h.getManga().getId())
                        .mangaTitle(h.getManga().getTitle())
                        .coverUrl(h.getManga().getCoverUrl())
                        .chapterId(h.getChapter() != null ? h.getChapter().getId() : null)
                        .chapterNumber(h.getChapter() != null ? h.getChapter().getNumber() : null)
                        .lastPage(h.getLastPage())
                        .updatedAt(h.getUpdatedAt())
                        .build())
                .toList();
    }

    @Transactional
    public void deleteProgress(String username, UUID mangaId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        historyRepository.deleteByUserIdAndMangaId(user.getId(), mangaId);
    }
}
