package com.manga.ovh.controller;

import com.manga.ovh.dto.ApiResponse;
import com.manga.ovh.dto.BookmarkRequest;
import com.manga.ovh.dto.BookmarkResponse;
import com.manga.ovh.enums.BookmarkStatus;
import com.manga.ovh.service.BookmarkService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/bookmarks")
@RequiredArgsConstructor
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @PostMapping("/{mangaId}")
    public ResponseEntity<ApiResponse<BookmarkResponse>> addOrUpdate(
            @PathVariable UUID mangaId,
            @Valid @RequestBody BookmarkRequest request,
            Principal principal
    ) {
        BookmarkResponse response = bookmarkService.addOrUpdate(principal.getName(), mangaId, request);
        return ResponseEntity.ok(new ApiResponse<>(200, "Закладка обновлена", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<BookmarkResponse>>> getBookmarks(
            @RequestParam(required = false) BookmarkStatus status,
            Principal principal
    ) {
        List<BookmarkResponse> bookmarks = status != null
                ? bookmarkService.getBookmarksByStatus(principal.getName(), status)
                : bookmarkService.getBookmarks(principal.getName());
        return ResponseEntity.ok(new ApiResponse<>(200, "Закладки", bookmarks));
    }

    @DeleteMapping("/{mangaId}")
    public ResponseEntity<ApiResponse<Void>> remove(
            @PathVariable UUID mangaId,
            Principal principal
    ) {
        bookmarkService.remove(principal.getName(), mangaId);
        return ResponseEntity.ok(new ApiResponse<>(200, "Закладка удалена", null));
    }
}
