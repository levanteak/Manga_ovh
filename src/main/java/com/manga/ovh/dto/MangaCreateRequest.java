package com.manga.ovh.dto;

import com.manga.ovh.enums.MangaCategory;
import com.manga.ovh.enums.MangaStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class MangaCreateRequest {

    @NotNull(message = "Cover is required")
    private MultipartFile cover;

    @NotNull(message = "Title is required")
    @Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters")
    private String title;

    private String originalTitle;

    @NotNull(message = "Description is required")
    @Size(min = 1, message = "Description must not be empty")
    private String description;

    @NotNull(message = "Status is required")
    private MangaStatus status;

    @NotNull(message = "Category is required")
    private MangaCategory category;

    @NotNull(message = "Release year is required")
    private Integer releaseYear;

    @NotNull(message = "Country is required")
    private String country;

    @NotNull(message = "Author is required")
    private String author;

    @NotNull(message = "Artist is required")
    private String artist;

    @NotNull(message = "Publisher name is required")
    private String publisherName;

    @NotNull(message = "Genres are required")
    private List<String> genres;
}
