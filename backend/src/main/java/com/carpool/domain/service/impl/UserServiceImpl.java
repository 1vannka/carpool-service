package com.carpool.domain.service.impl;

import com.carpool.domain.model.user.User;
import com.carpool.domain.repository.UserRepositoryPort;
import com.carpool.domain.service.UserService;

public class UserServiceImpl implements UserService {

    private final UserRepositoryPort userRepositoryPort;

    public UserServiceImpl(UserRepositoryPort userRepositoryPort) {
        this.userRepositoryPort = userRepositoryPort;
    }

    @Override
    public User getUserProfile(Long userId) {
        return userRepositoryPort.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
    }

    @Override
    public User updateSocialAliases(Long userId, String telegramAlias, String vkAlias) {
        User user = getUserProfile(userId);

        user.setTelegramAlias(parseTelegram(telegramAlias));
        user.setVkAlias(parseVk(vkAlias));

        return userRepositoryPort.save(user);
    }

    private String parseTelegram(String alias) {
        if (alias == null || alias.isBlank()) return null;

        String cleanAlias = alias.trim();
        if (cleanAlias.contains("t.me/")) {
            cleanAlias = cleanAlias.substring(cleanAlias.lastIndexOf("/") + 1);
        }
        if (cleanAlias.startsWith("@")) {
            cleanAlias = cleanAlias.substring(1);
        }
        return cleanAlias;
    }

    private String parseVk(String alias) {
        if (alias == null || alias.isBlank()) return null;

        String cleanAlias = alias.trim();
        if (cleanAlias.contains("vk.com/")) {
            cleanAlias = cleanAlias.substring(cleanAlias.lastIndexOf("/") + 1);
        }
        if (cleanAlias.startsWith("@")) {
            cleanAlias = cleanAlias.substring(1);
        }
        return cleanAlias;
    }
}