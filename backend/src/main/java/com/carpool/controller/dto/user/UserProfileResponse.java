package com.carpool.controller.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Ответ с данными запрашиваемого профиля")
public record UserProfileResponse(
        @Schema(description = "ID пользователя", example = "1")
        Long id,

        @Schema(description = "Корпоративный email", example = "user@company.com")
        String email,

        @Schema(description = "Имя пользователя", example = "Иван")
        String firstName,

        @Schema(description = "Фамилия пользователя", example = "Иванов")
        String lastName,

        @Schema(description = "Ник/id пользователя в telegram", example = "id12345")
        String telegramAlias,

        @Schema(description = "Ник/id пользователя в Вконтакте", example = "id12345")
        String vkAlias,

        @Schema(description = "Роль пользователя")
        String role
) {}