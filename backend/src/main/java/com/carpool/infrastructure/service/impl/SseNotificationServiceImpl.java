package com.carpool.infrastructure.service.impl;

import com.carpool.domain.service.NotificationService;
import com.carpool.infrastructure.service.SseSubscriptionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class SseNotificationServiceImpl implements NotificationService, SseSubscriptionManager {
    private static final Logger log = LoggerFactory.getLogger(SseNotificationServiceImpl.class);
    private final ConcurrentMap<Long, ConcurrentMap<String, SseEmitter>> userEmitters = new ConcurrentHashMap<>();

    public SseEmitter subscribe(Long userId) {
        String connectionId = UUID.randomUUID().toString();
        SseEmitter emitter = new SseEmitter(60 * 60 * 1000L);

        userEmitters.computeIfAbsent(userId, k -> new ConcurrentHashMap<>()).put(connectionId, emitter);

        emitter.onCompletion(() -> removeEmitter(userId, connectionId));
        emitter.onTimeout(() -> removeEmitter(userId, connectionId));
        emitter.onError((e) -> removeEmitter(userId, connectionId));

        try {
            emitter.send(SseEmitter.event().name("INIT").data("Connected successfully"));
        } catch (IOException e) {
            removeEmitter(userId, connectionId);
        }

        log.info("Пользователь {} подключился к SSE (Соединение: {})", userId, connectionId);
        return emitter;
    }

    private void removeEmitter(Long userId, String connectionId) {
        userEmitters.computeIfPresent(userId, (id, connections) -> {
            connections.remove(connectionId);
            return connections.isEmpty() ? null : connections;
        });
    }

    @Override
    public void sendNotification(Long userId, String message) {
        ConcurrentMap<String, SseEmitter> connections = userEmitters.get(userId);
        if (connections != null) {
            connections.forEach((connectionId, emitter) -> {
                try {
                    emitter.send(SseEmitter.event().name("NOTIFICATION").data(message));
                } catch (IOException e) {
                    removeEmitter(userId, connectionId);
                }
            });
        }
    }

    @Async
    @Scheduled(fixedRate = 20000)
    public void sendKeepAlivePings() {
        userEmitters.forEach((userId, connections) -> {
            connections.forEach((connectionId, emitter) -> {
                try {
                    emitter.send(SseEmitter.event().name("PING").data("keep-alive"));
                } catch (IOException e) {
                    removeEmitter(userId, connectionId);
                }
            });
        });
    }
}