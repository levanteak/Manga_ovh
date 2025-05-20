package com.manga.ovh.controller;

import com.manga.ovh.dto.ApiResponse;
import com.manga.ovh.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/likes")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/{mangaId}")
    public ResponseEntity<ApiResponse<String>> like(
            @PathVariable UUID mangaId,
            @AuthenticationPrincipal String username
    ) {
        likeService.like(mangaId, username);
        return ResponseEntity.ok(
                new ApiResponse<>(200, "Лайк успешно добавлен (если ранее не был)", "OK")
        );
    }

    @GetMapping("/{mangaId}")
    public ResponseEntity<ApiResponse<Long>> count(@PathVariable UUID mangaId) {
        long count = likeService.countLikes(mangaId);
        return ResponseEntity.ok(
                new ApiResponse<>(200, "Количество лайков", count)
        );
    }
}
