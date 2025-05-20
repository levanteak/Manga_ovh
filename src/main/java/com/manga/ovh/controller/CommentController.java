package com.manga.ovh.controller;

import com.manga.ovh.dto.ApiResponse;
import com.manga.ovh.dto.CommentResponse;
import com.manga.ovh.entity.Comment;
import com.manga.ovh.service.CommentService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/{mangaId}")
    public ResponseEntity<ApiResponse<Comment>> addComment(
            @PathVariable UUID mangaId,
            @AuthenticationPrincipal String username,
            @RequestBody CommentRequest request
    ) {
        Comment saved = commentService.addComment(mangaId, username, request.getContent());
        return ResponseEntity.ok(new ApiResponse<>(200, "Комментарий успешно добавлен", saved));
    }

    @GetMapping("/{mangaId}")
    public ResponseEntity<ApiResponse<List<CommentResponse>>> getComments(@PathVariable UUID mangaId) {
        List<CommentResponse> comments = commentService.getCommentsByManga(mangaId);
        return ResponseEntity.ok(new ApiResponse<>(200, "Комментарии успешно получены", comments));
    }

    @Data
    static class CommentRequest {
        private String content;
    }
}
