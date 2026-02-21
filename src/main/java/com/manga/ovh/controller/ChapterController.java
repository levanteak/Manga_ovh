package com.manga.ovh.controller;

import com.manga.ovh.dto.ApiResponse;
import com.manga.ovh.dto.ChapterResponse;
import com.manga.ovh.entity.Chapter;
import com.manga.ovh.service.ChapterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/chapters")
@RequiredArgsConstructor
public class ChapterController {

    private final ChapterService chapterService;

    @PostMapping("/{mangaId}")
    public ResponseEntity<ApiResponse<ChapterResponse>> create(@PathVariable UUID mangaId, @RequestBody Chapter chapter) {
        ChapterResponse saved = chapterService.create(mangaId, chapter);
        return ResponseEntity.ok(new ApiResponse<>(200, "Глава успешно создана", saved));
    }

    @GetMapping("/{mangaId}")
    public ResponseEntity<ApiResponse<List<ChapterResponse>>> getAll(@PathVariable UUID mangaId) {
        List<ChapterResponse> chapters = chapterService.getAllByManga(mangaId);
        return ResponseEntity.ok(new ApiResponse<>(200, "Главы успешно получены", chapters));
    }
}
