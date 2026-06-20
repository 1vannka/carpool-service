package com.carpool.controller.notification;

import com.carpool.domain.service.UserService;
import com.carpool.infrastructure.service.SseSubscriptionManager;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final SseSubscriptionManager sseSubscriptionManager;
    private final UserService userService;

    public NotificationController(SseSubscriptionManager sseSubscriptionManager, UserService userService) {
        this.sseSubscriptionManager = sseSubscriptionManager;
        this.userService = userService;
    }

    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = userService.getUserProfileByEmail(userDetails.getUsername()).getId();
        return sseSubscriptionManager.subscribe(userId);
    }
}