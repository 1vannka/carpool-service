package com.carpool.controller.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Ответ с парой токенов (Access и Refresh)")
public record AuthResponse(
        @Schema(description = "JWT токен для доступа к API")
        String accessToken,
        @Schema(description = "Токен для обновления access-токена")
        String refreshToken
) {
}