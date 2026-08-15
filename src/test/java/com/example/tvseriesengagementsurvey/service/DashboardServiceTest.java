package com.example.tvseriesengagementsurvey.service;

import com.example.tvseriesengagementsurvey.dto.dashboard.DashboardResponse;
import com.example.tvseriesengagementsurvey.repository.RatingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private RatingRepository ratingRepository;

    @InjectMocks
    private DashboardService dashboardService;

    @Test
    void calcularDashboard_retornaPromedioYTotalVotosPorSerie() {
        List<DashboardResponse> expected = List.of(
                new DashboardResponse(1L, "Serie A", 4.6, 120L),
                new DashboardResponse(2L, "Serie B", 4.2, 95L));
        when(ratingRepository.findDashboardStats()).thenReturn(expected);

        List<DashboardResponse> result = dashboardService.getDashboard();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).averageScore()).isEqualTo(4.6);
        assertThat(result.get(0).totalVotes()).isEqualTo(120L);
        assertThat(result.get(1).title()).isEqualTo("Serie B");
    }
}
