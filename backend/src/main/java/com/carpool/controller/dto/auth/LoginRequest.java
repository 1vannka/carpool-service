package com.carpool.controller.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Запрос на авторизацию пользователя")
public record LoginRequest(
        @NotBlank(message = "Email не может быть пустым")
        @Email(message = "Некорректный формат email")
        @Schema(description = "Корпоративный email", example = "user@company.com")
        String email,

        @NotBlank(message = "Пароль не может быть пустым")
        @Schema(description = "Пароль (минимум 8 символов)")
        String password
) {}