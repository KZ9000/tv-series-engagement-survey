package com.example.tvseriesengagementsurvey.exception;

import java.time.Instant;

public record ErrorResponse(Instant timestamp, int status, String message) {
}
