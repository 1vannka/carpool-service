package com.carpool.controller.notification;

import com.carpool.domain.service.UserService;
import com.carpool.infrastructure.service.SseSubscriptionManager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/notifications")
@Tag(name = "Notifications", description = "Server-Sent Events (SSE) подписка на пуши")
public class NotificationController {

    private final SseSubscriptionManager sseSubscriptionManager;
    private final UserService userService;

    public NotificationController(SseSubscriptionManager sseSubscriptionManager, UserService userService) {
        this.sseSubscriptionManager = sseSubscriptionManager;
        this.userService = userService;
    }

    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "Подключиться к потоку уведомлений", description = "Открывает долгоживущее SSE соединение. Клиент должен слушать события 'INIT', 'NOTIFICATION' и 'PING'")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Подключение установлено (текстовый поток text/event-stream)"),
            @ApiResponse(responseCode = "401", description = "Отсутствует или невалиден JWT-токен")
    })
    public SseEmitter subscribe(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = userService.getUserProfileByEmail(userDetails.getUsername()).getId();
        return sseSubscriptionManager.subscribe(userId);
    }
}