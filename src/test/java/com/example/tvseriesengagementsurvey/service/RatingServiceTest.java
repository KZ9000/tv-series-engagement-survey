package com.example.tvseriesengagementsurvey.service;

import com.example.tvseriesengagementsurvey.dto.rating.CreateRatingRequest;
import com.example.tvseriesengagementsurvey.dto.rating.RatingResponse;
import com.example.tvseriesengagementsurvey.entity.Rating;
import com.example.tvseriesengagementsurvey.entity.Role;
import com.example.tvseriesengagementsurvey.entity.Series;
import com.example.tvseriesengagementsurvey.entity.User;
import com.example.tvseriesengagementsurvey.exception.DuplicateRatingException;
import com.example.tvseriesengagementsurvey.exception.InactiveSeriesException;
import com.example.tvseriesengagementsurvey.exception.ResourceNotFoundException;
import com.example.tvseriesengagementsurvey.repository.RatingRepository;
import com.example.tvseriesengagementsurvey.repository.SeriesRepository;
import com.example.tvseriesengagementsurvey.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RatingServiceTest {

    @Mock
    private RatingRepository ratingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SeriesRepository seriesRepository;

    @InjectMocks
    private RatingService ratingService;

    private User user;
    private Series series;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("user@test.com");
        user.setPassword("encoded");
        user.setRole(Role.USER);

        series = new Series();
        series.setId(10L);
        series.setTitle("Serie A");
        series.setActive(true);
    }

    @Test
    void crearRating_conSerieActivaYUsuarioSinVoto_retornaRatingCreado() {
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
        when(seriesRepository.findById(10L)).thenReturn(Optional.of(series));
        when(ratingRepository.existsByUserIdAndSeriesId(1L, 10L)).thenReturn(false);
        Rating saved = new Rating(5, user, series);
        saved.setId(100L);
        when(ratingRepository.save(any(Rating.class))).thenReturn(saved);

        RatingResponse response = ratingService.createRating(
                "user@test.com", new CreateRatingRequest(10L, 5));

        assertThat(response.id()).isEqualTo(100L);
        assertThat(response.seriesId()).isEqualTo(10L);
        assertThat(response.score()).isEqualTo(5);
        verify(ratingRepository).save(any(Rating.class));
    }

    @Test
    void crearRating_conScoreInvalido_lanzaIllegalArgumentException() {
        assertThatThrownBy(() -> ratingService.createRating(
                "user@test.com", new CreateRatingRequest(10L, 7)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("between 1 and 5");
        verify(ratingRepository, never()).save(any(Rating.class));
    }

    @Test
    void crearRating_duplicado_lanzaDuplicateRatingException() {
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
        when(seriesRepository.findById(10L)).thenReturn(Optional.of(series));
        when(ratingRepository.existsByUserIdAndSeriesId(1L, 10L)).thenReturn(true);

        assertThatThrownBy(() -> ratingService.createRating(
                "user@test.com", new CreateRatingRequest(10L, 4)))
                .isInstanceOf(DuplicateRatingException.class);
        verify(ratingRepository, never()).save(any(Rating.class));
    }

    @Test
    void crearRating_serieInexistente_lanzaResourceNotFoundException() {
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
        when(seriesRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ratingService.createRating(
                "user@test.com", new CreateRatingRequest(99L, 5)))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void crearRating_serieInactiva_lanzaInactiveSeriesException() {
        series.setActive(false);
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
        when(seriesRepository.findById(10L)).thenReturn(Optional.of(series));

        assertThatThrownBy(() -> ratingService.createRating(
                "user@test.com", new CreateRatingRequest(10L, 5)))
                .isInstanceOf(InactiveSeriesException.class);
    }
}
