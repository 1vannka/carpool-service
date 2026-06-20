package com.carpool.domain.model.auth;

public record TokenPair(
        String accessToken,
        String refreshToken
) {
}