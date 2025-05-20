package com.manga.ovh.entity;

import com.manga.ovh.enums.MangaCategory;
import com.manga.ovh.enums.MangaStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "manga")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Manga {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;
    private String originalTitle;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    private MangaStatus status;

    @Enumerated(EnumType.STRING)
    private MangaCategory category;

    private Integer releaseYear;
    private String country;
    private String author;
    private String artist;

    @ManyToOne
    @JoinColumn(name = "publisher_id")
    private Publisher publisher;

    private Integer views;
    private Integer likes;

    @Column(name = "cover_url")
    private String coverUrl;

    @ManyToMany
    @JoinTable(
            name = "manga_genres",
            joinColumns = @JoinColumn(name = "manga_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private Set<Genre> genres = new HashSet<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}