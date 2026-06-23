package com.carpool.domain.service;

public interface NotificationService {
    void sendNotification(Long userId, String type, Long tripId, Long passengerId, String message);
}