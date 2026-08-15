package com.example.tvseriesengagementsurvey.service;

import com.example.tvseriesengagementsurvey.dto.dashboard.DashboardResponse;
import com.example.tvseriesengagementsurvey.repository.RatingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final RatingRepository ratingRepository;

    @Transactional(readOnly = true)
    public List<DashboardResponse> getDashboard() {
        return ratingRepository.findDashboardStats();
    }
}
