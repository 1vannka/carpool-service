package com.carpool.controller.dto.user;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(
        @Nullable
        @Size(max = 100, message = "Telegram alias слишком длинный")
        String telegramAlias,

        @Nullable
        @Size(max = 100, message = "VK alias слишком длинный")
        String vkAlias
) {}