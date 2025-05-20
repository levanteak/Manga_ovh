package com.manga.ovh.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.manga.ovh.document.MangaDocument;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MangaSearchService {

    private final ElasticsearchClient client;

    public MangaSearchService(ElasticsearchClient client) {
        this.client = client;
    }

    public List<MangaDocument> searchByTitle(String keyword) {
        try {
            SearchResponse<MangaDocument> response = client.search(s -> s
                            .index("manga")
                            .query(q -> q
                                    .match(m -> m
                                            .field("title")
                                            .query(keyword)
                                    )
                            ),
                    MangaDocument.class
            );

            return response.hits().hits().stream()
                    .map(Hit::source)
                    .collect(Collectors.toList());

        } catch (IOException e) {
            throw new RuntimeException("Elasticsearch search error", e);
        }
    }

    public String save(MangaDocument doc) {
        try {
            IndexRequest<MangaDocument> request = IndexRequest.of(i -> i
                    .index("manga")
                    .id(doc.getId())
                    .document(doc)
            );
            IndexResponse response = client.index(request);
            return response.id();
        } catch (IOException e) {
            throw new RuntimeException("Elasticsearch index error", e);
        }
    }
}
