package com.manga.ovh.repository;

import com.manga.ovh.dto.MangaFilter;
import com.manga.ovh.entity.Genre;
import com.manga.ovh.entity.Manga;
import com.manga.ovh.entity.Tag;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class MangaSpecification {

    public static Specification<Manga> withFilter(MangaFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), filter.getStatus()));
            }

            if (filter.getCategory() != null) {
                predicates.add(cb.equal(root.get("category"), filter.getCategory()));
            }

            if (filter.getCountry() != null && !filter.getCountry().isBlank()) {
                predicates.add(cb.equal(cb.lower(root.get("country")), filter.getCountry().toLowerCase()));
            }

            if (filter.getReleaseYear() != null) {
                predicates.add(cb.equal(root.get("releaseYear"), filter.getReleaseYear()));
            }

            if (filter.getGenre() != null && !filter.getGenre().isBlank()) {
                Join<Manga, Genre> genreJoin = root.join("genres", JoinType.INNER);
                predicates.add(cb.equal(cb.lower(genreJoin.get("name")), filter.getGenre().toLowerCase()));
            }

            if (filter.getTag() != null && !filter.getTag().isBlank()) {
                Join<Manga, Tag> tagJoin = root.join("tags", JoinType.INNER);
                predicates.add(cb.equal(cb.lower(tagJoin.get("name")), filter.getTag().toLowerCase()));
            }

            // distinct нужен из-за JOIN по genres/tags — иначе дублируются строки
            if (query != null) {
                query.distinct(true);
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
