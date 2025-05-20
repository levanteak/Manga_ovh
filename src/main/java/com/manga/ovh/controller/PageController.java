package com.manga.ovh.controller;

import com.manga.ovh.dto.ApiResponse;
import com.manga.ovh.dto.PageResponse;
import com.manga.ovh.entity.Page;
import com.manga.ovh.service.PageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/pages")
@RequiredArgsConstructor
public class PageController {

    private final PageService pageService;

    @PostMapping("/{chapterId}")
    public ResponseEntity<ApiResponse<List<PageResponse>>> upload(
            @PathVariable UUID chapterId,
            @RequestPart("images") List<MultipartFile> images
    ) {
        List<PageResponse> uploaded = pageService.uploadPages(chapterId, images);
        return ResponseEntity.ok(
                new ApiResponse<>(200, "Страницы успешно загружены", uploaded)
        );
    }

    @GetMapping("/{chapterId}")
    public ResponseEntity<ApiResponse<List<PageResponse>>> get(@PathVariable UUID chapterId) {
        List<PageResponse> pages = pageService.getPages(chapterId);
        return ResponseEntity.ok(
                new ApiResponse<>(200, "Страницы успешно получены", pages)
        );
    }
}
