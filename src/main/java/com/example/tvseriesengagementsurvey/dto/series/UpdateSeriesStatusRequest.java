package com.example.tvseriesengagementsurvey.dto.series;

import jakarta.validation.constraints.NotNull;

public record UpdateSeriesStatusRequest(
        @NotNull(message = "Active flag is required")
        Boolean active
) {
}
