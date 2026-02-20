package com.manga.ovh.service;

import com.manga.ovh.dto.BookmarkRequest;
import com.manga.ovh.dto.BookmarkResponse;
import com.manga.ovh.entity.Bookmark;
import com.manga.ovh.entity.Manga;
import com.manga.ovh.entity.User;
import com.manga.ovh.enums.BookmarkStatus;
import com.manga.ovh.repository.BookmarkRepository;
import com.manga.ovh.repository.MangaRepository;
import com.manga.ovh.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final UserRepository userRepository;
    private final MangaRepository mangaRepository;

    @Transactional
    public BookmarkResponse addOrUpdate(String username, UUID mangaId, BookmarkRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        Manga manga = mangaRepository.findById(mangaId)
                .orElseThrow(() -> new RuntimeException("Манга не найдена"));

        Bookmark bookmark = bookmarkRepository.findByUserIdAndMangaId(user.getId(), mangaId)
                .orElseGet(() -> Bookmark.builder().user(user).manga(manga).build());

        bookmark.setStatus(request.getStatus());
        bookmarkRepository.save(bookmark);

        return toResponse(bookmark);
    }

    public List<BookmarkResponse> getBookmarks(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        return bookmarkRepository.findByUserIdOrderByUpdatedAtDesc(user.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<BookmarkResponse> getBookmarksByStatus(String username, BookmarkStatus status) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        return bookmarkRepository.findByUserIdAndStatusOrderByUpdatedAtDesc(user.getId(), status)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public void remove(String username, UUID mangaId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        bookmarkRepository.deleteByUserIdAndMangaId(user.getId(), mangaId);
    }

    private BookmarkResponse toResponse(Bookmark b) {
        return BookmarkResponse.builder()
                .mangaId(b.getManga().getId())
                .mangaTitle(b.getManga().getTitle())
                .coverUrl(b.getManga().getCoverUrl())
                .status(b.getStatus())
                .updatedAt(b.getUpdatedAt())
                .build();
    }
}
