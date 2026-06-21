package com.carpool.controller.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Size;

@Schema(description = "Запрос на изменение данных профиля")
public record UpdateProfileRequest(
        @Nullable
        @Size(max = 100, message = "Telegram alias слишком длинный")
        @Schema(description = "Ник/id пользователя в telegram", example = "id12345")
        String telegramAlias,

        @Nullable
        @Size(max = 100, message = "VK alias слишком длинный")
        @Schema(description = "Ник/id пользователя в Вконтакте", example = "id12345")
        String vkAlias
) {}