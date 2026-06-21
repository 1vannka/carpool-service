package com.carpool.controller.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Запрос на обновление токена")
public record RefreshRequest(
        @NotBlank(message = "Refresh token не может быть пустым")
        @Schema(description = "Токен для обновления access-токена")
        String refreshToken
) {}