package com.example.tvseriesengagementsurvey.repository;

import com.example.tvseriesengagementsurvey.entity.Series;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeriesRepository extends JpaRepository<Series, Long> {

    List<Series> findByActiveTrueOrderByTitleAsc();
}
