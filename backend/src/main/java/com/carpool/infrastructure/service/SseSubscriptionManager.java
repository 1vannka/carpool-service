package com.carpool.infrastructure.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface SseSubscriptionManager {
    SseEmitter subscribe(Long userId);
    void sendKeepAlivePings();
}