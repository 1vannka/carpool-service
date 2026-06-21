package com.carpool.controller.auth;

import com.carpool.controller.dto.auth.AuthResponse;
import com.carpool.controller.dto.auth.LoginRequest;
import com.carpool.controller.dto.auth.RegisterRequest;
import com.carpool.controller.dto.auth.RefreshRequest;
import com.carpool.domain.model.auth.TokenPair;
import com.carpool.domain.model.user.User;
import com.carpool.domain.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "Аутентификация, регистрация и управление JWT токенами")
public class AuthController {

    private final AuthService authService;
    private final AuthWebMapper authWebMapper;

    public AuthController(AuthService authService, AuthWebMapper authWebMapper) {
        this.authService = authService;
        this.authWebMapper = authWebMapper;
    }

    @PostMapping("/register")
    @Operation(summary = "Регистрация", description = "Создает нового пользователя и возвращает пару JWT токенов")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователь успешно зарегистрирован"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Ошибка валидации данных",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"error\": \"Ошибка валидации данных\"}"))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Пользователь с таким email уже существует",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"error\": \"Пользователь уже существует\"}"))
            )
    })
    @SecurityRequirements()
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        User user = authWebMapper.toDomain(request);
        TokenPair tokenPair = authService.register(user, request.password());

        return ResponseEntity.ok(new AuthResponse(tokenPair.accessToken(), tokenPair.refreshToken()));
    }

    @PostMapping("/login")
    @Operation(summary = "Вход (Login)", description = "Аутентификация пользователя по email и паролю")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Успешный вход, токены выданы"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Ошибка валидации данных",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"error\": \"Ошибка валидации данных\"}"))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Неверное имя или пароль",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"error\": \"Неверное имя или пароль\"}"))
            )
    })
    @SecurityRequirements()
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        TokenPair tokenPair = authService.login(request.email(), request.password());

        return ResponseEntity.ok(new AuthResponse(tokenPair.accessToken(), tokenPair.refreshToken()));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Обновление токена", description = "Выдает новую пару access/refresh токенов по действующему refresh токену")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Токены успешно обновлены"),
            @ApiResponse(responseCode = "400", description = "Отсутствует refresh токен в теле запроса",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"error\": \"Отсутствует refresh токен\"}"))),
            @ApiResponse(responseCode = "401", description = "Refresh токен недействителен",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"error\": \"Refresh токен недействителен\"}")))
    })
    @SecurityRequirements()
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        TokenPair tokenPair = authService.refreshTokens(request.refreshToken());

        return ResponseEntity.ok(new AuthResponse(tokenPair.accessToken(), tokenPair.refreshToken()));
    }

    @PostMapping("/logout")
    @Operation(summary = "Выход (Logout)", description = "Удаляет (отзывает) конкретный refresh токен из базы данных. Требует авторизации.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Успешный выход", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "400", description = "Отсутствует refresh токен в теле запроса",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"error\": \"Отсутствует refresh токен\"}"))),
            @ApiResponse(responseCode = "401", description = "Не авторизован",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"error\": \"Не авторизован\"}")))
    })
    public ResponseEntity<Void> logout(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody RefreshRequest request) {

        Long userId = authService.getUserIdByEmail(userDetails.getUsername());
        authService.logout(userId, request.refreshToken());

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/logout-all")
    @Operation(summary = "Выйти со всех устройств", description = "Отзывает все refresh токены текущего пользователя (сброс всех сессий). Требует авторизации.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Успешный выход со всех устройств",content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "Не авторизован",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"error\": \"Не авторизован\"}")))
    })
    public ResponseEntity<Void> logoutAll(@AuthenticationPrincipal UserDetails userDetails) {

        Long userId = authService.getUserIdByEmail(userDetails.getUsername());
        authService.logoutAll(userId);

        return ResponseEntity.noContent().build();
    }
}