package com.manga.ovh.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "manga")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MangaDocument {

    @Id
    private String id;

    private String title;
    private String description;
    private String genres;
}
