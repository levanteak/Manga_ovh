package com.manga.ovh.controller;

import com.manga.ovh.dto.ApiResponse;
import com.manga.ovh.entity.Tag;
import com.manga.ovh.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Tag>> create(@RequestParam String name) {
        Tag tag = tagService.create(name);
        return ResponseEntity.ok(new ApiResponse<>(200, "Тег создан", tag));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Tag>>> getAll() {
        return ResponseEntity.ok(new ApiResponse<>(200, "Теги", tagService.getAll()));
    }
}
