package com.manga.ovh.repository;

import com.manga.ovh.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, UUID> {
    List<Comment> findByMangaId(UUID mangaId);
}
