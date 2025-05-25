package com.manga.ovh.controller;

import com.manga.ovh.dto.ApiResponse;
import com.manga.ovh.dto.MangaCreateRequest;
import com.manga.ovh.dto.MangaDto;
import com.manga.ovh.entity.Manga;
import com.manga.ovh.service.MangaService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    public ResponseEntity<ApiResponse<MangaDto>> get(@PathVariable UUID id) {
        return ResponseEntity.ok(new ApiResponse<>(200, "OK", mangaService.getDtoById(id)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<MangaDto>>> getAll() {
        return ResponseEntity.ok(
                new ApiResponse<>(200, "Список манг", mangaService.getAll())
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<MangaDto>> update(
            @PathVariable UUID id,
            @ModelAttribute MangaCreateRequest request
    ) {
        return ResponseEntity.ok(
                new ApiResponse<>(200, "Манга обновлена", mangaService.update(id, request))
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        mangaService.delete(id);
        return ResponseEntity.ok(new ApiResponse<>(200, "Манга удалена", null));
    }
}
