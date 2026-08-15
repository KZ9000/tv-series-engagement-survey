package com.example.tvseriesengagementsurvey.controller;

import com.example.tvseriesengagementsurvey.dto.series.CreateSeriesRequest;
import com.example.tvseriesengagementsurvey.dto.series.SeriesResponse;
import com.example.tvseriesengagementsurvey.dto.series.UpdateSeriesRequest;
import com.example.tvseriesengagementsurvey.dto.series.UpdateSeriesStatusRequest;
import com.example.tvseriesengagementsurvey.service.SeriesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/series")
@RequiredArgsConstructor
public class SeriesController {

    private final SeriesService seriesService;

    @GetMapping
    public List<SeriesResponse> listActive() {
        return seriesService.listActiveSeries();
    }

    @GetMapping("/{id}")
    public SeriesResponse getById(@PathVariable Long id) {
        return seriesService.getSeries(id);
    }

    @PostMapping
    public ResponseEntity<SeriesResponse> create(@Valid @RequestBody CreateSeriesRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(seriesService.createSeries(request));
    }

    @PutMapping("/{id}")
    public SeriesResponse update(@PathVariable Long id,
                                 @Valid @RequestBody UpdateSeriesRequest request) {
        return seriesService.updateSeries(id, request);
    }

    @PatchMapping("/{id}/status")
    public SeriesResponse changeStatus(@PathVariable Long id,
                                       @Valid @RequestBody UpdateSeriesStatusRequest request) {
        return seriesService.setActive(id, request.active());
    }
}
