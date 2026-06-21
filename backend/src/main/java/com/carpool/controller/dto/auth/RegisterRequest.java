package com.carpool.controller.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Запрос на регистрацию нового пользователя")
public record RegisterRequest(
        @NotBlank(message = "Email не может быть пустым")
        @Email(message = "Некорректный формат email")
        @Schema(description = "Корпоративный email", example = "user@company.com")
        String email,

        @NotBlank(message = "Пароль не может быть пустым")
        @Size(min = 8, message = "Пароль должен содержать минимум 8 символов")
        @Schema(description = "Пароль (минимум 8 символов)")
        String password,

        @NotBlank(message = "Имя не может быть пустым")
        @Schema(description = "Имя пользователя", example = "Иван")
        String firstName,

        @NotBlank(message = "Фамилия не может быть пустой")
        @Schema(description = "Фамилия пользователя", example = "Иванов")
        String lastName
) {}