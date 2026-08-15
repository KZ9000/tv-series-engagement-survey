package com.example.tvseriesengagementsurvey.dto.series;

import java.time.LocalDate;

public record SeriesResponse(
        Long id,
        String title,
        String description,
        LocalDate releaseDate,
        boolean active
) {
}
