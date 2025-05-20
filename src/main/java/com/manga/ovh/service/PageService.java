package com.manga.ovh.service;

import com.manga.ovh.dto.PageResponse;
import com.manga.ovh.entity.Chapter;
import com.manga.ovh.entity.Page;
import com.manga.ovh.repository.ChapterRepository;
import com.manga.ovh.repository.PageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PageService {

    private final PageRepository pageRepository;
    private final ChapterRepository chapterRepository;
    private final S3Service s3Service;

    public List<PageResponse> uploadPages(UUID chapterId, List<MultipartFile> images) {
        Chapter chapter = chapterRepository.findById(chapterId).orElseThrow();

        List<Page> pages = new ArrayList<>();
        int pageNumber = 1;

        for (MultipartFile image : images) {
            String imageUrl = s3Service.uploadFile(image, "manga/" + chapter.getManga().getId() + "/chapter_" + chapter.getNumber());

            Page page = Page.builder()
                    .chapter(chapter)
                    .pageNumber(pageNumber++)
                    .imageUrl(imageUrl)
                    .build();

            pages.add(page);
        }

        pageRepository.saveAll(pages);

        return pages.stream()
                .map(p -> new PageResponse(p.getPageNumber(), p.getImageUrl()))
                .toList();
    }

    public List<PageResponse> getPages(UUID chapterId) {
        return pageRepository.findByChapterId(chapterId).stream()
                .map(page -> new PageResponse(page.getPageNumber(), page.getImageUrl()))
                .toList();
    }
}
