package com.example.tvseriesengagementsurvey.controller;

import com.example.tvseriesengagementsurvey.config.OpenApiConfig;
import com.example.tvseriesengagementsurvey.dto.series.CreateSeriesRequest;
import com.example.tvseriesengagementsurvey.dto.series.SeriesResponse;
import com.example.tvseriesengagementsurvey.dto.series.UpdateSeriesRequest;
import com.example.tvseriesengagementsurvey.dto.series.UpdateSeriesStatusRequest;
import com.example.tvseriesengagementsurvey.service.SeriesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Series", description = "Catálogo de series. La escritura requiere rol ADMIN")
@SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME)
@RestController
@RequestMapping("/api/series")
@RequiredArgsConstructor
public class SeriesController {

    private final SeriesService seriesService;

    @Operation(summary = "Listar series activas", description = "Requiere autenticación (rol USER o ADMIN).")
    @GetMapping
    public List<SeriesResponse> listActive() {
        return seriesService.listActiveSeries();
    }

    @Operation(summary = "Obtener una serie por id", description = "Requiere autenticación (rol USER o ADMIN).")
    @GetMapping("/{id}")
    public SeriesResponse getById(
            @Parameter(description = "Id de la serie") @PathVariable Long id) {
        return seriesService.getSeries(id);
    }

    @Operation(summary = "Crear una serie", description = "Requiere rol ADMIN. Devuelve 201 Created.")
    @PostMapping
    public ResponseEntity<SeriesResponse> create(@Valid @RequestBody CreateSeriesRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(seriesService.createSeries(request));
    }

    @Operation(summary = "Actualizar una serie", description = "Requiere rol ADMIN.")
    @PutMapping("/{id}")
    public SeriesResponse update(
            @Parameter(description = "Id de la serie") @PathVariable Long id,
            @Valid @RequestBody UpdateSeriesRequest request) {
        return seriesService.updateSeries(id, request);
    }

    @Operation(summary = "Activar o desactivar una serie",
            description = "Requiere rol ADMIN. Una serie inactiva no puede recibir nuevas calificaciones.")
    @PatchMapping("/{id}/status")
    public SeriesResponse changeStatus(
            @Parameter(description = "Id de la serie") @PathVariable Long id,
            @Valid @RequestBody UpdateSeriesStatusRequest request) {
        return seriesService.setActive(id, request.active());
    }
}
