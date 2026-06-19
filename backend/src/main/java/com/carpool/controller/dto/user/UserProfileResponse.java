package com.carpool.controller.dto.user;

public record UserProfileResponse(
        Long id,
        String email,
        String firstName,
        String lastName,
        String telegramAlias,
        String vkAlias,
        String role
) {}