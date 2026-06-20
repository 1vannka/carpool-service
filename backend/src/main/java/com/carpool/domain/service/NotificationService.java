package com.carpool.domain.service;

public interface NotificationService {
    void sendNotification(Long userId, String message);
}