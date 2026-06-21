package com.carpool.controller.user;

import com.carpool.controller.dto.user.UpdateProfileRequest;
import com.carpool.controller.dto.user.UserProfileResponse;
import com.carpool.domain.model.user.User;
import com.carpool.domain.service.UserService;
import com.carpool.infrastructure.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@Tag(name = "Profile", description = "Управление профилем пользователя")
public class ProfileController {

    private final UserService userService;
    private final UserWebMapper userWebMapper;

    public ProfileController(UserService userService, UserWebMapper userWebMapper) {
        this.userService = userService;
        this.userWebMapper = userWebMapper;
    }

    @GetMapping
    @Operation(summary = "Получить профиль", description = "Возвращает данные текущего авторизованного пользователя")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Профиль получен"),
            @ApiResponse(responseCode = "401", description = "Не авторизован",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"error\": \"Не авторизован\"}"))),
            @ApiResponse(responseCode = "403", description = "Нет прав для доступа к профилю",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"error\": \"Нет прав для доступа к профилю\"}"))),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"error\": \"Пользователь не найдена\"}")))
    })
    public ResponseEntity<UserProfileResponse> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getUserProfileByEmail(userDetails.getUsername());
        return ResponseEntity.ok(userWebMapper.toDto(user));
    }

    @PutMapping
    @Operation(summary = "Обновить профиль", description = "Обновляет ссылки на социальные сети")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Профиль успешно обновлен"),
            @ApiResponse(responseCode = "400", description = "Невалидные данные",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"error\": \"Ошибка валидации данных\"}"))),
            @ApiResponse(responseCode = "401", description = "Не авторизован",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"error\": \"Не авторизован\"}")))
    })
    public ResponseEntity<UserProfileResponse> updateProfile(
            @Valid @RequestBody UpdateProfileRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long userId = userService.getUserProfileByEmail(userDetails.getUsername()).getId();

        User updatedUser = userService.updateSocialAliases(
                userId,
                request.telegramAlias(),
                request.vkAlias()
        );

        return ResponseEntity.ok(userWebMapper.toDto(updatedUser));
    }
}