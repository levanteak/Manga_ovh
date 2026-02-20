package com.manga.ovh.controller;

import com.manga.ovh.dto.ApiResponse;
import com.manga.ovh.dto.ReadingHistoryResponse;
import com.manga.ovh.dto.ReadingProgressRequest;
import com.manga.ovh.service.ReadingHistoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/history")
@RequiredArgsConstructor
public class ReadingHistoryController {

    private final ReadingHistoryService historyService;

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> saveProgress(
            @Valid @RequestBody ReadingProgressRequest request,
            Principal principal
    ) {
        historyService.saveProgress(principal.getName(), request);
        return ResponseEntity.ok(new ApiResponse<>(200, "Прогресс сохранён", null));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ReadingHistoryResponse>>> getHistory(Principal principal) {
        List<ReadingHistoryResponse> history = historyService.getHistory(principal.getName());
        return ResponseEntity.ok(new ApiResponse<>(200, "История чтения", history));
    }

    @DeleteMapping("/{mangaId}")
    public ResponseEntity<ApiResponse<Void>> deleteProgress(
            @PathVariable UUID mangaId,
            Principal principal
    ) {
        historyService.deleteProgress(principal.getName(), mangaId);
        return ResponseEntity.ok(new ApiResponse<>(200, "Запись удалена", null));
    }
}
