package com.manga.ovh.repository;

import com.manga.ovh.entity.Publisher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PublisherRepository extends JpaRepository<Publisher, UUID> {
    Optional<Publisher> findByName(String name);
}
