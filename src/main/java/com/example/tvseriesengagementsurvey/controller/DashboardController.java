package com.example.tvseriesengagementsurvey.controller;

import com.example.tvseriesengagementsurvey.config.OpenApiConfig;
import com.example.tvseriesengagementsurvey.dto.dashboard.DashboardResponse;
import com.example.tvseriesengagementsurvey.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Dashboard", description = "Métricas de engagement por serie")
@SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME)
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @Operation(summary = "Obtener dashboard",
            description = "Requiere autenticación. Devuelve el promedio de calificación y la cantidad "
                    + "de votos por serie, calculados a partir de la tabla de ratings.")
    @GetMapping
    public List<DashboardResponse> getDashboard() {
        return dashboardService.getDashboard();
    }
}
