package com.example.tvseriesengagementsurvey.dto.rating;

import java.time.Instant;

public record RatingResponse(
        Long id,
        Long seriesId,
        Integer score,
        Instant createdAt
) {
}
