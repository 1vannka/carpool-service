package com.carpool.domain.service;

import com.carpool.domain.model.user.User;

public interface AuthService {
    String register(User user, String rawPassword);

    String login(String email, String rawPassword);
}