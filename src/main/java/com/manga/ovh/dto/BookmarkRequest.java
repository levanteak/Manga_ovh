package com.manga.ovh.dto;

import com.manga.ovh.enums.BookmarkStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BookmarkRequest {

    @NotNull(message = "Status is required")
    private BookmarkStatus status;
}
