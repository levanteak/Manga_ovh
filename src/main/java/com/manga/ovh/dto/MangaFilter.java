package com.manga.ovh.dto;

import com.manga.ovh.enums.MangaCategory;
import com.manga.ovh.enums.MangaStatus;
import lombok.Data;

@Data
public class MangaFilter {
    private MangaStatus status;
    private MangaCategory category;
    private String genre;
    private String tag;
    private String country;
    private Integer releaseYear;
    private String sortBy = "createdAt";   // createdAt | views | averageRating | title
    private String sortDir = "DESC";       // ASC | DESC
    private int page = 0;
    private int size = 20;
}
