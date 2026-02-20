package com.manga.ovh.repository;

import com.manga.ovh.entity.Manga;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface MangaRepository extends JpaRepository<Manga, UUID>, JpaSpecificationExecutor<Manga> {
}
