package com.manga.ovh.controller;

import com.manga.ovh.dto.ApiResponse;
import com.manga.ovh.dto.RatingRequest;
import com.manga.ovh.dto.RatingResponse;
import com.manga.ovh.service.RatingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/api/ratings")
@RequiredArgsConstructor
public class RatingController {

    private final RatingService ratingService;

    @PostMapping("/{mangaId}")
    public ResponseEntity<ApiResponse<RatingResponse>> rate(
            @PathVariable UUID mangaId,
            @Valid @RequestBody RatingRequest request,
            Principal principal
    ) {
        RatingResponse response = ratingService.rate(mangaId, principal.getName(), request.getScore());
        return ResponseEntity.ok(new ApiResponse<>(200, "Оценка сохранена", response));
    }

    @GetMapping("/{mangaId}")
    public ResponseEntity<ApiResponse<RatingResponse>> getRating(
            @PathVariable UUID mangaId,
            Principal principal
    ) {
        String username = principal != null ? principal.getName() : null;
        RatingResponse response = ratingService.getRating(mangaId, username);
        return ResponseEntity.ok(new ApiResponse<>(200, "OK", response));
    }
}
