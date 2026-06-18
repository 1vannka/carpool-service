package com.carpool.controller.dto.auth;

public record LoginRequest(
        String email,
        String password
) {}