package com.manga.ovh.controller;

import com.manga.ovh.dto.ApiResponse;
import com.manga.ovh.dto.MangaCreateRequest;
import com.manga.ovh.entity.Manga;
import com.manga.ovh.service.MangaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/manga")
@RequiredArgsConstructor
public class MangaController {

    private final MangaService mangaService;

    @PostMapping
    public ResponseEntity<ApiResponse<UUID>> create(@ModelAttribute MangaCreateRequest request) {
        Manga saved = mangaService.create(request);
        return ResponseEntity.ok(
                new ApiResponse<>(200, "Манга успешно добавлена", saved.getId())
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Manga>> get(@PathVariable UUID id) {
        Manga manga = mangaService.getById(id);
        return ResponseEntity.ok(
                new ApiResponse<>(200, "Данные о манге получены", manga)
        );
    }
}
