package com.example.tvseriesengagementsurvey.service;

import com.example.tvseriesengagementsurvey.dto.series.CreateSeriesRequest;
import com.example.tvseriesengagementsurvey.dto.series.SeriesResponse;
import com.example.tvseriesengagementsurvey.dto.series.UpdateSeriesRequest;
import com.example.tvseriesengagementsurvey.entity.Series;
import com.example.tvseriesengagementsurvey.exception.ResourceNotFoundException;
import com.example.tvseriesengagementsurvey.repository.SeriesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SeriesService {

    private final SeriesRepository seriesRepository;

    @Transactional(readOnly = true)
    public List<SeriesResponse> listActiveSeries() {
        return seriesRepository.findByActiveTrueOrderByTitleAsc().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public SeriesResponse getSeries(Long id) {
        return toResponse(findSeries(id));
    }

    @Transactional
    public SeriesResponse createSeries(CreateSeriesRequest request) {
        Series series = new Series();
        series.setTitle(request.title());
        series.setDescription(request.description());
        series.setReleaseDate(request.releaseDate());
        series.setActive(true);
        return toResponse(seriesRepository.save(series));
    }

    @Transactional
    public SeriesResponse updateSeries(Long id, UpdateSeriesRequest request) {
        Series series = findSeries(id);
        series.setTitle(request.title());
        series.setDescription(request.description());
        series.setReleaseDate(request.releaseDate());
        return toResponse(seriesRepository.save(series));
    }

    @Transactional
    public SeriesResponse setActive(Long id, boolean active) {
        Series series = findSeries(id);
        series.setActive(active);
        return toResponse(seriesRepository.save(series));
    }

    private Series findSeries(Long id) {
        return seriesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Series not found with id: " + id));
    }

    private SeriesResponse toResponse(Series series) {
        return new SeriesResponse(
                series.getId(),
                series.getTitle(),
                series.getDescription(),
                series.getReleaseDate(),
                series.isActive());
    }
}
