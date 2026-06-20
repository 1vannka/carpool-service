package com.carpool.domain.service;

import com.carpool.domain.model.auth.TokenPair;
import com.carpool.domain.model.user.User;

public interface AuthService {
    TokenPair register(User user, String rawPassword);
    TokenPair login(String email, String rawPassword);
    TokenPair refreshTokens(String refreshToken);
    void logout(Long userId, String refreshToken);
    void logoutAll(Long userId);
}