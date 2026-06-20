package com.carpool.controller.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record RefreshRequest(
        @NotBlank(message = "Refresh token не может быть пустым")
        String refreshToken
) {}