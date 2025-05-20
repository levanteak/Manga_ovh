package com.manga.ovh.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PageResponse {
    private int pageNumber;
    private String imageUrl;
}
