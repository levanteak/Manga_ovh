package com.manga.ovh.repository;

import com.manga.ovh.entity.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PageRepository extends JpaRepository<Page, UUID> {
    List<Page> findByChapterId(UUID chapterId);
}
