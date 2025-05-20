package com.manga.ovh.controller;

import com.manga.ovh.document.MangaDocument;
import com.manga.ovh.service.MangaSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class MangaSearchController {

    private final MangaSearchService mangaSearchService;

    @PostMapping("/add")
    public String save(@RequestBody MangaDocument doc) {
        return mangaSearchService.save(doc);
    }

    @GetMapping
    public List<MangaDocument> search(@RequestParam String keyword) {
        return mangaSearchService.searchByTitle(keyword);
    }
}
