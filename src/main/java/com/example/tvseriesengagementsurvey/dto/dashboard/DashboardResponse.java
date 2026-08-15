package com.example.tvseriesengagementsurvey.dto.dashboard;

public record DashboardResponse(
        Long seriesId,
        String title,
        Double averageScore,
        Long totalVotes
) {
}
