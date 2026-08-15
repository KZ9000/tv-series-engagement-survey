package com.example.tvseriesengagementsurvey.repository;

import com.example.tvseriesengagementsurvey.dto.dashboard.DashboardResponse;
import com.example.tvseriesengagementsurvey.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RatingRepository extends JpaRepository<Rating, Long> {

    boolean existsByUserIdAndSeriesId(Long userId, Long seriesId);

    @Query("SELECT new com.example.tvseriesengagementsurvey.dto.dashboard.DashboardResponse("
            + "r.series.id, r.series.title, AVG(r.score), COUNT(r)) "
            + "FROM Rating r "
            + "GROUP BY r.series.id, r.series.title "
            + "ORDER BY r.series.title ASC")
    List<DashboardResponse> findDashboardStats();
}
