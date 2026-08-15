package com.example.tvseriesengagementsurvey.service;

import com.example.tvseriesengagementsurvey.dto.rating.CreateRatingRequest;
import com.example.tvseriesengagementsurvey.dto.rating.RatingResponse;
import com.example.tvseriesengagementsurvey.entity.Rating;
import com.example.tvseriesengagementsurvey.entity.Series;
import com.example.tvseriesengagementsurvey.entity.User;
import com.example.tvseriesengagementsurvey.exception.DuplicateRatingException;
import com.example.tvseriesengagementsurvey.exception.InactiveSeriesException;
import com.example.tvseriesengagementsurvey.exception.ResourceNotFoundException;
import com.example.tvseriesengagementsurvey.repository.RatingRepository;
import com.example.tvseriesengagementsurvey.repository.SeriesRepository;
import com.example.tvseriesengagementsurvey.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RatingService {

    private static final int MIN_SCORE = 1;
    private static final int MAX_SCORE = 5;

    private final RatingRepository ratingRepository;
    private final UserRepository userRepository;
    private final SeriesRepository seriesRepository;

    @Transactional
    public RatingResponse createRating(String userEmail, CreateRatingRequest request) {
        if (request.score() == null
                || request.score() < MIN_SCORE
                || request.score() > MAX_SCORE) {
            throw new IllegalArgumentException(
                    "Score must be between " + MIN_SCORE + " and " + MAX_SCORE);
        }
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with email: " + userEmail));
        Series series = seriesRepository.findById(request.seriesId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Series not found with id: " + request.seriesId()));

        if (!series.isActive()) {
            throw new InactiveSeriesException("Series is not active: " + series.getId());
        }
        if (ratingRepository.existsByUserIdAndSeriesId(user.getId(), series.getId())) {
            throw new DuplicateRatingException(
                    "User has already rated series: " + series.getId());
        }

        Rating saved = ratingRepository.save(new Rating(request.score(), user, series));
        return new RatingResponse(
                saved.getId(), saved.getSeries().getId(), saved.getScore(), saved.getCreatedAt());
    }
}
