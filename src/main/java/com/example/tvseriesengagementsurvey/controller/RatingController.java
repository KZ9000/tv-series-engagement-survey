package com.example.tvseriesengagementsurvey.controller;

import com.example.tvseriesengagementsurvey.config.OpenApiConfig;
import com.example.tvseriesengagementsurvey.dto.rating.CreateRatingRequest;
import com.example.tvseriesengagementsurvey.dto.rating.RatingResponse;
import com.example.tvseriesengagementsurvey.service.RatingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Ratings", description = "Calificaciones de usuarios sobre series")
@SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME)
@RestController
@RequestMapping("/api/ratings")
@RequiredArgsConstructor
public class RatingController {

    private final RatingService ratingService;

    @Operation(summary = "Crear una calificación",
            description = "Requiere autenticación. El usuario se obtiene del JWT, nunca del body. "
                    + "Score de 1 a 5. Un usuario solo puede calificar una vez cada serie (409 duplicado) "
                    + "y la serie debe estar activa (409) y existir (404). Devuelve 201 Created.")
    @PostMapping
    public ResponseEntity<RatingResponse> create(
            @Valid @RequestBody CreateRatingRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        RatingResponse response = ratingService.createRating(userDetails.getUsername(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
