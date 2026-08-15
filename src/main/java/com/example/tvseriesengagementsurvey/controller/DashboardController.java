package com.example.tvseriesengagementsurvey.controller;

import com.example.tvseriesengagementsurvey.dto.dashboard.DashboardResponse;
import com.example.tvseriesengagementsurvey.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public List<DashboardResponse> getDashboard() {
        return dashboardService.getDashboard();
    }
}
