package com.manga.ovh.service;

import com.manga.ovh.entity.Tag;
import com.manga.ovh.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;

    public Tag create(String name) {
        if (tagRepository.existsByName(name)) {
            throw new RuntimeException("Тег с именем '" + name + "' уже существует");
        }
        return tagRepository.save(Tag.builder().name(name).build());
    }

    public List<Tag> getAll() {
        return tagRepository.findAll();
    }
}
