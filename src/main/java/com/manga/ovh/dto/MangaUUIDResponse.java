package com.manga.ovh.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.UUID;

@Data
@AllArgsConstructor
public class MangaUUIDResponse {
    private UUID uuid;
}
