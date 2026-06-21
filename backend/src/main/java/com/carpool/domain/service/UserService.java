package com.carpool.domain.service;

import com.carpool.domain.model.user.User;

public interface UserService {
    User getUserProfile(Long userId);
    User updateSocialAliases(Long userId, String telegramAlias, String vkAlias);
    User getUserProfileByEmail(String email);
}