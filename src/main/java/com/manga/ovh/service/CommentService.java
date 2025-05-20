package com.manga.ovh.service;

import com.manga.ovh.dto.CommentResponse;
import com.manga.ovh.entity.Comment;
import com.manga.ovh.entity.Manga;
import com.manga.ovh.entity.User;
import com.manga.ovh.repository.CommentRepository;
import com.manga.ovh.repository.MangaRepository;
import com.manga.ovh.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final MangaRepository mangaRepository;
    private final UserRepository userRepository;

    public Comment addComment(UUID mangaId, String username, String content) {
        Manga manga = mangaRepository.findById(mangaId).orElseThrow();
        User user = userRepository.findByUsername(username).orElseThrow();

        Comment comment = Comment.builder()
                .manga(manga)
                .user(user)
                .content(content)
                .build();

        return commentRepository.save(comment);
    }

    public List<CommentResponse> getCommentsByManga(UUID mangaId) {
        return commentRepository.findByMangaId(mangaId)
                .stream()
                .map(c -> new CommentResponse(
                        c.getUser().getUsername(),
                        c.getContent(),
                        c.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }

}
