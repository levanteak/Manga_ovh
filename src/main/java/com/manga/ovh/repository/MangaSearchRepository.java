package com.manga.ovh.repository;

import com.manga.ovh.document.MangaDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MangaSearchRepository extends ElasticsearchRepository<MangaDocument, String> {
    List<MangaDocument> findByTitleContainingIgnoreCase(String keyword);
}
