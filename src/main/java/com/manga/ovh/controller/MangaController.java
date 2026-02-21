package com.manga.ovh.controller;

import com.manga.ovh.dto.ApiResponse;
import com.manga.ovh.dto.MangaCreateRequest;
import com.manga.ovh.dto.MangaDto;
import com.manga.ovh.dto.MangaFilter;
import com.manga.ovh.entity.Manga;
import com.manga.ovh.enums.MangaCategory;
import com.manga.ovh.enums.MangaStatus;
import com.manga.ovh.service.MangaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
        return ResponseEntity.ok(new ApiResponse<>(200, "Манга успешно добавлена", saved.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MangaDto>> get(@PathVariable UUID id) {
        return ResponseEntity.ok(new ApiResponse<>(200, "OK", mangaService.getDtoById(id)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<MangaDto>>> getAll(
            @RequestParam(required = false) MangaStatus status,
            @RequestParam(required = false) MangaCategory category,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) Integer releaseYear,
            @RequestParam(required = false) String publisherName,
            @RequestParam(required = false) UUID userId,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        MangaFilter filter = new MangaFilter();
        filter.setStatus(status);
        filter.setCategory(category);
        filter.setGenre(genre);
        filter.setTag(tag);
        filter.setCountry(country);
        filter.setReleaseYear(releaseYear);
        filter.setPublisherName(publisherName);
        filter.setUserId(userId);
        filter.setSortBy(sortBy);
        filter.setSortDir(sortDir);
        filter.setPage(page);
        filter.setSize(size);

        Page<MangaDto> result = mangaService.getFiltered(filter);
        return ResponseEntity.ok(new ApiResponse<>(200, "Список манг", result));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<MangaDto>> update(
            @PathVariable UUID id,
            @ModelAttribute MangaCreateRequest request
    ) {
        return ResponseEntity.ok(new ApiResponse<>(200, "Манга обновлена", mangaService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        mangaService.delete(id);
        return ResponseEntity.ok(new ApiResponse<>(200, "Манга удалена", null));
    }
}
