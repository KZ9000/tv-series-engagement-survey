package com.example.tvseriesengagementsurvey.controller;

import com.example.tvseriesengagementsurvey.dto.rating.CreateRatingRequest;
import com.example.tvseriesengagementsurvey.dto.rating.RatingResponse;
import com.example.tvseriesengagementsurvey.service.RatingService;
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

@RestController
@RequestMapping("/api/ratings")
@RequiredArgsConstructor
public class RatingController {

    private final RatingService ratingService;

    @PostMapping
    public ResponseEntity<RatingResponse> create(
            @Valid @RequestBody CreateRatingRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        RatingResponse response = ratingService.createRating(userDetails.getUsername(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
