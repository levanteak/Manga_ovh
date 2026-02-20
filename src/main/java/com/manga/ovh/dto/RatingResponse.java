package com.manga.ovh.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RatingResponse {
    private Double averageRating;
    private Long totalRatings;
    private Integer userScore;
}
