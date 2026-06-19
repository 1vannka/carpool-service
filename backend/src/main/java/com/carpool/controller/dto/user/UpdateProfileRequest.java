package com.carpool.controller.dto.user;

import jakarta.annotation.Nullable;

public record UpdateProfileRequest(
        @Nullable
        String telegramAlias,

        @Nullable
        String vkAlias
) {}