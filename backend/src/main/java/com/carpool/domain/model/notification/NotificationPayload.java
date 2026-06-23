package com.carpool.domain.model.notification;

public record NotificationPayload(
        String type,
        Long tripId,
        Long passengerId,
        String message
) {}